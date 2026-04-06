package com.spokiy.slimearenamod.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.data.*;
import com.spokiy.slimearenamod.config.Config;
import com.spokiy.slimearenamod.config.ConfigManager;
import com.spokiy.slimearenamod.util.class_selection.ClassSelectionMenu;
import com.spokiy.slimearenamod.util.shop.ShopMenu;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
                                                .requires(source -> source.getServer() != null)
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
                                .requires(source -> source.getServer() != null)
                                .executes(ctx -> {

                                    MinecraftServer server = ctx.getSource().getServer();
                                    WorldData worldData = SAComponents.WORLD_DATA.get(server.getOverworld());

                                    if (!worldData.getCurrentPhase().equals(GamePhaseType.LOBBY)) {
                                        ctx.getSource().sendMessage(
                                                Text.literal("Stop the ongoing game first")
                                                        .formatted(Formatting.RED));
                                        return 0;
                                    }

                                    worldData.setCurrentPhase(GamePhaseType.SLIME);
                                    worldData.initGameTimer(GamePhaseType.SLIME);

                                    String gameTag = "playing_" + (1000000000L + (long) (Math.random() * 9000000000L));
                                    worldData.setGameTag(gameTag);

                                    SlimeArenaMod.bossBar.setVisible(true);
                                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                        for (String tag : new HashSet<>(player.getCommandTags())) player.removeCommandTag(tag);

                                        SlimeArenaMod.bossBar.addPlayer(player);
                                        player.changeGameMode(Config.ARENA_GAMEMODE);

                                        player.addCommandTag(gameTag);
                                    }

                                    ctx.getSource().sendMessage(Text.literal("The game was started"));
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("stop")
                                .requires(source -> source.getServer() != null)
                                .executes(ctx -> {
                                    ServerWorld world = ctx.getSource().getWorld();
                                    WorldData worldData = SAComponents.WORLD_DATA.get(world);

                                    Util.endGame(world, worldData, PlayerTeam.NONE);

                                    ctx.getSource().sendMessage(Text.literal("The game was stopped"));
                                    return 1;
                                })
                        )
        );
    

        dispatcher.register(CommandManager.literal("lobby")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    if (player == null) return 0;
                    if (!player.getCommandTags().contains("training")) return 0;

                    Vec3d pos = Config.LOBBY_POS;

                    player.teleport(player.getServerWorld(), pos.x, pos.y, pos.z, player.getYaw(), 0);
                    player.removeCommandTag("training");

                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("training")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    if (player == null || !player.getCommandTags().isEmpty()) return 0;

                    Vec3d pos = Config.TRAINING_POS;

                    player.teleport(player.getServerWorld(), pos.x, pos.y, pos.z, player.getYaw(), 0);
                    player.addCommandTag("training");

                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("shop")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    if (player == null) return 0;

                    ShopMenu.open(player);

                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("slimeclass")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    if (player == null) return 0;

                    ClassSelectionMenu.open(player);

                    return 1;
                })
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

        dispatcher.register(CommandManager.literal("reloadarenaconfig")
                .requires(source -> source.getServer() != null)
                .executes(ctx -> {
                    Config.DATA = ConfigManager.load();

                    ctx.getSource().sendMessage(
                            Text.translatable("commands.slimearenamod.reload_server_config.success").formatted(Formatting.GREEN)
                    );
                    return 1;
                })
        );
    }
}