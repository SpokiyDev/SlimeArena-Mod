package com.spokiy.slimearenamod.mixin.client;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.components.PlayerData;
import com.spokiy.slimearenamod.components.SAComponents;
import com.spokiy.slimearenamod.components.PlayerTeam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
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
            if (playerData.getPlayerTeam() == PlayerTeam.NONE) return;

            cir.setReturnValue(player.getJumpingMount() == null && playerData.hasCooldown());
        }
    }

    @Inject(method = "renderArmor",
            at = @At("HEAD"), cancellable = true
    )
    private static void cancelArmorBar(DrawContext context, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        if (playerData.getPlayerTeam() != PlayerTeam.NONE) ci.cancel();
    }


    @Inject(method = "renderFood",
            at = @At("HEAD"), cancellable = true
    )
    private static void cancelFoodBar(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        if (playerData.getPlayerTeam() != PlayerTeam.NONE) ci.cancel();
    }

//    @Inject(method = "renderStatusBars",
//            at = @At("HEAD"), cancellable = true
//    )
//    private void cancelBars(DrawContext context, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        ClientPlayerEntity player = client.player;
//        if (player != null) {
//            PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
//            if (playerData.getPlayerTeam() != PlayerTeam.NONE) ci.cancel();
//        }
//    }

}