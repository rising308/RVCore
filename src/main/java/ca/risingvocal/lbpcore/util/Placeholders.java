package ca.risingvocal.lbpcore.util;

import ca.risingvocal.lbpcore.LBPCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {
    public Placeholders(LBPCore lbpCore) {
    }

    @Override
    public String getIdentifier() {
        return "lbpcore";
    }

    @Override
    public String getAuthor() {
        return "rising308";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        ParticleData particleCache = LBPConfig.getInstance().particleCache.get(player.getUniqueId());

        if (params.equalsIgnoreCase("red_initial")) {
            return String.valueOf(particleCache.getR()); //
        }
        if (params.equalsIgnoreCase("green_initial")) {
            return String.valueOf(particleCache.getG()); //
        }
        if (params.equalsIgnoreCase("blue_initial")) {
            return String.valueOf(particleCache.getB()); //
        }
        if (params.equalsIgnoreCase("red_final")) {
            return String.valueOf(particleCache.getR1()); //
        }
        if (params.equalsIgnoreCase("green_final")) {
            return String.valueOf(particleCache.getG1()); //
        }
        if (params.equalsIgnoreCase("blue_final")) {
            return String.valueOf(particleCache.getB1()); //
        }
        if (params.equalsIgnoreCase("count")) {
            return String.valueOf(particleCache.getCount()); //
        }
        if (params.equalsIgnoreCase("y_offset")) {
            return String.valueOf(particleCache.getYOffset()); //
        }
        if (params.equalsIgnoreCase("scale")) {
            return String.valueOf(particleCache.getScale()); //
        }
        if (params.equalsIgnoreCase("name")) {
            return particleCache.getName(); //
        }
        if (params.equalsIgnoreCase("enabled")) {
            return (particleCache.getEnabled() ? "Yes" : "No"); //
        }

        return null; //
    }
}
