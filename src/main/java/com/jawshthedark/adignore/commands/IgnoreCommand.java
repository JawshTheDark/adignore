package com.jawshthedark.adignore.commands;

import com.jawshthedark.adignore.IgnoreManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;

public class IgnoreCommand extends Command {
    public IgnoreCommand() {
        super("ignore", "Add a player to the ignore list with a reason.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("player", StringArgumentType.word())
            .suggests((context, suggestionsBuilder) -> {
                List<String> names = new ArrayList<>();

                if (mc.getNetworkHandler() != null) {
                    for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                        names.add(entry.getProfile().name());
                    }
                }

                return CommandSource.suggestMatching(names, suggestionsBuilder);
            })
            .then(argument("reason", StringArgumentType.greedyString())
                .executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "player");
                    String reason = StringArgumentType.getString(ctx, "reason");

                    IgnoreManager.add(name, reason);
                    info("Ignored %s with reason: %s", name, reason);

                    return SINGLE_SUCCESS;
                })));
    }
}