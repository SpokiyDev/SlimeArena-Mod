package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.components.PlayerData;
import com.spokiy.slimearenamod.components.SAComponents;
import com.spokiy.slimearenamod.components.PlayerClass;
import com.spokiy.slimearenamod.components.PlayerTeam;
import com.spokiy.slimearenamod.world.item.SlimeTrapItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;

public class SAAbilities {
    public static final Map<PlayerClass, Integer> COOLDOWNS = Map.of(
            // Slime
            PlayerClass.SPRINTER, 20 * Config.SPRINTER_ABILITY_COOLDOWN,
            PlayerClass.HUNTER, 20 * Config.HUNTER_ABILITY_COOLDOWN,
            //PlayerClass.TRAPPER, 20 * Config.TRAPPER_ABILITY_DURATION,
            PlayerClass.SUPPORT, 20 * Config.SUPPORT_ABILITY_COOLDOWN
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
//            case PlayerClass.TRAPPER:
//                trapperAbility(player);
//                break;
            case PlayerClass.SUPPORT:
                supportAbility(player);
                break;
            default:
                return;
        }

        // Set ability cooldown
        if (COOLDOWNS.containsKey(playerClass)) {
            if (!player.isCreative()) playerData.setCooldown(time, COOLDOWNS.get(playerClass));
            else {
                switch (playerClass) {
                    case PlayerClass.SPRINTER:

                    default:
                        playerData.setCooldown(time, 20 * 2);
                }
            }
        }
        SAComponents.PLAYER_DATA.sync(player);

    }

    public static void sprinterAbility(ServerPlayerEntity player) {
        // Dash
        Vec3d look = player.getRotationVec(1.0F);
        double strength = Config.SPRINTER_DASH_STRENGTH;
        player.addVelocity(look.x * strength, look.y * strength / 1.5, look.z * strength);
        player.velocityModified = true;
        // Effects
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                20 * Config.SPRINTER_SPEED_EFFECT_DURATION, Config.SPRINTER_SPEED_EFFECT_AMPLIFIER,
                false, true, true));
    }

    public static void hunterAbility(ServerPlayerEntity player) {

        player.addStatusEffect(Config.HUNTER_ABILITY_BUFF.create());

        List<LivingEntity> entities = player.getServerWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(Config.HUNTER_ABILITY_RADIUS), LivingEntity::isAlive);
        for(LivingEntity target : entities) {
            if (target instanceof PlayerEntity playerEntity) {
                PlayerData playerData = SAComponents.PLAYER_DATA.get(playerEntity);
                if (playerData.getPlayerTeam() == PlayerTeam.SLIME) continue;
            }
            target.addStatusEffect(Config.HUNTER_ABILITY_EFFECT.create());
        }

    }

    public static void trapperAbility(ServerPlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof SlimeTrapItem trapItem) {

        }

    }

    public static void supportAbility(ServerPlayerEntity player) {
        EntityHitResult hitResult = Util.getTargetedEntity(player, Config.SUPPORT_ABILITY_RANGE);
        if (hitResult == null || hitResult.getEntity() == null) return;
        if (!(hitResult.getEntity() instanceof LivingEntity target)) return;

        String targetType = "enemy";
        if (target instanceof PlayerEntity) {
            PlayerData targetData = SAComponents.PLAYER_DATA.get(target);
            if (targetData.getPlayerTeam() == PlayerTeam.SLIME) targetType = "ally";
        }

        // Create particles beam
        Util.spawnParticleBeam(player.getServerWorld(),
                Config.SUPPORT_ABILITY_BEAM_PARTICLES.get(targetType),
                player.getCameraPosVec(1.0F), hitResult.getPos(), 0.44);

        // Apply effect
        target.addStatusEffect(Config.SUPPORT_ABILITY_SLOWNESS_EFFECTS.get(targetType).create());

    }

}
