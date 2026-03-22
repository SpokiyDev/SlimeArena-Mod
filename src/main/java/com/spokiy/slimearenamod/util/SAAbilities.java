package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.component.PlayerData;
import com.spokiy.slimearenamod.component.SAComponents;
import com.spokiy.slimearenamod.enums.PlayerClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Map;

public class SAAbilities {
    public static final Map<PlayerClass, Integer> COOLDOWNS = Map.of(
            // Slime
            PlayerClass.SLIME, 20 * 5
    );

    public static void useAbility(ServerPlayerEntity player, PlayerData playerData) {
        long time = player.getServerWorld().getTime();

        if (playerData.isOnCooldown(time)) return;

        PlayerClass playerClass = playerData.getPlayerClass();
        switch (playerClass) {
            case PlayerClass.SLIME:
                slimeAbility(player);
                break;
            default:
                return;
        }

        // Set ability cooldown
        if (COOLDOWNS.containsKey(playerClass)) playerData.setCooldown(time, COOLDOWNS.get(playerClass));
        SAComponents.PLAYER_DATA.sync(player);

    }

    public static void slimeAbility(ServerPlayerEntity player) {
        List<MobEntity> entityList = player.getServerWorld().getEntitiesByClass(MobEntity.class, player.getBoundingBox().expand(10D), LivingEntity::isAlive);
        for(LivingEntity livingEntity : entityList) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 5, 0, true, false, true), player);
        }

    }

}
