package ca.risingvocal.rvcore.commands;

import ca.risingvocal.rvcore.RVCore;
import ca.risingvocal.rvcore.util.RVConfig;
import ca.risingvocal.rvcore.util.ParticleData;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Particle;

public class ParticleCommand {
    private final RVCore plugin;

    public ParticleCommand(RVCore plugin) {this.plugin = plugin;}

    public LiteralArgumentBuilder<CommandSourceStack> getTree() {
        return Commands.literal("particle_effect")
                .requires(sender -> sender.getSender().hasPermission("rvcore.command.admin.particle"))

                .then(Commands.literal("set")
                                .then(Commands.argument("target", ArgumentTypes.player())
                                        .then(Commands.argument("type", ArgumentTypes.resource(RegistryKey.PARTICLE_TYPE))
                                                .executes(ctx -> {
                                                    setParticle(ctx, 1, false);
                                                    return 1;
                                                })
                                                .then(Commands.argument("count", IntegerArgumentType.integer(0,4))
                                                        .executes(ctx -> {
                                                            setParticle(ctx, 2, false);
                                                            return 1;
                                                        })
                                                        .then(Commands.argument("y_offset", DoubleArgumentType.doubleArg(0.0,2.0))
                                                                .executes(ctx -> {
                                                                    setParticle(ctx, 3, false);
                                                                    return 1;
                                                                })
                                                                .then(Commands.literal("hide")
                                                                        .executes(ctx -> {
                                                                            setParticle(ctx, 3, true);
                                                                            return 1;
                                                                        })))))))

                .then(Commands.literal("rgb")
                                .then(Commands.argument("target", ArgumentTypes.player())
                                        .then(Commands.argument("red", IntegerArgumentType.integer(0,255))
                                                .then(Commands.argument("green", IntegerArgumentType.integer(0,255))
                                                        .then(Commands.argument("blue", IntegerArgumentType.integer(0,255))
                                                                .then(Commands.argument("red1", IntegerArgumentType.integer(0,255))
                                                                        .then(Commands.argument("green1", IntegerArgumentType.integer(0,255))
                                                                                .then(Commands.argument("blue1", IntegerArgumentType.integer(0, 255))
                                                                                        .then(Commands.argument("scale", DoubleArgumentType.doubleArg(0.5,2.0))
                                                                                                .executes(ctx -> {
                                                                                                    setRGB(ctx, false);
                                                                                                    return 1;
                                                                                                })
                                                                                                .then(Commands.literal("hide")
                                                                                                        .executes(ctx -> {
                                                                                                            setRGB(ctx, true);
                                                                                                            return 1;
                                                                                                        }))
                                                                                )))))))))

                .then(Commands.literal("toggle")
                        .then(Commands.argument("target", ArgumentTypes.player())
                                .executes(ctx -> {
                                    toggleParticle(ctx, false);
                                    return 1;
                                })
                                .then(Commands.literal("hide")
                                        .executes(ctx -> {
                                            toggleParticle(ctx, true);
                                            return 1;
                                        }))));
    }

    private void setParticle(CommandContext<CommandSourceStack> ctx, int args, boolean hide) throws CommandSyntaxException {
        var target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();

        CommandSourceStack source = ctx.getSource();

        ParticleData data = plugin.getPlayerCache().getOrDefault(target.getUniqueId(), new ParticleData(0, 0, 0, 255, 255, 255, 3, 0.0, 1.0, "FLAME", true));

        Particle type = ctx.getArgument("type", Particle.class);
        int count = IntegerArgumentType.getInteger(ctx, "count");
        double yOffset = DoubleArgumentType.getDouble(ctx, "y_offset");

        if (args >= 1) {
            data.setName(String.valueOf(type));

            if (args == 1 && !hide) {
                String rawSender = RVConfig.getInstance().getLang().getString("commands.particles.set_type",
                        "<aqua>Set particle to <dark_aqua><type> <aqua>for <light_purple><player><aqua>!");
                Component sender = MiniMessage.miniMessage().deserialize(rawSender,
                        Placeholder.unparsed("type", String.valueOf(type)),
                        Placeholder.unparsed("player", target.getName())
                );
                source.getSender().sendMessage(sender);
            }
        }
        if (args >= 2) {
            data.setCount(count);

            if (args == 2 && !hide) {
                String rawSender = RVConfig.getInstance().getLang().getString("commands.particles.set_count",
                        "<aqua>Set particle to <dark_aqua><type> <aqua>with count <dark_aqua><count> <aqua>for <light_purple><player><aqua>!");
                Component sender = MiniMessage.miniMessage().deserialize(rawSender,
                        Placeholder.unparsed("type", String.valueOf(type)),
                        Placeholder.unparsed("count", String.valueOf(count)),
                        Placeholder.unparsed("player", target.getName())
                );
                source.getSender().sendMessage(sender);
            }
        }
        if (args >= 3) {
            data.setYOffset(yOffset);

            if (args == 3 && !hide) {
                String rawSender = RVConfig.getInstance().getLang().getString("commands.particles.set_full",
                        "<aqua>Set particle to <dark_aqua><type> <aqua>with count <dark_aqua><count> " +
                                "<aqua>and Y-Offset <dark_aqua><offset> <aqua>for <light_purple><player><aqua>!");
                Component sender = MiniMessage.miniMessage().deserialize(rawSender,
                        Placeholder.unparsed("type", String.valueOf(type)),
                        Placeholder.unparsed("count", String.valueOf(count)),
                        Placeholder.unparsed("offset", String.valueOf(yOffset)),
                        Placeholder.unparsed("player", target.getName())
                );
                source.getSender().sendMessage(sender);
            }
        }
        plugin.getPlayerCache().put(target.getUniqueId(), data);
        RVConfig.getInstance().save();
    }

    private void setRGB(CommandContext<CommandSourceStack> ctx, boolean hide) throws CommandSyntaxException {
        var target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();

        CommandSourceStack source = ctx.getSource();


        int red = IntegerArgumentType.getInteger(ctx, "red");
        int green = IntegerArgumentType.getInteger(ctx, "green");
        int blue = IntegerArgumentType.getInteger(ctx, "blue");
        int red1 = IntegerArgumentType.getInteger(ctx, "red1");
        int green1 = IntegerArgumentType.getInteger(ctx, "green1");
        int blue1 = IntegerArgumentType.getInteger(ctx, "blue1");
        double scale = DoubleArgumentType.getDouble(ctx, "scale");

        ParticleData data = plugin.getPlayerCache().getOrDefault(target.getUniqueId(), new ParticleData(0, 0, 0, 255, 255, 255, 3, 0.0, 1.0, "FLAME", true));

        data.setR(red);
        data.setG(green);
        data.setB(blue);
        data.setR1(red1);
        data.setG1(green1);
        data.setB1(blue1);
        data.setScale(scale);

        if (!hide) {
            String rawSender = RVConfig.getInstance().getLang().getString("commands.particles.rgb",
                    "<aqua>Set RGB to <dark_aqua>[<r>, <g>, <b>] [<r1>, <g1>, <b1>] <aqua>with scale <dark_aqua><scale> <aqua>for <light_purple><player><aqua>!");
            Component sender = MiniMessage.miniMessage().deserialize(rawSender,
                    Placeholder.unparsed("r", String.valueOf(red)),
                    Placeholder.unparsed("g", String.valueOf(green)),
                    Placeholder.unparsed("b", String.valueOf(blue)),
                    Placeholder.unparsed("r1", String.valueOf(red1)),
                    Placeholder.unparsed("g1", String.valueOf(green1)),
                    Placeholder.unparsed("b1", String.valueOf(blue1)),
                    Placeholder.unparsed("scale", String.valueOf(scale)),
                    Placeholder.unparsed("player", target.getName())
            );
            source.getSender().sendMessage(sender);
        }

        plugin.getPlayerCache().put(target.getUniqueId(), data);
        RVConfig.getInstance().save();
    }

    private void toggleParticle(CommandContext<CommandSourceStack> ctx, boolean hide) throws CommandSyntaxException {
        var target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();

        CommandSourceStack source = ctx.getSource();

        ParticleData data = plugin.getPlayerCache().getOrDefault(target.getUniqueId(), new ParticleData(0, 0, 0, 255, 255, 255, 3, 0.0, 1.0, "FLAME", true));

        data.setEnabled(!data.getEnabled());

        if (!hide) {
            String rawSender = RVConfig.getInstance().getLang().getString("commands.particles.toggle",
                    "<dark_aqua><state> <aqua>particle effect for <light_purple><player><aqua>!");
            Component sender = MiniMessage.miniMessage().deserialize(rawSender,
                    Placeholder.unparsed("state", data.getEnabled() ? "Enabled" : "Disabled"),
                    Placeholder.unparsed("player", target.getName())
            );
            source.getSender().sendMessage(sender);
        }

        plugin.getPlayerCache().put(target.getUniqueId(), data);
        RVConfig.getInstance().save();
    }
}
