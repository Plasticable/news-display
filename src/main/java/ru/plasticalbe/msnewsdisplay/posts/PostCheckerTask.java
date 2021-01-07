package ru.plasticalbe.msnewsdisplay.posts;

import com.google.common.base.Splitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import ru.plasticalbe.msnewsdisplay.MSNewsDisplayPlugin;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class PostCheckerTask implements Runnable {
    private final MSNewsDisplayPlugin plugin;
    private final Supplier<WallPost> postSupplier;

    @Override
    public void run() {
        try {
            WallPost post = postSupplier.get();
            if (post != null) {
                plugin.setLastPost(post);
                plugin.setPostBook(createBook(post.getText()));
            }
        } catch (Exception e) {
            log.error("Could not get wall post", e);
        }
    }

    private ItemStack createBook(String text) {
        ItemStack bookStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) bookStack.getItemMeta();

        bookMeta.setTitle("Should not be empty");
        Splitter.fixedLength(255).split(text).forEach(bookMeta::addPage);
        bookMeta.setAuthor("Plasticable");

        bookStack.setItemMeta(bookMeta);

        return bookStack;
    }
}
