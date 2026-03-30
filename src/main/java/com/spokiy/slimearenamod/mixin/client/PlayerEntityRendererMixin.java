package com.spokiy.slimearenamod.mixin.client;

import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.data.PlayerData;
import com.spokiy.slimearenamod.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(
            method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void slimearena$getTexture(AbstractClientPlayerEntity player, CallbackInfoReturnable<Identifier> cir) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        Identifier texture = Util.getTextureByClass(playerData.getPlayerClass());

        if (texture != null) cir.setReturnValue(texture);

    }
    @Redirect(
            method = "renderArm",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/SkinTextures;texture()Lnet/minecraft/util/Identifier;"
            )
    )
    private Identifier slimearena$overrideArmTexture(SkinTextures skinTextures) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return null;

        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        Identifier texture = Util.getTextureByClass(playerData.getPlayerClass());

        return texture != null ? texture : skinTextures.texture();
    }

}