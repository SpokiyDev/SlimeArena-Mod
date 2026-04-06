package com.spokiy.slimearenamod.mixin.server;

import com.spokiy.slimearenamod.world.entity.SlimeTargetEntity;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SlimeEntity.class)
public class SlimeEntityMixin {

    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/SlimeEntity;getSize()I"))
    private int onRemove(SlimeEntity instance) {
        return instance instanceof SlimeTargetEntity ? 0 : instance.getSize();
    }
}
