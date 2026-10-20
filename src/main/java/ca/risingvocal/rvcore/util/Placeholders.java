package ca.risingvocal.rvcore.util;

import ca.risingvocal.rvcore.RVCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {
    public Placeholders(RVCore rvCore) {
    }

    @Override
    public String getIdentifier() {
        return "rvcore";
    }

    @Override
    public String getAuthor() {
        return "rising308";
    }

    @Override
    public String getVersion() {
        return "1.1";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        ParticleData particleCache = RVConfig.getInstance().particleCache.get(player.getUniqueId());

        return switch (params.toLowerCase()) {
            case "red_initial" ->
                    (String.valueOf(particleCache.getR()));
            case "green_initial" ->
                    (String.valueOf(particleCache.getG()));
            case "blue_initial" ->
                    (String.valueOf(particleCache.getB()));
            case "red_final" ->
                    (String.valueOf(particleCache.getR1()));
            case "green_final" ->
                    String.valueOf(particleCache.getG1());
            case "blue_final" ->
                    (String.valueOf(particleCache.getB1()));
            case "count" ->
                    (String.valueOf(particleCache.getCount()));
            case "y_offset" ->
                    (String.valueOf(particleCache.getYOffset()));
            case "scale" ->
                    (String.valueOf(particleCache.getScale()));
            case "name" ->
                    (particleCache.getName());
            case "enabled" ->
                    (String.valueOf(particleCache.getEnabled()));
            default -> null;
        };

    }
}
