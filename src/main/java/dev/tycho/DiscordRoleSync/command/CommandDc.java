package dev.tycho.DiscordRoleSync.command;

import dev.tycho.DiscordRoleSync.DiscordRoleSync;
import dev.tycho.DiscordRoleSync.database.Link;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class CommandDc implements CommandExecutor {

    private JavaPlugin plugin;

    public CommandDc(JavaPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DiscordRoleSync.newChain().async(() -> {
            if(!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command may only be executed by a player!");
                return;
            }

            Player player = (Player) sender;

            if(!(args.length > 1)) {
                player.sendMessage(ChatColor.RED + "Incorrect usage! Should be: /dc link <discordtag>");
                return;
            }

            String providedTag = args[1];
            for(int i = 2; i < args.length; i++) {
                providedTag = providedTag + " " + args[i];
            }

            if(!DiscordRoleSync.linkQueue.containsKey(providedTag)) {
                player.sendMessage(ChatColor.RED + "Discord user " + providedTag + " was not found trying to link with your mc account. Make sure you type !link <mcname> first in the #mc-linking channel!");
                return;
            }

            if(!DiscordRoleSync.linkQueue.get(providedTag).equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "That discord user did not try to link with your account!");
                return;
            }

            Link link = new Link(DiscordRoleSync.jda.getUserByTag(providedTag).getId(), player.getUniqueId());
            try {
                DiscordRoleSync.linkDao.create(link);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Node groupPermission = Node.builder("group." + plugin.getConfig().getString("linkGroup")).build();
            User user = DiscordRoleSync.permsApi.getUserManager().getUser(player.getUniqueId());
            user.data().add(groupPermission);
            DiscordRoleSync.permsApi.getUserManager().saveUser(user);

            DiscordRoleSync.linkQueue.remove(providedTag);

            player.sendMessage(ChatColor.GREEN + "Link successful!");
        }).execute();
        return true;
    }
}
