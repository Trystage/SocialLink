package win.trystage.sociallink;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import win.trystage.sociallink.models.PlayerData;

public class SocialLinkExpansion extends PlaceholderExpansion {
    private final SocialLink plugin;

    public SocialLinkExpansion(SocialLink plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "media";
    }

    @Override
    public String getAuthor() {
        return "Trystage4C01";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) return null;

        PlayerData playerData = plugin.getDatabase().getPlayerData(player.getUniqueId());
        if (playerData == null) return "未绑定";

        switch (params.toLowerCase()) {
            case "qq":
                return playerData.getQq() != null ? playerData.getQq() : "未绑定";
            case "bilibili":
                return playerData.getBilibili() != null ? playerData.getBilibili() : "未绑定";
            case "douyin":
                return playerData.getDouyin() != null ? playerData.getDouyin() : "未绑定";
            case "hypixel":
                return playerData.getHypixel() != null ? playerData.getHypixel() : "未绑定";
            default:
                return null;
        }
    }
}
