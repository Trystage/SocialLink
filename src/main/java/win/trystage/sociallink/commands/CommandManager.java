package win.trystage.sociallink.commands;

import org.bukkit.command.PluginCommand;
import win.trystage.sociallink.SocialLink;

public class CommandManager {
    private final SocialLink plugin;

    public CommandManager(SocialLink plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        PluginCommand linkAccountCommand = plugin.getCommand("linkaccount");
        if (linkAccountCommand != null) {
            linkAccountCommand.setExecutor(new LinkAccountCommand(plugin));
        }
    }
}