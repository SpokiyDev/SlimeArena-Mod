package com.spokiy.slimearenamod.networking;

import com.spokiy.slimearenamod.component.PlayerData;
import com.spokiy.slimearenamod.component.SAComponents;
import com.spokiy.slimearenamod.util.SAAbilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class SAMessages {
    public static void registerC2S() {
        PayloadTypeRegistry.playC2S().register(SAC2SPayload.ID, SAC2SPayload.CODEC);
    }

    public static void register() {
        registerC2S();

        ServerPlayNetworking.registerGlobalReceiver(
                SAC2SPayload.ID,
                (payload, context) -> {
                    context.server().execute(() ->  {
                        ServerPlayerEntity player = context.player();
                        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);

                        SAAbilities.useAbility(player, playerData);
                    });
                }
        );
    }

}
