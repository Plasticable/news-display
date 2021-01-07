package ru.plasticalbe.msnewsdisplay.storage;

import java.util.UUID;

public interface SeenPostsStorage {
    void setLastSeenPostId(UUID playerId, int postId);

    int getLastSeenPostId(UUID playerId);

    void handleExit();
}
