package com.spokiy.slimearenamod.networking;

import com.spokiy.slimearenamod.data.PlayerData;
import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.networking.packet.ChangeYawPayload;
import com.spokiy.slimearenamod.world.entity.DrivableMinecartEntity;
import com.spokiy.slimearenamod.networking.packet.JumpInputPayload;
import com.spokiy.slimearenamod.networking.packet.KeyPressPayload;
import com.spokiy.slimearenamod.util.SAAbilities;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class SAMessages {
    public static void registerC2S() {
        PayloadTypeRegistry.playC2S().register(KeyPressPayload.ID, KeyPressPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(JumpInputPayload.ID, JumpInputPayload.CODEC);
    }
    public static void registerS2C() {
        PayloadTypeRegistry.playS2C().register(ChangeYawPayload.ID, ChangeYawPayload.CODEC);
    }

    public static void register() {
        registerC2S();
        registerS2C();

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

        ClientPlayNetworking.registerGlobalReceiver(
                ChangeYawPayload.ID,
                ((payload, context) -> {
                    PlayerEntity player = context.player();

                    if (player != null) {
                        float yaw = player.getYaw() + 180.0F;
                        player.setYaw(yaw);
                        player.setHeadYaw(yaw);
                    }
                })
        );

    }

}
