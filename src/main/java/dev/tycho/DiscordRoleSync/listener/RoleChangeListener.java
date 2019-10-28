package dev.tycho.DiscordRoleSync.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.stmt.QueryBuilder;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.manager.UserManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoleChangeListener extends ListenerAdapter {
    @Override
    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
//        DiscordRoleSync.newChain().asyncFirst(() -> {
//            String command = "";
//            outerLoop:
//            for (Role role: event.getRoles()) {
//                for(int i = 0; i < DiscordRoleSync.roles.size(); i++) {
//                    if(role.getId().equals(DiscordRoleSync.roles.get(i))) {
//
//                        List<Link> linkList = null;
//                        try {
//                            Member member =  event.getMember();
//                            linkList = DiscordRoleSync.linkDao.queryForEq("discordId", member.getId());
//
//                            if(linkList.size() > 0) {
//                                command = "lp user " + Bukkit.getOfflinePlayer(linkList.get(0).getUuid()).getName() + " parent set " + DiscordRoleSync.roles.get(i - 1);
//                            }
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        break outerLoop;
//                    }
//                }
//            }
//            return command;
//        }).sync(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command)).execute();
    }
}