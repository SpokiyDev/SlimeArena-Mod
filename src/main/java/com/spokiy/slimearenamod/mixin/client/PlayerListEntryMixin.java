package com.spokiy.slimearenamod.mixin.client;

import com.spokiy.slimearenamod.data.PlayerClass;
import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void slimearena$overrideSkin(CallbackInfoReturnable<SkinTextures> cir) {
        PlayerListEntry entry = (PlayerListEntry)(Object)this;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        PlayerEntity player = client.world.getPlayerByUuid(entry.getProfile().getId());
        if (player == null) return;

        PlayerClass playerClass = SAComponents.PLAYER_DATA.get(player).getPlayerClass();
        Identifier texture = Util.getTextureByClass(playerClass);

        if (texture != null) {
            SkinTextures original = cir.getReturnValue();
            cir.setReturnValue(new SkinTextures(
                    texture,
                    original.textureUrl(),
                    original.capeTexture(),
                    original.elytraTexture(),
                    original.model(),
                    original.secure()
            ));
        }
    }
}