package ru.plasticalbe.msnewsdisplay.posts.fetcher;

import com.google.gson.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.plasticalbe.msnewsdisplay.posts.WallPost;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SimplePostFetcher implements PostFetcher {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static JsonParser jsonParser = new JsonParser();

    String accessToken;
    int groupId;

    @Override
    public WallPost get() {
        try (CloseableHttpClient httpClient = HttpClients.createMinimal()) {
            URI uri = new URIBuilder("https://api.vk.com/method/wall.get")
                    .addParameter("owner_id", String.valueOf(-groupId))
                    .addParameter("count", "2")
                    .addParameter("extended", "0")
                    .addParameter("access_token", accessToken)
                    .addParameter("v", "5.126")
                    .build();

            try (CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(uri))) {
                String responseRaw = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                log.debug(responseRaw);

                JsonObject response = jsonParser.parse(responseRaw).getAsJsonObject().getAsJsonObject("response");
                if (response.getAsJsonPrimitive("count").getAsInt() < 1) {
                    log.debug("No wall posts was found");
                    return null;
                }

                for (JsonElement postElement : response.getAsJsonArray("items")) {
                    WallPost post = gson.fromJson(postElement, WallPost.class);

                    if (!post.isPinned()) {
                        return post;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Could not fetch posts", e);
        }

        return null;
    }
}
