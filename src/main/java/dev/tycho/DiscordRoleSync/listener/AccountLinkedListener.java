package dev.tycho.DiscordRoleSync.listener;

import dev.tycho.DiscordRoleSync.DiscordRoleSync;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;

import java.util.Objects;

public class AccountLinkedListener {
    private DiscordRoleSync plugin;
    public AccountLinkedListener(DiscordRoleSync plugin) {
        plugin.getLogger().info("Account link listener initialized");
        this.plugin = plugin;
    }

    @Subscribe
    public void onAccountLinked(AccountLinkedEvent ev) {
        plugin.getLogger().info("New Discord account linked - " + ev.getUser().getName());
        plugin.syncRoles(ev.getPlayer().getUniqueId());
    }
}
