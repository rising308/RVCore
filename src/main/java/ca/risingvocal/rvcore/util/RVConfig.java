package ca.risingvocal.rvcore.util;

import ca.risingvocal.rvcore.RVCore;
import ca.risingvocal.rvcore.model.Database;
import ca.risingvocal.rvcore.model.Database.DatabaseType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RVConfig {
    private final static RVConfig instance = new RVConfig();

    public FileConfiguration langConfig;

    public Map<UUID, ParticleData> particleCache = new HashMap<>();

    //General options
    private boolean isDebug;

    //Sweepstakes
    private double minChance;
    private double maxChance;

    //Particles
    private int particleDistance;

    private boolean particleSphere;
    private boolean particleInSpectator;

    private DatabaseType databaseType;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDb;
    private String mysqlUser;
    private String mysqlPass;
    private String sqliteFilename;

    private RVConfig() {
    }

    public void load() {
        File configFile = new File(RVCore.getInstance().getDataFolder(), "options.yml");
        File langFile = new File (RVCore.getInstance().getDataFolder(), "lang/en_US.yml");
        loadLang();

        if (!configFile.exists()) RVCore.getInstance().saveResource("options.yml", false);
        if (!langFile.exists()) RVCore.getInstance().saveResource("lang/en_US.yml", false);

        YamlConfiguration config = new YamlConfiguration();

        for (Player player : RVCore.getInstance().getServer().getOnlinePlayers()) {
            Database.getInstance().retrieveParticleData(player);
        }

        try {
            config.options().parseComments(true);
        } catch (final Throwable t) {

        }

        try {
            config.load(configFile);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        isDebug = config.getBoolean("sweepstakes.debug");

        minChance = config.getDouble("sweepstakes.min-chance");
        maxChance = config.getDouble("sweepstakes.max-chance");

        particleDistance = config.getInt("particles.view-distance");
        particleSphere = config.getBoolean("particles.view-sphere");
        particleInSpectator = config.getBoolean("particles.show-in-spectator");

        databaseType = DatabaseType.valueOf(config.getString("database.type").toUpperCase());
        mysqlHost = config.getString("database.host");
        mysqlPort = config.getInt("database.port");
        mysqlDb = config.getString("database.database");
        mysqlUser = config.getString("database.username");
        mysqlPass = config.getString("database.password");

        sqliteFilename = config.getString("database.sqlite.filename");

        if (databaseType == DatabaseType.MYSQL && (mysqlHost == null || mysqlPort == 0 || mysqlDb == null || mysqlUser == null || mysqlPass == null))
            throw new RuntimeException("Missing MySQL credentials in options.yml");

        Database.getInstance().load();
    }

    public void save() {
        if (!RVCore.getInstance().getDataFolder().exists()) RVCore.getInstance().getDataFolder().mkdirs();

        for (Player player : RVCore.getInstance().getServer().getOnlinePlayers()) {
            Database.getInstance().saveParticleData(player);
        }
    }

    public void autoSave() {
        if (!particleCache.isEmpty()) {
            save();
            RVCore.getInstance().getLogger().info("Autosaved " + particleCache.size() + " player profiles to JSON.");
        }
    }

    public void loadLang() {
        File langFile = new File(RVCore.getInstance().getDataFolder(), "lang/en_US.yml");
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public FileConfiguration getLang() {
        return this.langConfig;
    }

    public boolean getDebugState() {
        return isDebug;
    }

    public double getMinChance() {
        return minChance;
    }

    public double getMaxChance() {
        return maxChance;
    }

    public int getParticleDistance() {
        return particleDistance;
    }

    public boolean getParticleSphere() {
        return particleSphere;
    }

    public boolean getParticleSpectator() {
        return particleInSpectator;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDb() {
        return mysqlDb;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public String getMysqlPass() {
        return mysqlPass;
    }

    public String getSqliteFilename() {
        return sqliteFilename;
    }

    public static RVConfig getInstance() {
        return instance;
    }
}
