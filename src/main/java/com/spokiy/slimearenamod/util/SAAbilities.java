package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.components.PlayerData;
import com.spokiy.slimearenamod.components.SAComponents;
import com.spokiy.slimearenamod.components.PlayerClass;
import com.spokiy.slimearenamod.components.PlayerTeam;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;

public class SAAbilities {
    public static final Map<PlayerClass, Integer> COOLDOWNS = Map.of(
            // Slime
            PlayerClass.SPRINTER, 20 * Util.SPRINTER_ABILITY_COOLDOWN,
            PlayerClass.HUNTER, 20 * Util.HUNTER_ABILITY_COOLDOWN,
            PlayerClass.SUPPORT, 20 * Util.SUPPORT_ABILITY_COOLDOWN
    );

    public static void useAbility(ServerPlayerEntity player, PlayerData playerData) {
        long time = player.getServerWorld().getTime();

        if (playerData.isOnCooldown(time)) return;

        PlayerClass playerClass = playerData.getPlayerClass();
        switch (playerClass) {
            case PlayerClass.SPRINTER:
                sprinterAbility(player);
                break;
            case PlayerClass.HUNTER:
                hunterAbility(player);
                break;
            case PlayerClass.SUPPORT:
                if (!supportAbility(player)) return;
                break;
            default:
                return;
        }

        // Set ability cooldown
        if (COOLDOWNS.containsKey(playerClass)) {
            if (!player.isCreative()) playerData.setCooldown(time, COOLDOWNS.get(playerClass));
            else playerData.setCooldown(time, 20 * 2);
        }
        SAComponents.PLAYER_DATA.sync(player);

    }

    public static void sprinterAbility(ServerPlayerEntity player) {
        // Dash
        Vec3d look = player.getRotationVec(1.0F);
        double strength = Util.SPRINTER_DASH_STRENGTH;
        player.addVelocity(look.x * strength, look.y * strength / 1.5, look.z * strength);
        player.velocityModified = true;
        // Effects
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                20 * Util.SPRINTER_SPEED_EFFECT_DURATION, Util.SPRINTER_SPEED_EFFECT_AMPLIFIER,
                true, true, true));
    }

    public static void hunterAbility(ServerPlayerEntity player) {
        List<LivingEntity> entities = player.getServerWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(Util.HUNTER_ABILITY_RADIUS), LivingEntity::isAlive);
        for(LivingEntity target : entities) {
            if (target instanceof PlayerEntity playerEntity) {
                PlayerData playerData = SAComponents.PLAYER_DATA.get(playerEntity);
                if (playerData.getPlayerTeam() == PlayerTeam.SLIME) continue;
            }

            target.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING,
                    20 * Util.HUNTER_ABILITY_DURATION, 0,
                    true, false
            ));
        }

    }

    public static boolean supportAbility(ServerPlayerEntity player) {
        EntityHitResult hitResult = Util.getTargetedPlayer(player, Util.SUPPORT_ABILITY_RANGE);
        if (hitResult == null || hitResult.getEntity() == null || !(hitResult.getEntity() instanceof ServerPlayerEntity target)) return false;
        PlayerData targetData = SAComponents.PLAYER_DATA.get(target);
        if (targetData.getPlayerTeam() == PlayerTeam.SLIME) return false;

        // Spawn beam
        Util.spawnParticleBeam(player.getServerWorld(),
                EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, StatusEffects.SLOWNESS.value().getColor()),
                player.getCameraPosVec(1.0F), hitResult.getPos(), 0.44);

        // Apply slowness effect
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,
                20 * Util.SUPPORT_ABILITY_SLOWNESS_EFFECT_DURATION,
                Util.SUPPORT_ABILITY_SLOWNESS_EFFECT_AMPLIFIER,
                false, true, true));

        return true;
    }

}
