package com.spokiy.slimearenamod.event;

import com.spokiy.slimearenamod.component.PlayerData;
import com.spokiy.slimearenamod.component.SAComponents;
import com.spokiy.slimearenamod.enums.PlayerClass;
import com.spokiy.slimearenamod.enums.PlayerTeam;
import com.spokiy.slimearenamod.util.Util;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public class SAEvents {
    static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

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

                if (attackerTeam == PlayerTeam.SLIME && targetTeam == PlayerTeam.HUMAN) {
                    Util.infectPlayer(target, targetData);
                    attacker.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                            1, Util.randomRange(attacker.getRandom(), 0.8f, 1.0f));
                    target.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                            1, Util.randomRange(target.getRandom(), 0.8f, 1.0f));

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
                else if (attackerClass == PlayerClass.SUPPORT && targetTeam == PlayerTeam.SLIME) {
                    target.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SPEED, 20 * 6, 2,
                            true, true, true));
                    target.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.JUMP_BOOST, 20 * 6, 1,
                            true, false, true));
                }

                // Friendly fire off
                if (attackerData.getPlayerTeam() == targetData.getPlayerTeam()) return ActionResult.FAIL;
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