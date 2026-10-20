package ca.risingvocal.lbpcore.commands;

import ca.risingvocal.lbpcore.LBPCore;
import ca.risingvocal.lbpcore.util.LBPConfig;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class SweepstakesCommand {
    private final LBPCore plugin;
    public SweepstakesCommand(LBPCore plugin) {this.plugin = plugin;}

    public LiteralArgumentBuilder<CommandSourceStack> getTree() {
        return Commands.literal("sweepstakes")
                .requires(sender -> sender.getSender().hasPermission("lbpcore.command.admin.sweepstakes"))
                .then(Commands.argument("target", ArgumentTypes.player())
                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                                .then(Commands.argument("min_chance", DoubleArgumentType.doubleArg(0.01, 100))
                                        .then(Commands.argument("max_chance", DoubleArgumentType.doubleArg(0.01, 100))
                                                .executes(ctx -> {
                                                    executeSweepstakes(ctx, false);
                                                    return 1;
                                                })
                                                .then(Commands.literal("hide")
                                                        .executes(ctx -> {
                                                            executeSweepstakes(ctx, true);
                                                            return 1;
                                                        }))
                                        ))));
    }

    private void executeSweepstakes(CommandContext<CommandSourceStack> ctx, boolean hide) throws CommandSyntaxException {
        Economy economy = plugin.getEconomy();
        CommandSourceStack source = ctx.getSource();

        double finalAmount = 0;

        var targetGet = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(source);
        if (targetGet.isEmpty()) return;
        Player playerName = targetGet.getFirst();

        double amount = ctx.getArgument("amount", Double.class);
        double chanceMinArgs = ctx.getArgument("min_chance", Double.class);
        double chanceMaxArgs = ctx.getArgument("max_chance", Double.class);

        double chance = chanceMinArgs / chanceMaxArgs;

        double minGlobal = LBPConfig.getInstance().getMinChance();
        double maxGlobal = LBPConfig.getInstance().getMaxChance();

        chance = Math.clamp(chance, minGlobal, maxGlobal);

        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll < chance) {
            finalAmount = amount;
        }

        if (LBPConfig.getInstance().getDebugState()) {
            source.getSender().sendMessage("chance = " + chance);
            source.getSender().sendMessage("roll = " + roll);
            source.getSender().sendMessage("finalAmount = " + finalAmount);
        }

        String formattedAmount = economy.format(finalAmount);


        if (!hide) {
            String rawSender = LBPConfig.getInstance().getLang().getString("commands.sweepstakes.sender", "<aqua>Executed sweepstakes on <light_purple><player><aqua>!");
            Component sender = MiniMessage.miniMessage().deserialize(rawSender,
                    Placeholder.unparsed("amount", formattedAmount),
                    Placeholder.unparsed("player", playerName.getName())
            );
            source.getSender().sendMessage(sender);
        }

        // Check for win
        if (finalAmount > 0) {
            economy.depositPlayer(playerName.getPlayer(), finalAmount);

            String rawTargetWin = LBPConfig.getInstance().getLang().getString("commands.sweepstakes.win", "<gold>You won <yellow><amount><gold>!");
            Component targetWin = MiniMessage.miniMessage().deserialize(rawTargetWin,
                    Placeholder.unparsed("amount", formattedAmount));
            playerName.getPlayer().sendMessage(targetWin);
            return;
        }
        // Lose
        String rawTargetLose = LBPConfig.getInstance().getLang().getString("commands.sweepstakes.lose", "<gray>You didn't win anything...");
        Component targetLose = MiniMessage.miniMessage().deserialize(rawTargetLose);
        playerName.getPlayer().sendMessage(targetLose);
    }
}
