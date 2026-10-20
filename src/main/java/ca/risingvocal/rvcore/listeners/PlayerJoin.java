package ca.risingvocal.rvcore.listeners;

import ca.risingvocal.rvcore.RVCore;
import ca.risingvocal.rvcore.model.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Player player : RVCore.getInstance().getServer().getOnlinePlayers()) {
            Database.getInstance().retrieveParticleData(player);
        }
    }
}
