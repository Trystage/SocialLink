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
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
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
        try {
            ensureConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
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
        try {
            ensureConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
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
        try {
            ensureConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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
        try {
            ensureConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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
        try {
            ensureConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        String sql = "UPDATE player_accounts SET " + accountType + " = ? WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountId);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查当前连接是否有效
     */
    private boolean isConnectionValid() {
        try {
            if (connection == null || connection.isClosed()) return false;
            // 执行一个轻量级查询测试连接
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1");
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * 确保连接有效，若无效则重新建立
     */
    private void ensureConnection() throws SQLException {
        if (!isConnectionValid()) {
            if (connection != null) {
                try { connection.close(); } catch (SQLException ignored) {}
            }
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
            connection = DriverManager.getConnection(url, username, password);
        }
    }
}