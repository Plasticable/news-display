package ru.plasticalbe.msnewsdisplay.storage;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SeenPostsMVStorage implements SeenPostsStorage {
    private final LoadingCache<SeenPostsMVStorage, MVStore> storeCache;

    public SeenPostsMVStorage(File storageFile) {
        storeCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .removalListener(listener -> ((MVStore) listener.getValue()).close())
                .build(new CacheLoader<SeenPostsMVStorage, MVStore>() {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public MVStore load(SeenPostsMVStorage key) {
                        return MVStore.open(storageFile.getAbsolutePath());
                    }
                });
    }

    @Override
    public int getLastSeenPostId(UUID playerId) {
        return getSeenPostsMap().getOrDefault(playerId, 0);
    }

    @Override
    public void setLastSeenPostId(UUID playerId, int postId) {
        getSeenPostsMap().put(playerId, postId);
    }

    @Override
    public void handleExit() {
        storeCache.invalidateAll();
    }

    private Map<UUID, Integer> getSeenPostsMap() {
        return storeCache.getUnchecked(this).openMap("seen-posts");
    }
}
