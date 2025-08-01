package win.trystage.sociallink.models;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final String username;
    private String qq;
    private String bilibili;
    private String douyin;
    private String hypixel;

    public PlayerData(UUID uuid, String username, String qq, String bilibili, String douyin, String hypixel) {
        this.uuid = uuid;
        this.username = username;
        this.qq = qq;
        this.bilibili = bilibili;
        this.douyin = douyin;
        this.hypixel = hypixel;
    }

    // Getters and Setters
    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }
    public String getQq() { return qq; }
    public void setQq(String qq) { this.qq = qq; }
    public String getBilibili() { return bilibili; }
    public void setBilibili(String bilibili) { this.bilibili = bilibili; }
    public String getDouyin() { return douyin; }
    public void setDouyin(String douyin) { this.douyin = douyin; }
    public String getHypixel() { return hypixel; }
    public void setHypixel(String hypixel) { this.hypixel = hypixel; }
}
