package ca.risingvocal.lbpcore.listeners;

import ca.risingvocal.lbpcore.LBPCore;
import ca.risingvocal.lbpcore.model.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Player player : LBPCore.getInstance().getServer().getOnlinePlayers()) {
            Database.getInstance().retrieveParticleData(player);
        }
    }
}
