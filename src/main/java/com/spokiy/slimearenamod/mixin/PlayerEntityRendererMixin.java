package com.spokiy.slimearenamod.mixin;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.component.SAComponents;
import com.spokiy.slimearenamod.component.PlayerData;
import com.spokiy.slimearenamod.enums.PlayerClass;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(
            method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void slimearena$getTexture(AbstractClientPlayerEntity player,
                                       CallbackInfoReturnable<Identifier> cir) {
        PlayerData data = SAComponents.PLAYER_DATA.get(player);

        PlayerClass playerClass = data.getPlayerClass();
        Identifier texture = null;

        switch (playerClass) {
            case PlayerClass.SLIME:
                texture = SlimeArenaMod.prefix("textures/player/slime.png");
                break;
            case PlayerClass.SUPPORT:
                texture = SlimeArenaMod.prefix("textures/player/slime_support.png");
                break;

        }

        if (texture != null) cir.setReturnValue(texture);

    }
}