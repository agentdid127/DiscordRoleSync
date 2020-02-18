package dev.tycho.DiscordRoleSync.listener;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

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