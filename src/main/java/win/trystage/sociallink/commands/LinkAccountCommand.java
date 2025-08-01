package win.trystage.sociallink.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import win.trystage.sociallink.SocialLink;
import win.trystage.sociallink.models.PlayerData;

import java.util.HashMap;
import java.util.Map;

public class LinkAccountCommand implements CommandExecutor {
    private final SocialLink plugin;
    private final Map<Player, String> pendingAccountTypes = new HashMap<>();

    public LinkAccountCommand(SocialLink plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家才能执行此命令!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage("§c用法: /linkaccount <qq/bilibili/douyin/hypixel> [账号名]");
            return true;
        }

        String accountType = args[0].toLowerCase();

        // 验证账号类型
        if (!accountType.equals("qq") && !accountType.equals("bilibili") &&
                !accountType.equals("douyin") && !accountType.equals("hypixel")) {
            sender.sendMessage("§c无效的账号类型! 可用类型: qq, bilibili, douyin, hypixel");
            return true;
        }

        // 如果没有提供账号ID，启动对话流程
        if (args.length < 2) {

            String message = "§a请输入要绑定的" + accountType + "账号: ";
            switch (accountType) {
                case "qq":
                    message += "(纯数字)";
                    break;
                case "bilibili":
                    message += "(完整个人空间链接，例如: https://space.bilibili.com/1478748996)";
                    break;
                case "douyin":
                    message += "(抖音号，例如: TrystageBedwars)";
                    break;
                case "hypixel":
                    message += "(MC IGN，只能包含字母和下划线)";
                    break;
            }

            player.sendMessage(message);

            pendingAccountTypes.put(player, accountType);

            ConversationFactory factory = new ConversationFactory(plugin)
                    .withFirstPrompt(new AccountIdPrompt())
                    .withLocalEcho(false)
                    .withTimeout(30)
                    .thatExcludesNonPlayersWithMessage("只有玩家可以使用此命令")
                    .addConversationAbandonedListener(event -> {
                        if (!event.gracefulExit()) {
                            player.sendMessage("§c账号绑定已取消");
                        }
                        pendingAccountTypes.remove(player);
                    });

            Conversation conv = factory.buildConversation(player);
            conv.begin();
            return true;
        }

        // 如果提供了账号ID，直接处理
        String accountId = args[1];
        processAccountBinding(player, accountType, accountId);
        return true;
    }

    private void processAccountBinding(Player player, String accountType, String accountId) {
        // 在主线程执行绑定逻辑
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            // 验证账号格式
            switch (accountType) {
                case "qq":
                    if (!accountId.matches("\\d+")) {
                        player.sendMessage("§cQQ号必须为纯数字，请重新输入正确的QQ号");
                        return;
                    }
                    break;
                case "bilibili":
                    if (!accountId.matches("https://space\\.bilibili\\.com/\\d+")) {
                        player.sendMessage("§cB站账号格式不正确，请输入完整的个人空间链接，例如: https://space.bilibili.com/1478748996");
                        return;
                    }
                    break;
                case "douyin":
                    if (accountId.contains("http") || accountId.contains("/")) {
                        player.sendMessage("§c请输入抖音号而不是链接，例如: TrystageBedwars");
                        return;
                    }
                    break;
                case "hypixel":
                    if (!accountId.matches("[0-9a-zA-Z_]+")) {
                        player.sendMessage("§cHypixel账号只能包含字母(a-z, A-Z)和下划线(_)，请输入MC IGN");
                        return;
                    }
                    break;
            }

            // 获取或创建玩家数据
            PlayerData playerData = plugin.getDatabase().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                playerData = new PlayerData(
                        player.getUniqueId(),
                        player.getName(),
                        null, null, null, null
                );
            }

            // 更新对应账号
            switch (accountType) {
                case "qq":
                    playerData.setQq(accountId);
                    break;
                case "bilibili":
                    playerData.setBilibili(accountId);
                    break;
                case "douyin":
                    playerData.setDouyin(accountId);
                    break;
                case "hypixel":
                    playerData.setHypixel(accountId);
                    break;
            }

            // 保存到数据库
            plugin.getDatabase().savePlayerData(playerData);
            player.sendMessage(ChatColor.GREEN + "成功绑定" + accountType + "账号: " + accountId);
        });
    }

    private class AccountIdPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ""; // 已经在上面的begin()前发送了提示信息
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            Player player = (Player) context.getForWhom();
            String accountType = pendingAccountTypes.get(player);

            if (accountType != null) {
                processAccountBinding(player, accountType, input);
            }

            return END_OF_CONVERSATION;
        }
    }
}