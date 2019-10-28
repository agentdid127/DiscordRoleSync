package dev.tycho.DiscordRoleSync;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.tycho.DiscordRoleSync.command.CommandDc;
import dev.tycho.DiscordRoleSync.database.Link;
import dev.tycho.DiscordRoleSync.listener.RoleChangeListener;
import dev.tycho.DiscordRoleSync.listener.messageListener;
import me.lucko.luckperms.api.LuckPermsApi;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DiscordRoleSync extends JavaPlugin {

    public static LuckPermsApi permsApi;
    public static JDA jda;
    static List<String> roles;

    private static TaskChainFactory taskChainFactory;
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static Dao<Link, Integer> linkDao;

    public static HashMap<String, UUID> linkQueue = new HashMap<>();

    @Override
    public void onEnable() {
        taskChainFactory = BukkitTaskChainFactory.create(this);
        this.saveDefaultConfig();

        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");

        String host = getConfig().getString("mysql.host");
        String port = getConfig().getString("mysql.port");
        String database = getConfig().getString("mysql.database");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");
        String useSsl = getConfig().getString("mysql.ssl");

        String databaseUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT&useSSL=" + useSsl;

        try {
            ConnectionSource connectionSource = new JdbcPooledConnectionSource(databaseUrl, username, password);

            linkDao = DaoManager.createDao(connectionSource, Link.class);
            TableUtils.createTableIfNotExists(connectionSource, Link.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        roles = getConfig().getStringList("roles");

        RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
        if (provider != null) {
            permsApi = provider.getProvider();

        }

        try {
             jda = new JDABuilder(this.getConfig().getString("token"))
                    //.addEventListeners(new RoleChangeListener())
                     .addEventListeners(new messageListener(this))
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        this.getCommand("dc").setExecutor(new CommandDc(this));

    }
    @Override
    public void onDisable() {

    }
}
