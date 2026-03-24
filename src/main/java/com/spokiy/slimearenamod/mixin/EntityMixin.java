package com.spokiy.slimearenamod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
//    @Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
//    private void modifyTeamColor(CallbackInfoReturnable<Integer> cir) {
//        Entity entity = (Entity) (Object) this;
//        if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(StatusEffects.UNLUCK)) {
//            cir.setReturnValue(0xFFFF0000);
//        }
//    }

}