package com.spokiy.slimearenamod.mixin.server;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Inject(method = "update", at = @At("HEAD"))
    private void alwaysFull(PlayerEntity player, CallbackInfo ci) {
        HungerManager hunger = (HungerManager)(Object)this;

        hunger.setFoodLevel(20);
        hunger.setSaturationLevel(20.0f);
        hunger.setExhaustion(0.0f);

//        ci.cancel();
    }
}