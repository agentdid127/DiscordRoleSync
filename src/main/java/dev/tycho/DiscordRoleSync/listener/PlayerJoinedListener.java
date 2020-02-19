package dev.tycho.DiscordRoleSync.listener;

import dev.tycho.DiscordRoleSync.DiscordRoleSync;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinedListener implements Listener {
    private DiscordRoleSync plugin;
    public PlayerJoinedListener(DiscordRoleSync plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.syncRoles(event.getPlayer());
    }
}
