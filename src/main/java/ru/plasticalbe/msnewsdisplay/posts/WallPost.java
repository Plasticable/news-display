package ru.plasticalbe.msnewsdisplay.posts;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

@Value
public class WallPost {
    int id;

    String text;

    @Getter(AccessLevel.PRIVATE)
    @SerializedName("is_pinned")
    Integer isPinnedInt;

    public boolean isPinned() {
        return isPinnedInt != null;
    }
}
