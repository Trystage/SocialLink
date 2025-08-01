package win.trystage.sociallink;

import org.bukkit.plugin.java.JavaPlugin;
import win.trystage.sociallink.commands.CommandManager;
import win.trystage.sociallink.database.Database;
import win.trystage.sociallink.database.MysqlDatabase;

public final class SocialLink extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {
        // 保存默认配置
        saveDefaultConfig();

        // 初始化数据库连接
        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String database = getConfig().getString("database.database");
        String username = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");

        this.database = new MysqlDatabase(host, port, database, username, password);
        this.database.connect();
        this.database.createTableIfNotExists();

        // 注册命令
        new CommandManager(this).registerCommands();

        // 注册PAPI扩展
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SocialLinkExpansion(this).register();
        }

        getLogger().info("SocialLink插件已启用!");
    }
    @Override
    public void onDisable() {
        if (database != null) {
            database.disconnect();
        }
        getLogger().info("SocialLink插件已禁用!");
    }

    public Database getDatabase() {
        return database;
    }
}
