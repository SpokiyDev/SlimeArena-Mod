package com.spokiy.slimearenamod.mixin;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.component.PlayerData;
import com.spokiy.slimearenamod.component.SAComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique
    private static final Identifier COOLDOWN_BAR_BACKGROUND_TEXTURE = SlimeArenaMod.prefix("hud/experience_bar_background");
    @Unique
    private static final Identifier COOLDOWN_BAR_PROGRESS_TEXTURE = SlimeArenaMod.prefix("hud/experience_bar_progress");

    @Inject(method = "shouldRenderExperience", at = @At("HEAD"), cancellable = true)
    private void shouldRenderExperienceMixin(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player != null && player.isCreative())
        {
            PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
            cir.setReturnValue(player.getJumpingMount() == null && playerData.hasCooldown());
        }
    }

}