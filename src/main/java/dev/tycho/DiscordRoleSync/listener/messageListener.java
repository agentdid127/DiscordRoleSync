package dev.tycho.DiscordRoleSync.listener;

import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.DiscordRoleSync.DiscordRoleSync;
import dev.tycho.DiscordRoleSync.database.Link;

import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;

public class messageListener extends ListenerAdapter {

    private JavaPlugin plugin;

    public messageListener(JavaPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(!event.getChannel().getId().equals(plugin.getConfig().getString("linkingChannel"))) {
            return;
        }

        String message = event.getMessage().getContentRaw();

        if(!message.startsWith("!link")) {
            return;
        }

        int spaceIndex = message.indexOf(" ");
        if(!message.contains(" ")) {
            event.getChannel().sendMessage("Correct usage: !link <username>").queue();
            return;
        }

        String providedName = message.substring(spaceIndex + 1);
        if(providedName.contains(" ")) {
            event.getChannel().sendMessage("Correct usage: !link <username>").queue();
            return;
        }

        Player player = Bukkit.getPlayer(providedName);

        if(player == null) {
            MessageBuilder messageBuilder = new MessageBuilder();

            messageBuilder.append("You must be online on the MC server to link your account!", MessageBuilder.Formatting.BOLD);
            messageBuilder.append(" Join the server (IP in ");
            messageBuilder.append(event.getGuild().getTextChannelById(plugin.getConfig().getString("serverInfoChannel")).getAsMention());
            messageBuilder.append(" ) and link here in this channel");

            event.getChannel().sendMessage(messageBuilder.build()).queue();
            return;
        }

        DiscordRoleSync.newChain().async(() -> {
            try {
                List<Link> links = DiscordRoleSync.linkDao.queryBuilder().where().eq("discordId", event.getMessage().getAuthor().getId()).query();

                if(links.size() > 0) {
                    event.getChannel().sendMessage("You have already linked to an account!").queue();
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            DiscordRoleSync.linkQueue.put(event.getMessage().getAuthor().getAsTag(), player.getUniqueId());

            event.getChannel().sendMessage("Perform this command ingame to finish linking: /dc link " + event.getMessage().getAuthor().getAsTag()).queue();

            player.sendMessage(ChatColor.GREEN + "Discord user " + ChatColor.AQUA + event.getMessage().getAuthor().getAsTag() + ChatColor.GREEN + " tried to link with your account. If this was you type:" + ChatColor.AQUA + " /dc link " + event.getMessage().getAuthor().getAsTag());
        }).execute();
    }
}
