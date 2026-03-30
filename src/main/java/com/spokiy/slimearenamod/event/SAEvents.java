package com.spokiy.slimearenamod.event;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.data.*;
import com.spokiy.slimearenamod.data.GamePhaseType;
import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.world.item.VaccineItem;
import com.spokiy.slimearenamod.util.Util;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SAEvents {
    static int tickCounter = 0;

    public static void register() {
        ServerPlayerEvents.JOIN.register((player) -> {
            PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
            if (playerData.getPlayerClass() == PlayerClass.NONE) {
                Util.changePlayerClass(player, playerData, PlayerClass.HUMAN);
            }

            // Game timer
            ServerWorld world = Objects.requireNonNull(player.getServer()).getOverworld();
            WorldData worldData = SAComponents.WORLD_DATA.get(world);

            SlimeArenaMod.bossBar.addPlayer(player);
            if (worldData.getGameTimer() > 0) {
                if (!player.getCommandTags().contains(worldData.getGameTag())) {
                    player.changeGameMode(GameMode.SPECTATOR);
                    // Tp to the map
                } else player.changeGameMode(Config.ARENA_GAMEMODE);
            } else {
                player.changeGameMode(Config.ARENA_GAMEMODE);
                Util.changePlayerClass(player, playerData, PlayerClass.HUMAN);
                player.teleport(world, 0, -60, 0, player.getYaw(), 0);

                if (!player.getCommandTags().isEmpty()) {
                    for (String tag : new HashSet<>(player.getCommandTags())) player.removeCommandTag(tag);
                }
            }

        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

            // Game timer
            WorldData worldData = SAComponents.WORLD_DATA.get(server.getOverworld());
            if (worldData.getGameTimer() > 0) {
                worldData.setGameTimer(worldData.getGameTimer() - 1);
            } else if (worldData.getGameTimer() == 0) {
                switch (worldData.getPhase()) {
                    case SLIME:
                        worldData.setPhase(GamePhaseType.PLAYING);

                        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList().stream()
                                .filter(p -> p.getCommandTags().contains(worldData.getGameTag()))
                                .toList();

                        if (!players.isEmpty()) {
                            worldData.initGameTimer(WorldData.GAME_PHASES.get(GamePhaseType.PLAYING).maxTimerValue);

                            ServerPlayerEntity randomPlayer =
                                    players.get(ThreadLocalRandom.current().nextInt(players.size()));

                            Util.infectPlayer(randomPlayer);
                            break;
                        } else {
                            server.getPlayerManager().broadcast(
                                    Text.literal("Гру було аварійно завершено").formatted(Formatting.RED),
                                    false);
                        }

                    case PLAYING:
                        SlimeArenaMod.bossBar.setVisible(false);
                        SlimeArenaMod.bossBar.clearPlayers();

                        worldData.setPhase(GamePhaseType.LOBBY);
                        worldData.setGameTimer(-1);

                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                            player.changeGameMode(Config.ARENA_GAMEMODE);
                            Util.changePlayerClass(player, SAComponents.PLAYER_DATA.get(player), PlayerClass.HUMAN);
                            player.teleport(server.getOverworld(), 0, -60, 0, player.getYaw(), 0);

                            if (!player.getCommandTags().isEmpty()) {
                                for (String tag : new HashSet<>(player.getCommandTags())) player.removeCommandTag(tag);
                            }
                        }
                        break;
                }

            }
        });

        AttackEntityCallback.EVENT.register(((attacker, world, hand, entity, result) -> {
            if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;

            if (entity instanceof ServerPlayerEntity target) {
                PlayerData attackerData = SAComponents.PLAYER_DATA.get(attacker);
                PlayerData targetData = SAComponents.PLAYER_DATA.get(target);

                PlayerTeam attackerTeam = attackerData.getPlayerTeam();
                PlayerClass attackerClass = attackerData.getPlayerClass();
                PlayerTeam targetTeam = targetData.getPlayerTeam();
                PlayerClass targetClass = targetData.getPlayerClass();

                // Team change on hit
                ItemStack stack = attacker.getMainHandStack();
                if (stack.getItem() instanceof VaccineItem && targetTeam == PlayerTeam.SLIME) {
                    stack.setCount(stack.getCount() - 1);

                    Util.curePlayer(target, targetData);
                    attacker.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, Util.randomRange(attacker.getRandom(), 0.8f, 1.0f));
                    target.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, Util.randomRange(target.getRandom(), 0.8f, 1.0f));

                    return ActionResult.FAIL;
                }
                else if (attackerTeam == PlayerTeam.SLIME && targetTeam == PlayerTeam.HUMAN) {
                    Util.infectPlayer(target, targetData);
                    attacker.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, Util.randomRange(attacker.getRandom(), 0.8f, 1.0f));
                    target.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, Util.randomRange(target.getRandom(), 0.8f, 1.0f));

                    Vec3d pos = target.getBoundingBox().getCenter();
                    serverWorld.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.getDefaultState()),
                            pos.x ,pos.y, pos.z,
                            16,
                            0.25, 0.25 ,0.25,
                            0.1
                    );

                    return ActionResult.FAIL;
                }

                // Hit abilities
                else if (attackerClass == PlayerClass.SUPPORT && targetTeam == PlayerTeam.SLIME) {
                    int speedAmplifier = target.hasStatusEffect(StatusEffects.SPEED) ?
                            Objects.requireNonNull(target.getStatusEffect(StatusEffects.SPEED)).getAmplifier() : -1;
                    int jumpAmplifier = target.hasStatusEffect(StatusEffects.JUMP_BOOST) ?
                            Objects.requireNonNull(target.getStatusEffect(StatusEffects.JUMP_BOOST)).getAmplifier() : -1;

                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,
                            20 * Config.SUPPORT_EFFECT_DURATION,
                            Math.min(speedAmplifier + 1, Config.SUPPORT_MAX_SPEED_AMPLIFIER),
                            false, true, true));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST,
                            20 * Config.SUPPORT_EFFECT_DURATION,
                            Math.min(jumpAmplifier + 1, Config.SUPPORT_MAX_JUMP_BOOST_AMPLIFIER),
                            false, false, true));
                }

                // Friendly fire off (Slime Team)
                if (attackerData.getPlayerTeam() == PlayerTeam.SLIME && targetData.getPlayerTeam() == PlayerTeam.SLIME)
                    return ActionResult.FAIL;
            }

            return ActionResult.PASS;

        }));

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof PlayerEntity player) {
                PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
                return playerData.getPlayerTeam() != PlayerTeam.SLIME || !source.isOf(DamageTypes.FALL);
            }

            return true;
        });

    }

}