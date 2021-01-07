package ru.plasticalbe.msnewsdisplay;

import com.google.common.base.Strings;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ru.plasticalbe.msnewsdisplay.posts.PostCheckerTask;
import ru.plasticalbe.msnewsdisplay.posts.WallPost;
import ru.plasticalbe.msnewsdisplay.posts.fetcher.PostFetcher;
import ru.plasticalbe.msnewsdisplay.posts.fetcher.SimplePostFetcher;
import ru.plasticalbe.msnewsdisplay.storage.SeenPostsMVStorage;
import ru.plasticalbe.msnewsdisplay.storage.SeenPostsStorage;

import java.io.File;

@Slf4j
public final class MSNewsDisplayPlugin extends JavaPlugin implements Listener {
    private SeenPostsStorage seenPostsStorage;

    @Setter
    private ItemStack postBook;

    @Setter
    private WallPost lastPost;

    @Override
    @SneakyThrows
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        String serviceAccessToken = config.getString("vk.service-access-token");

        if (Strings.isNullOrEmpty(serviceAccessToken)) {
            log.error("Fill service access token field in the config");

            setEnabled(false);
            return;
        }

        PostFetcher postFetcher = new SimplePostFetcher(serviceAccessToken, config.getInt("vk.group-id"));

        try {
            postFetcher.get();
        } catch (Exception e) {
            log.error("Could not make a test post fetch, probably a misconfiguration");
            e.printStackTrace();

            setEnabled(false);
            return;
        }

        getServer().getScheduler().runTaskTimerAsynchronously(
                this,
                new PostCheckerTask(this, postFetcher),
                0,
                config.getInt("vk.posts-check-delay-seconds") * 20L
        );

        seenPostsStorage = new SeenPostsMVStorage(new File(getDataFolder(), "seen_posts.dat"));

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        if (seenPostsStorage != null) {
            seenPostsStorage.handleExit();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (lastPost == null || postBook == null) {
            return;
        }

        Player player = event.getPlayer();
        if (seenPostsStorage.getLastSeenPostId(player.getUniqueId()) < lastPost.getId()) {
            seenPostsStorage.setLastSeenPostId(player.getUniqueId(), lastPost.getId());
            player.openBook(postBook);
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (postBook != null) {
                ((Player) sender).openBook(postBook);
            } else {
                sender.sendMessage(ChatColor.GOLD + "There is no news available");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command is available only for players");
        }

        return true;
    }
}
