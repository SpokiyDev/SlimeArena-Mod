package com.spokiy.slimearenamod.networking;

import com.spokiy.slimearenamod.components.PlayerData;
import com.spokiy.slimearenamod.components.SAComponents;
import com.spokiy.slimearenamod.entity.DrivableMinecartEntity;
import com.spokiy.slimearenamod.networking.packet.JumpInputPayload;
import com.spokiy.slimearenamod.networking.packet.KeyPressPayload;
import com.spokiy.slimearenamod.util.SAAbilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class SAMessages {
    public static void registerC2S() {
        PayloadTypeRegistry.playC2S().register(KeyPressPayload.ID, KeyPressPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(JumpInputPayload.ID, JumpInputPayload.CODEC);
    }

    public static void register() {
        registerC2S();

        ServerPlayNetworking.registerGlobalReceiver(
                KeyPressPayload.ID,
                (payload, context) -> {
                    context.server().execute(() ->  {
                        ServerPlayerEntity player = context.player();
                        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);

                        SAAbilities.useAbility(player, playerData);
                    });
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(
                JumpInputPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();

                    Entity entity = player.getWorld().getEntityById(payload.entityId());
                    if (entity instanceof DrivableMinecartEntity cart) {
                        cart.jumping = payload.jumping();
                    }

                }
        );
    }

}
