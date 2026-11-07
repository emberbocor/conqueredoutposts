package io.github.emberbocor.conqueredoutposts.claim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.LongPredicate;
import java.util.stream.LongStream;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.emberbocor.conqueredoutposts.ConqueredOutposts;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

final class ClaimData extends SavedData {
    private static final Codec<ClaimData> CODEC = Entry.CODEC.listOf().fieldOf("claims").codec()
            .xmap(ClaimData::fromEntries, ClaimData::entries);
    // Vanilla needs a data fix type; this one has no fixes for 1.21.1 or newer data.
    private static final SavedDataType<ClaimData> TYPE =
            new SavedDataType<>(ConqueredOutposts.MODID, ClaimData::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

    private final Map<Key, Claim> claims = new HashMap<>();

    static ClaimData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
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

    private List<Entry> entries() {
        List<Entry> entries = new ArrayList<>();
        claims.forEach((key, claim) -> entries.add(new Entry(key.structure(), key.chunk(), claim.box(), claim.banners().toLongArray())));
        return entries;
    }

    private static ClaimData fromEntries(List<Entry> entries) {
        ClaimData data = new ClaimData();
        for (Entry entry : entries) {
            if (entry.banners().length > 0) {
                data.claims.put(new Key(entry.structure(), entry.chunk()), new Claim(entry.box(), new LongOpenHashSet(entry.banners())));
            }
        }
        return data;
    }

    record Key(Identifier structure, long chunk) {
    }

    record Claim(BoundingBox box, LongSet banners) {
    }

    /** One stored claim, with the same tags as the 1.21.1 format (structure, chunk, box, banners). */
    private record Entry(Identifier structure, long chunk, BoundingBox box, long[] banners) {
        static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("structure").forGetter(Entry::structure),
                Codec.LONG.fieldOf("chunk").forGetter(Entry::chunk),
                BoundingBox.CODEC.fieldOf("box").forGetter(Entry::box),
                Codec.LONG_STREAM.xmap(LongStream::toArray, LongStream::of).fieldOf("banners").forGetter(Entry::banners))
                .apply(instance, Entry::new));
    }
}
