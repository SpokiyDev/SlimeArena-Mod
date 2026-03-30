package com.spokiy.slimearenamod.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.data.PlayerClass;
import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.data.WorldData;
import com.spokiy.slimearenamod.data.GamePhaseType;
import com.spokiy.slimearenamod.util.shop.ShopMenu;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.HashSet;

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


                                                    for (ServerPlayerEntity target : players) {
                                                        Util.changePlayerClass(target, playerClass);

                                                        target.sendMessage(
                                                                Text.literal("Your class: " + playerClass.name()),
                                                                false
                                                        );

                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(CommandManager.literal("start")
                                .executes(ctx -> {
                                    MinecraftServer server = ctx.getSource().getServer();
                                    WorldData worldData = SAComponents.WORLD_DATA.get(server.getOverworld());

                                    worldData.setPhase(GamePhaseType.SLIME);
                                    worldData.initGameTimer(WorldData.GAME_PHASES.get(GamePhaseType.SLIME).maxTimerValue);

                                    String gameTag = "playing_" + (1000000000L + (long) (Math.random() * 9000000000L));
                                    worldData.setGameTag(gameTag);

                                    SlimeArenaMod.bossBar.setVisible(true);
                                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                        for (String tag : new HashSet<>(player.getCommandTags())) player.removeCommandTag(tag);

                                        SlimeArenaMod.bossBar.addPlayer(player);
                                        player.changeGameMode(Config.ARENA_GAMEMODE);

                                        player.addCommandTag(gameTag);
                                    }

                                    return 1;
                                })
                        )
        );

        dispatcher.register(CommandManager.literal("shop")
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                            .executes(ctx -> {
                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                if (player == null) return 0;

                                ShopMenu.open(player);

                                return 1;
                            }))
        );

        dispatcher.register(CommandManager.literal("push")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .then(CommandManager.argument("x", DoubleArgumentType.doubleArg())
                                .then(CommandManager.argument("y", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                                                    double x = DoubleArgumentType.getDouble(ctx, "x");
                                                    double y = DoubleArgumentType.getDouble(ctx, "y");
                                                    double z = DoubleArgumentType.getDouble(ctx, "z");

                                                    target.setVelocity(new Vec3d(x, y, z));
                                                    target.velocityModified = true;

                                                    ctx.getSource().sendMessage(
                                                            Text.literal("Pushed " + target.getName().getString() +
                                                                    " with vector (" + x + ", " + y + ", " + z + ")")
                                                    );

                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        );


        dispatcher.register(CommandManager.literal("push")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .then(CommandManager.argument("x", DoubleArgumentType.doubleArg())
                                .then(CommandManager.argument("y", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                                                    double x = DoubleArgumentType.getDouble(ctx, "x");
                                                    double y = DoubleArgumentType.getDouble(ctx, "y");
                                                    double z = DoubleArgumentType.getDouble(ctx, "z");

                                                    target.setVelocity(new Vec3d(x, y, z));
                                                    target.velocityModified = true;

                                                    ctx.getSource().sendMessage(
                                                            Text.literal("Pushed " + target.getName().getString() +
                                                                    " with vector (" + x + ", " + y + ", " + z + ")")
                                                    );

                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        );
    }
}