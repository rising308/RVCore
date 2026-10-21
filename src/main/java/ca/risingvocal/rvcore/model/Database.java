package ca.risingvocal.rvcore.model;

import ca.risingvocal.rvcore.RVCore;
import ca.risingvocal.rvcore.util.RVConfig;
import ca.risingvocal.rvcore.util.ParticleData;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class Database {
    private static final Database instance = new Database();

    private final RVConfig settings;
    private Connection connection;

    public Database() {
        this.settings = RVConfig.getInstance();
    }

    public void load() {
        this.connect();
        this.createTables();
    }

    private void connect() {
        try {
            if (this.connection != null && !this.connection.isClosed())
                return;

            if (this.settings.getDatabaseType() == DatabaseType.MYSQL) {
                Class.forName("com.mysql.cj.jdbc.Driver");

                final String jdbcUrl = "jdbc:mysql://" + this.settings.getMysqlHost() + ":" + this.settings.getMysqlPort() + "/" + this.settings.getMysqlDb() + "?useSSL=false&autoReconnect=true";

                this.connection = DriverManager.getConnection(jdbcUrl, this.settings.getMysqlUser(), this.settings.getMysqlPass());
                RVCore.getInstance().logger.info("Connected to the MySQL database.");
            }
            else {
                Class.forName("org.sqlite.JDBC");

                final File file = new File(RVCore.getInstance().getDataFolder(), this.settings.getSqliteFilename());

                if (!file.exists())
                    file.createNewFile();

                final String jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath();
                this.connection = DriverManager.getConnection(jdbcUrl);

                RVCore.getInstance().logger.info("Connected to the SQLite database.");
            }
        } catch (final Exception ex) {
            RVCore.getInstance().logger.severe("Failed to connect to database: " + ex.getMessage());

            ex.printStackTrace();
        }
    }

    private void createTables() {
        try {
            final Statement statement = this.connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS " + (this.settings.getDatabaseType() == DatabaseType.MYSQL ? this.settings.getMysqlTablePfx() + "_" : "") + "particle_data (" +
                    "id INTEGER PRIMARY KEY " + (this.settings.getDatabaseType() == DatabaseType.MYSQL ? "AUTO_INCREMENT" : "AUTOINCREMENT") + ", " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "player_name TEXT NOT NULL, " +
                    "red_initial INT NOT NULL, " +
                    "green_initial INT NOT NULL, " +
                    "blue_initial INT NOT NULL, " +
                    "red_final INT NOT NULL, " +
                    "green_final INT NOT NULL, " +
                    "blue_final INT NOT NULL, " +
                    "count INT NOT NULL, " +
                    "y_offset DOUBLE NOT NULL, " +
                    "scale DOUBLE NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "enabled BOOLEAN NOT NULL, " +
                    "last_changed TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            String uniqueIdxSql = (this.settings.getDatabaseType() == DatabaseType.MYSQL ?
                    "CREATE UNIQUE INDEX IF NOT EXISTS unique_player_uuid " +
                    "ON " + (this.settings.getMysqlTablePfx()) + "_particle_data (player_uuid)" :

                    //SQLITE
                    "CREATE UNIQUE INDEX IF NOT EXISTS unique_player_uuid " +
                    "ON particle_data (player_uuid)");

            statement.executeUpdate(uniqueIdxSql);

            statement.close();
        } catch (final SQLException ex) {
            RVCore.getInstance().logger.severe("Failed to create tables in database: " + ex.getMessage());

            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            if (this.connection != null && this.connection.isClosed()) {
                this.connection.close();

                RVCore.getInstance().logger.info("Closed database connection.");
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean saveParticleData(Player player) {
        synchronized (this.connection) {
            try {
                final String sql;
                if (this.settings.getDatabaseType() == DatabaseType.MYSQL) {
                    sql =
                            "INSERT INTO " + (this.settings.getMysqlTablePfx()) + "_particle_data (" +
                                    "player_uuid, player_name, " +
                                    "red_initial, green_initial, blue_initial, " +
                                    "red_final, green_final, blue_final, " +
                                    "count, y_offset, scale, name, enabled" +
                                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE " +
                                    "player_name = VALUES(player_name), " +
                                    "red_initial = VALUES(red_initial), " +
                                    "green_initial = VALUES(green_initial), " +
                                    "blue_initial = VALUES(blue_initial), " +
                                    "red_final = VALUES(red_final), " +
                                    "green_final = VALUES(green_final), " +
                                    "blue_final = VALUES(blue_final), " +
                                    "count = VALUES(count), " +
                                    "y_offset = VALUES(y_offset), " +
                                    "scale = VALUES(scale), " +
                                    "name = VALUES(name), " +
                                    "enabled = VALUES(enabled), " +
                                    "last_changed = CURRENT_TIMESTAMP";
                } else { //SQLite
                    sql = "INSERT INTO particle_data (" +
                            "player_uuid, player_name, " +
                            "red_initial, green_initial, blue_initial, " +
                            "red_final, green_final, blue_final, " +
                            "count, y_offset, scale, name, enabled" +
                            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                            "ON CONFLICT(player_uuid) DO UPDATE SET " +
                            "player_name = excluded.player_name, " +
                            "red_initial = excluded.red_initial, " +
                            "green_initial = excluded.green_initial, " +
                            "blue_initial = excluded.blue_initial, " +
                            "red_final = excluded.red_final, " +
                            "green_final = excluded.green_final, " +
                            "blue_final = excluded.blue_final, " +
                            "count = excluded.count, " +
                            "y_offset = excluded.y_offset, " +
                            "scale = excluded.scale, " +
                            "name = excluded.name, " +
                            "enabled = excluded.enabled, " +
                            "last_changed = CURRENT_TIMESTAMP";
                }
                // Save particle parameters
                try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
                    ParticleData particleCache = RVConfig.getInstance().particleCache.get(player.getUniqueId());

                    statement.setString(1, player.getUniqueId().toString());
                    statement.setString(2, player.getName());

                    statement.setInt(3, particleCache.getR());
                    statement.setInt(4, particleCache.getG());
                    statement.setInt(5, particleCache.getB());
                    statement.setInt(6, particleCache.getR1());
                    statement.setInt(7, particleCache.getG1());
                    statement.setInt(8, particleCache.getB1());

                    statement.setInt(9, particleCache.getCount());
                    statement.setDouble(10, particleCache.getYOffset());
                    statement.setDouble(11, particleCache.getScale());

                    statement.setString(12, particleCache.getName());

                    statement.setBoolean(13, particleCache.getEnabled());


                    final int rows = statement.executeUpdate();
                    statement.close();

                    return rows > 0;
                }



            } catch (final SQLException ex) {
                ex.printStackTrace();

                return false;
            }
        }
    }

    public boolean retrieveParticleData(Player player) {
        UUID uuid = player.getUniqueId();

        ParticleData particleCache = RVConfig.getInstance().particleCache.get(uuid);

        synchronized (this.connection) {
            try {
                String sql;

                if (this.settings.getDatabaseType() == DatabaseType.MYSQL) {
                    sql = "SELECT * FROM " + (this.settings.getMysqlTablePfx()) + "_particle_data WHERE player_uuid = ?";
                } else {
                    sql = "SELECT * FROM particle_data WHERE player_uuid = ?"; //SQLITE
                }

                final PreparedStatement statement = this.connection.prepareStatement(sql);

                statement.setString(1, player.getUniqueId().toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return false;
                    }

                    ParticleData insertCache = new ParticleData(
                            resultSet.getInt("red_initial"),
                            resultSet.getInt("green_initial"),
                            resultSet.getInt("blue_initial"),
                            resultSet.getInt("red_final"),
                            resultSet.getInt("green_final"),
                            resultSet.getInt("blue_final"),
                            resultSet.getInt("count"),
                            resultSet.getDouble("y_offset"),
                            resultSet.getDouble("scale"),
                            resultSet.getString("name"),
                            resultSet.getBoolean("enabled")
                    );

                    if (particleCache == null) {
                        RVConfig.getInstance().particleCache.put(uuid, insertCache);
                        return true;
                    }

                    RVConfig.getInstance().particleCache.put(uuid, insertCache);
                    return true;
                }
            } catch (final SQLException ex) {
                ex.printStackTrace();

                return false;
            }
        }
    }

    public enum DatabaseType {
        MYSQL,
        SQLITE
    }

    public static Database getInstance() {
        return instance;
    }
}
