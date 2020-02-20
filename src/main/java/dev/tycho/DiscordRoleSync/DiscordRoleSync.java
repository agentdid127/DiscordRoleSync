package dev.tycho.DiscordRoleSync;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import dev.tycho.DiscordRoleSync.listener.AccountLinkingListener;
import dev.tycho.DiscordRoleSync.listener.PlayerJoinedListener;
import dev.tycho.DiscordRoleSync.listener.RoleChangeListener;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class DiscordRoleSync extends JavaPlugin {

    public static LuckPerms permsApi;
    public static JDA jda;
    public static ConfigurationSection roles;

    private static TaskChainFactory taskChainFactory;
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static HashMap<String, UUID> linkQueue = new HashMap<>();

    @Override
    public void onEnable() {
        taskChainFactory = BukkitTaskChainFactory.create(this);
        this.saveDefaultConfig();

        roles = getConfig().getConfigurationSection("roles");
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            permsApi = provider.getProvider();

        }
        checkForJda();
        DiscordSRV.api.subscribe(new AccountLinkingListener(this));
    }

    // Run once a second to wait for DiscordSRV to be ready so we can grab an instance from it.
    private void checkForJda() throws NullPointerException {
        if (jda != null) return;
        if (!DiscordSRV.isReady) {
            Bukkit.getScheduler().runTaskLater(this, this::checkForJda, 20);
            return;
        }
        jda = DiscordSRV.getPlugin().getJda();
        if (jda == null) {
            Bukkit.getScheduler().runTaskLater(this, this::checkForJda, 20);
            return;
        }
        jda.addEventListener(new RoleChangeListener(this));
        getServer().getPluginManager().registerEvents(new PlayerJoinedListener(this), this);
        getLogger().info("DiscordRoleSync ready!");
    }

    public void syncRoles() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        for (Player player : players) syncRoles(player.getUniqueId());
    }

    public void syncRoles(UUID playerId) {
        syncRoles(Objects.requireNonNull(getServer().getPlayer(playerId)));
    }

    public void syncRoles(Player player) {
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
        if (discordId == null) {
            getLogger().info("User " + player.getUniqueId() + " not linked! Skipping..");
            return;
        }
        User user = permsApi.getUserManager().getUser(player.getUniqueId());
        Guild guild = jda.getGuildById(Objects.requireNonNull(getConfig().getString("primaryServerId")));
        Member guildMember = Objects.requireNonNull(guild).getMemberById(discordId);
        for (Role role : Objects.requireNonNull(guildMember).getRoles()) {
            if (!roles.isSet(role.getId())) {
                getLogger().info("Role " + role.getName() + " has no assigned group - skipping");
                continue;
            }
            String groupName = roles.getString(role.getId());
            String groupPermission = "group." + groupName;
            if (player.hasPermission(groupPermission)) {
                getLogger().info("User already has group " + groupName + " for role " + role.getName() + " - skipping");
                continue;
            }
            Node groupNode = Node.builder(groupPermission).build();
            user.data().add(groupNode);
            permsApi.getUserManager().saveUser(user);
            getLogger().info("Adding permissions group " + groupName + " to " + player.getName() +
                    " for discord role " + role.getName());
            player.sendMessage(ChatColor.GREEN + "[DiscordRoleSync] You are now a member of the " +
                    ChatColor.BLUE + groupName + ChatColor.GREEN + " group for being in the discord role " +
                    ChatColor.BLUE + role.getName());
        }
    }

    @Override
    public void onDisable() {

    }
}
