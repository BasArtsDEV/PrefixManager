package dev.basarts.prefixmanager;

import dev.basarts.prefixmanager.Listeners.Listeners;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault2.chat.ChatUnlocked;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main instance;
    private static Chat chat = null;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (setupChat()) {
            getLogger().info("§aSuccessfully hooked into ChatUnlocked!");
            Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        } else {
            getLogger().severe("No Chat service found! Ensure a permission plugin (like LuckPerms) is handling Vault.");
        }
    }

    private boolean setupChat() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;

        // On cherche l'interface Chat classique (net.milkbowl.vault.chat.Chat)
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            chat = rsp.getProvider();
        }
        return chat != null;
    }

    public static Main getInstance() { return instance; }

    public Chat getChat() {
        return chat;
    }
}