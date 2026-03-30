package com.spokiy.slimearenamod.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.spokiy.slimearenamod.data.PlayerData;
import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.data.PlayerTeam;
import com.spokiy.slimearenamod.util.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyExpressionValue(method = "travel", at = @At(value = "CONSTANT", args = "floatValue=0.02F"))
    private float modifySwimSpeed(float original) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
            if (playerData.getPlayerTeam() == PlayerTeam.SLIME) {
                return original * Config.SLIME_SWIM_SPEED_MULTIPLIER;
            }
        }

        return original;
    }

    @ModifyReturnValue(
            method = "computeFallDamage",
            at = @At("RETURN")
    )
    private int modifyFallDamage(int original) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (self instanceof PlayerEntity) {
            return Math.round((float) original / 1.5F);
        }

        return original;

    }
}