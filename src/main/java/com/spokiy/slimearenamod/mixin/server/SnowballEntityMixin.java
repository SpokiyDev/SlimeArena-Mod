package com.spokiy.slimearenamod.mixin.server;

import net.minecraft.entity.projectile.thrown.SnowballEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SnowballEntity.class)
public class SnowballEntityMixin {

    @ModifyArg(method = "onEntityHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),
            index = 1)
    private float changeDamage(float amount) {
        return 1.0F;
    }

}
