package ca.risingvocal.rvcore.util;

import ca.risingvocal.rvcore.RVCore;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleSpawn {

    private final RVCore plugin;
    public ParticleSpawn(RVCore plugin) {this.plugin = plugin;}

    public void particleTickEvent(Player player, ParticleData data) {

        if (data.getEnabled()) {
            String name = data.getName();
            if (name == null || name.isEmpty()) return;
            if (player.getGameMode().equals(GameMode.SPECTATOR) && !RVConfig.getInstance().getParticleSpectator())
                return;

            try {
                Particle particle = Particle.valueOf(name.toLowerCase());
                var builder = particle.builder()
                        .location(player.getLocation().add(0, data.getYOffset(), 0))
                        .offset(0.5, 0.5, 0.5)
                        .count(data.getCount()).speed(0)
                        .receivers(RVConfig.getInstance().getParticleDistance(), RVConfig.getInstance().getParticleSphere());

                if (particle == Particle.DUST_COLOR_TRANSITION) {
                    int red = data.getR();
                    int green = data.getG();
                    int blue = data.getB();
                    int red1 = data.getR1();
                    int green1 = data.getG1();
                    int blue1 = data.getB1();
                    builder.colorTransition(Color.fromRGB(red, green, blue), Color.fromRGB(red1, green1, blue1), (float) data.getScale());
                } else if (particle == Particle.DUST) {
                    int red = data.getR();
                    int green = data.getG();
                    int blue = data.getB();
                    builder.color(1, red, green, blue);
                }

                builder.spawn();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
