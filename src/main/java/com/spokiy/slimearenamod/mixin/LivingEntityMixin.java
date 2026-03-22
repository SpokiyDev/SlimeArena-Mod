package com.spokiy.slimearenamod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.spokiy.slimearenamod.component.PlayerData;
import com.spokiy.slimearenamod.component.SAComponents;
import com.spokiy.slimearenamod.enums.PlayerTeam;
import com.spokiy.slimearenamod.util.Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyExpressionValue(method = "travel", at = @At(value = "CONSTANT", args = "floatValue=0.02F"))
    private float modifySwimSpeed(float original) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
            if (playerData.getPlayerTeam() == PlayerTeam.SLIME) {
                return original * Util.SLIME_SWIM_SPEED_MULTIPLIER;
            }
        }

        return original;
    }

//    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
//    private void bounceOnFall(float fallDistance, float damageMultiplier, DamageSource source,
//                              CallbackInfoReturnable<Boolean> cir) {
//        if ((Object)this instanceof ServerPlayerEntity player) {
//            PlayerData data = SAComponents.PLAYER_DATA.get(player);
//
//            if (data.getPlayerTeam() == PlayerTeam.SLIME && source.isOf(DamageTypes.FALL)) {
//                player.sendMessage(Text.of("2"));
//                if (!player.isSneaking()) {
//                    Vec3d vel = player.getVelocity();
//
//                    player.setVelocity(vel.x, fallDistance * 0.5, vel.z);
//                    player.velocityModified = true;
//                }
//
//                cir.setReturnValue(false);
//            }
//        }
//    }
}