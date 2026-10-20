package ca.risingvocal.rvcore.commands;

import ca.risingvocal.rvcore.RVCore;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class RVCoreCommand {
    private final RVCore plugin;
    public RVCoreCommand(RVCore plugin) {this.plugin = plugin;}

    public LiteralArgumentBuilder<CommandSourceStack> getTree() {
        return Commands.literal("rvcore")
                .then(subReload())
                .executes(ctx -> {
                    ctx.getSource().getSender().sendRichMessage("<green>RVCore | <dark_green>Version: <yellow>" + plugin.getPluginMeta().getVersion());
                    return 1;
                });
    }

    public LiteralArgumentBuilder<CommandSourceStack> subReload() {
        return Commands.literal("reload")
                .requires(sender -> sender.getSender().hasPermission("rvcore.command.admin.reload"))
                .executes(ctx -> {
                    plugin.reloadPlugin();
                    ctx.getSource().getSender().sendRichMessage("<green>RVCore | <dark_green>Reloaded!");
                    return 1;
                });
    }

}
