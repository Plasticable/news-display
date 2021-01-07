package ru.plasticalbe.msnewsdisplay;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.plasticalbe.msnewsdisplay.posts.WallPost;
import ru.plasticalbe.msnewsdisplay.posts.fetcher.PostFetcher;
import ru.plasticalbe.msnewsdisplay.posts.fetcher.SimplePostFetcher;

@Slf4j
class PostFetcherTest {
    PostFetcher postFetcher;

    @BeforeEach
    void setUp() {
        postFetcher = new SimplePostFetcher(
                System.getenv("ACCESS_TOKEN"),
                Integer.parseInt(System.getenv("GROUP_ID"))
        );
    }

    @Test
    void fetchLastPost() {
        WallPost post = postFetcher.get();

        Assertions.assertNotNull(post);
        log.debug(post.getText());
    }
}