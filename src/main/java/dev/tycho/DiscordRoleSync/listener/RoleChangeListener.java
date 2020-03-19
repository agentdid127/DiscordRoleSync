package dev.tycho.DiscordRoleSync.listener;

import dev.tycho.DiscordRoleSync.DiscordRoleSync;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.events.Event;
import github.scarsz.discordsrv.dependencies.jda.api.events.GenericEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.EventListener;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public class RoleChangeListener extends ListenerAdapter implements EventListener {
    private final DiscordRoleSync plugin;

    public RoleChangeListener(DiscordRoleSync plugin) {
        plugin.getLogger().info("Role change listener initialized");
        this.plugin = plugin;
    }
    @Override
    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
        plugin.getLogger().info("Guild member " + event.getMember().getUser().getName() + " got " +
                "a role update - resyncing groups...");
        UUID playerId = DiscordSRV.getPlugin()
                                .getAccountLinkManager()
                                .getUuid(event.getMember().getId());
        plugin.syncRoles(playerId);
    }

@Override
    public void onGuildMemberRoleRemove(@Nonnull GuildMemberRoleRemoveEvent event) {
    plugin.getLogger().info("Guild member " + event.getMember().getUser().getName() + " got " +
            "a role update - resyncing groups...");
    UUID playerId = DiscordSRV.getPlugin()
            .getAccountLinkManager()
            .getUuid(event.getMember().getId());
    plugin.unsyncRoles(playerId);
}

}