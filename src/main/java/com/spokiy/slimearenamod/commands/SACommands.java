package com.spokiy.slimearenamod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.spokiy.slimearenamod.enums.PlayerClass;
import com.spokiy.slimearenamod.enums.PlayerTeam;
import com.spokiy.slimearenamod.util.Util;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class SACommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(
                CommandManager.literal("arena")
                        .then(CommandManager.literal("class")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("type", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    for (PlayerClass value : PlayerClass.values()) {
                                                        builder.suggest(value.name().toLowerCase());
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx -> {

                                                    Collection<ServerPlayerEntity> players =
                                                            EntityArgumentType.getPlayers(ctx, "targets");

                                                    String type = StringArgumentType.getString(ctx, "type");

                                                    PlayerClass playerClass;
                                                    try {
                                                        playerClass = PlayerClass.valueOf(type.toUpperCase());
                                                    } catch (IllegalArgumentException e) {
                                                        ctx.getSource().sendError(
                                                                Text.literal("Unknown class: " + type)
                                                        );
                                                        return 0;
                                                    }

                                                    PlayerTeam playerTeam = PlayerTeam.NONE;
                                                    if (Util.HUMAN_CLASSES.contains(playerClass)) {
                                                        playerTeam = PlayerTeam.HUMAN;
                                                    } else if (Util.SLIME_CLASSES.contains(playerClass)) {
                                                        playerTeam = PlayerTeam.SLIME;
                                                    }

                                                    for (ServerPlayerEntity target : players) {

                                                        Util.changePlayerClass(target, playerClass);
                                                        Util.changePlayerTeam(target, playerTeam);

                                                        target.sendMessage(
                                                                Text.literal("Your class: " + playerClass.name()),
                                                                false
                                                        );

                                                        target.sendMessage(
                                                                Text.literal("Your team: " + playerTeam.name()),
                                                                false
                                                        );
                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }
}