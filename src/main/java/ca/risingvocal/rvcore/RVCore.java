package ca.risingvocal.rvcore;

import ca.risingvocal.rvcore.commands.RVCoreCommand;
import ca.risingvocal.rvcore.commands.ParticleCommand;
import ca.risingvocal.rvcore.commands.SweepstakesCommand;
import ca.risingvocal.rvcore.listeners.PlayerJoin;
import ca.risingvocal.rvcore.util.ParticleSpawn;
import ca.risingvocal.rvcore.util.ParticleData;
import ca.risingvocal.rvcore.util.RVConfig;
import ca.risingvocal.rvcore.util.Placeholders;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class RVCore extends JavaPlugin {

    private Economy econ;

    private ParticleSpawn particleSpawn;

    public Logger logger = getLogger();

    @Override
    public void onEnable() {
        RVConfig.getInstance().load();

        loadCommands();

        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);

        this.particleSpawn = new ParticleSpawn(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders(this).register();
        }

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                ParticleData data = RVConfig.getInstance().particleCache.get(player.getUniqueId());

                if (data != null && data.getName() != "NONE") {
                    particleSpawn.particleTickEvent(player, data);
                }
            }
        }, 0L, 1L);

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            RVConfig.getInstance().autoSave();
        }, 6000L, 6000L);

        if (!setupEconomy()) {
            logger.severe("Failed to load economy.");
        }
    }

    public Map<UUID, ParticleData> getPlayerCache() {
        return RVConfig.getInstance().particleCache;
    }

    public void loadCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new RVCoreCommand(this).getTree().build());
        });

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new SweepstakesCommand(this).getTree().build());
        });

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new ParticleCommand(this).getTree().build());
        });
    }



    public void reloadPlugin() {
        RVConfig.getInstance().load();
        this.logger.info("Reloading!");
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public static RVCore getInstance() {
        return getPlugin(RVCore.class);
    }

    @Override
    public void onDisable() {
        RVConfig.getInstance().save();
    }
}
