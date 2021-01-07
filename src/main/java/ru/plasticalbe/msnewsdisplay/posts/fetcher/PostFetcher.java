package ru.plasticalbe.msnewsdisplay.posts.fetcher;

import ru.plasticalbe.msnewsdisplay.posts.WallPost;

import java.util.function.Supplier;

public interface PostFetcher extends Supplier<WallPost> {
}
