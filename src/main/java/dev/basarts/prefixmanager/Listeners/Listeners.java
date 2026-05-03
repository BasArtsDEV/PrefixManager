package dev.basarts.prefixmanager.Listeners;

import dev.basarts.prefixmanager.Main;
import dev.basarts.prefixmanager.Utils.MessageUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
    private final Main plugin = Main.getInstance();

    private String getVaultPrefix(Player player) {
        if (plugin.getChat() == null) return "";
        String prefix = plugin.getChat().getPlayerPrefix(player);
        return prefix != null ? prefix : "";
    }

    private String getVaultSuffix(Player player) {
        if (plugin.getChat() == null) return "";
        String suffix = plugin.getChat().getPlayerSuffix(player);
        return suffix != null ? suffix : "";
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        String configFormat = plugin.getConfig().getString("chat.format", "{PREFIX}{PLAYER}{SUFFIX}: {MESSAGE}");

        String prefix = getVaultPrefix(player);
        String suffix = getVaultSuffix(player);
        String message = MessageUtils.LEGACY.serialize(event.message());

        String formatted = MessageUtils.translateAlternateColorCodes('&', configFormat)
                .replace("{PREFIX}", MessageUtils.translateAlternateColorCodes('&', prefix))
                .replace("{SUFFIX}", MessageUtils.translateAlternateColorCodes('&', suffix))
                .replace("{PLAYER}", player.getName())
                .replace("{MESSAGE}", message);

        Bukkit.broadcast(MessageUtils.LEGACY.deserialize(MessageUtils.format(formatted)));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String format = plugin.getConfig().getString("messages.join", "&a+ &7{PLAYER}");
        event.joinMessage(MessageUtils.LEGACY.deserialize(MessageUtils.format(replacePlaceholders(format, event.getPlayer(), 0))));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String format = plugin.getConfig().getString("messages.quit", "&c- &7{PLAYER}");
        event.quitMessage(MessageUtils.LEGACY.deserialize(MessageUtils.format(replacePlaceholders(format, event.getPlayer(), -1))));
    }

    private String replacePlaceholders(String format, Player player, int offset) {
        int onlineCount = Bukkit.getOnlinePlayers().size() + offset;

        return MessageUtils.translateAlternateColorCodes('&', format)
                .replace("{PREFIX}", MessageUtils.translateAlternateColorCodes('&', getVaultPrefix(player)))
                .replace("{SUFFIX}", MessageUtils.translateAlternateColorCodes('&', getVaultSuffix(player)))
                .replace("{PLAYER}", player.getName())
                .replace("{ONLINE}", String.valueOf(onlineCount))
                .replace("{MAX}", String.valueOf(Bukkit.getServer().getMaxPlayers()));
    }
}