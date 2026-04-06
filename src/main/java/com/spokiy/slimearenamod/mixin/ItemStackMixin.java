package com.spokiy.slimearenamod.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "hasGlint",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void onHasGlint(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isOf(Blocks.PUMPKIN.asItem())) cir.setReturnValue(false);
    }

    @Inject(method = "decrementUnlessCreative",
            at = @At(value = "HEAD", target = "Lnet/minecraft/entity/LivingEntity;isInCreativeMode()Z"),
            cancellable = true)
    private void onDecrementUnlessCreative(int amount, LivingEntity entity, CallbackInfo ci) {
        if (entity.getWorld().isClient || entity.getCommandTags().contains("training")) {
            ci.cancel();
        }
    }

}
