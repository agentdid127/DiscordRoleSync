package dev.tycho.DiscordRoleSync.listener;

import dev.tycho.DiscordRoleSync.DiscordRoleSync;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;



public class AccountLinkingListener {
    private DiscordRoleSync plugin;
    public AccountLinkingListener(DiscordRoleSync plugin) {
        plugin.getLogger().info("Account link listener initialized");
        this.plugin = plugin;
    }

    @Subscribe
    public void onAccountLinked(AccountLinkedEvent ev) {
        plugin.getLogger().info("New Discord account linked - " + ev.getUser().getName());
        plugin.syncRoles(ev.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onAccountUnlinked(AccountUnlinkedEvent ev) {
        plugin.getLogger().info("Discord account unlinked - " + ev.getDiscordUser().getName());
        DiscordRoleSync.permsApi.getUserManager().loadUser(ev.getPlayer().getUniqueId())
                .thenAcceptAsync(user -> {
                    Set<String> groups = user.getNodes().stream()
                            .filter(NodeType.INHERITANCE::matches)
                            .map(NodeType.INHERITANCE::cast)
                            .map(InheritanceNode::getGroupName)
                            .collect(Collectors.toSet());
                   for (Object groupNameObj : DiscordRoleSync.roles.getValues(false).values()) {
                       String groupName = (String)groupNameObj;
                       if (groups.contains(groupName)) {
                           plugin.getLogger().info("Removing permissions group " + groupName +
                                   " from " + ev.getPlayer().getName() + " because they are being unlinked");
                           Player player = ev.getPlayer().getPlayer();
                           if (player != null) {
                               player.sendMessage(ChatColor.GREEN + "[DiscordRoleSync] You are no longer a member of the " +
                                       ChatColor.BLUE + groupName + ChatColor.GREEN + " group because your Discord " +
                                       "account was unlinked!");
                           }
                           user.data().remove(Node.builder("group." + groupName).build());
                       }
                   }
                   String unlinkedGroup = plugin.getConfig().getString("unlinkedGroupName");
                   if (unlinkedGroup != null)
                       user.data().add(Node.builder("group." + unlinkedGroup).build());
                   DiscordRoleSync.permsApi.getUserManager().saveUser(user);
                });
    }
}
