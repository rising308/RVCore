package ca.risingvocal.rvcore.listeners;

import ca.risingvocal.rvcore.RVCore;
import ca.risingvocal.rvcore.model.Database;
import ca.risingvocal.rvcore.util.ParticleData;
import ca.risingvocal.rvcore.util.RVConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Player player : RVCore.getInstance().getServer().getOnlinePlayers()) {
            if (RVConfig.getInstance().particleCache.isEmpty()) {
                ParticleData data = RVCore.getInstance().getPlayerCache().getOrDefault(player.getUniqueId(), new ParticleData(0, 0, 0, 255, 255, 255, 1, 0.0, 1.0, "null", false));


                RVCore.getInstance().getPlayerCache().put(player.getUniqueId(), data);
            }

            Database.getInstance().retrieveParticleData(player);
        }


    }
}
