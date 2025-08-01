package win.trystage.sociallink.database;

import win.trystage.sociallink.models.PlayerData;

import java.sql.*;
import java.util.UUID;

public class MysqlDatabase implements Database {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private Connection connection;

    public MysqlDatabase(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect() {
        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS player_accounts (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "username VARCHAR(16), " +
                "qq VARCHAR(20), " +
                "bilibili VARCHAR(50), " +
                "douyin VARCHAR(50), " +
                "hypixel VARCHAR(50)" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePlayerData(PlayerData playerData) {
        String sql = "INSERT INTO player_accounts (uuid, username, qq, bilibili, douyin, hypixel) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "username = VALUES(username), " +
                "qq = VALUES(qq), " +
                "bilibili = VALUES(bilibili), " +
                "douyin = VALUES(douyin), " +
                "hypixel = VALUES(hypixel)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerData.getUuid().toString());
            stmt.setString(2, playerData.getUsername());
            stmt.setString(3, playerData.getQq());
            stmt.setString(4, playerData.getBilibili());
            stmt.setString(5, playerData.getDouyin());
            stmt.setString(6, playerData.getHypixel());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        String sql = "SELECT * FROM player_accounts WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PlayerData(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("username"),
                        rs.getString("qq"),
                        rs.getString("bilibili"),
                        rs.getString("douyin"),
                        rs.getString("hypixel")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PlayerData getPlayerData(String username) {
        String sql = "SELECT * FROM player_accounts WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PlayerData(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("username"),
                        rs.getString("qq"),
                        rs.getString("bilibili"),
                        rs.getString("douyin"),
                        rs.getString("hypixel")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateAccount(UUID uuid, String accountType, String accountId) {
        String sql = "UPDATE player_accounts SET " + accountType + " = ? WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountId);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}