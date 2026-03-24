package com.spokiy.slimearenamod.mixin.client;

import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V"))
    private void redirectSetColor(OutlineVertexConsumerProvider provider, int r, int g, int b, int a) {
        int newR = 255;
        int newG = 0;
        int newB = 0;
        provider.setColor(newR, newG, newB, a);
    }
}