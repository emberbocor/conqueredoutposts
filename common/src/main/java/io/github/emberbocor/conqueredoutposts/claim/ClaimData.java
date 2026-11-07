package io.github.emberbocor.conqueredoutposts.claim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.LongPredicate;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.conqueredoutposts.ConqueredOutposts;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;

final class ClaimData extends SavedData {
    private final Map<Key, Claim> claims = new HashMap<>();

    static ClaimData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ClaimData::load, ClaimData::new, ConqueredOutposts.MODID);
    }

    boolean isEmpty() {
        return claims.isEmpty();
    }

    @Nullable
    Claim get(Key key) {
        return claims.get(key);
    }

    boolean add(Key key, BoundingBox box, long banner) {
        Claim claim = claims.get(key);
        boolean claimed = claim == null;
        if (claimed) {
            claim = new Claim(box, new LongOpenHashSet());
            claims.put(key, claim);
        }
        if (claim.banners().add(banner)) {
            setDirty();
        }
        return claimed;
    }

    boolean removeBanners(Key key, LongPredicate missing) {
        Claim claim = claims.get(key);
        if (claim == null || !claim.banners().removeIf(missing)) {
            return false;
        }
        setDirty();
        if (claim.banners().isEmpty()) {
            claims.remove(key);
            return true;
        }
        return false;
    }

    List<Claim> removeBanner(long banner) {
        List<Claim> released = new ArrayList<>();
        Iterator<Claim> iterator = claims.values().iterator();
        while (iterator.hasNext()) {
            Claim claim = iterator.next();
            if (claim.banners().remove(banner)) {
                setDirty();
                if (claim.banners().isEmpty()) {
                    iterator.remove();
                    released.add(claim);
                }
            }
        }
        return released;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        claims.forEach((key, claim) -> {
            BoundingBox box = claim.box();
            CompoundTag entry = new CompoundTag();
            entry.putString("structure", key.structure().toString());
            entry.putLong("chunk", key.chunk());
            entry.putIntArray("box", new int[] {box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()});
            entry.putLongArray("banners", claim.banners().toLongArray());
            list.add(entry);
        });
        tag.put("claims", list);
        return tag;
    }

    private static ClaimData load(CompoundTag tag) {
        ClaimData data = new ClaimData();
        ListTag list = tag.getList("claims", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ResourceLocation structure = ResourceLocation.tryParse(entry.getString("structure"));
            int[] box = entry.getIntArray("box");
            long[] banners = entry.getLongArray("banners");
            if (structure != null && box.length == 6 && banners.length > 0) {
                data.claims.put(new Key(structure, entry.getLong("chunk")),
                        new Claim(new BoundingBox(box[0], box[1], box[2], box[3], box[4], box[5]), new LongOpenHashSet(banners)));
            }
        }
        return data;
    }

    record Key(ResourceLocation structure, long chunk) {
    }

    record Claim(BoundingBox box, LongSet banners) {
    }
}
