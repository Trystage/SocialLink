package win.trystage.sociallink.database;

import java.util.UUID;
import win.trystage.sociallink.models.PlayerData;

public interface Database {
    void connect();
    void disconnect();
    void createTableIfNotExists();
    void savePlayerData(PlayerData playerData);
    PlayerData getPlayerData(UUID uuid);
    PlayerData getPlayerData(String username);
    void updateAccount(UUID uuid, String accountType, String accountId);
}
