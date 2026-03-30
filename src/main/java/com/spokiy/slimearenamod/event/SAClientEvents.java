package com.spokiy.slimearenamod.event;

import com.spokiy.slimearenamod.client.SlimeArenaModClient;
import com.spokiy.slimearenamod.data.PlayerData;
import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.world.entity.DrivableMinecartEntity;
import com.spokiy.slimearenamod.networking.packet.JumpInputPayload;
import com.spokiy.slimearenamod.util.SAAbilities;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class SAClientEvents {
    static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientWorld world = client.world;
            ClientPlayerEntity player = client.player;
            if (client.world == null || player == null) return;

            // Inputs
            SAKeyInputHandler.tickKeyInputs(world, player);

            // Cooldowns
            PlayerData playerData = SAComponents.PLAYER_DATA.get(player);

            if (SAAbilities.COOLDOWNS.containsKey(playerData.getPlayerClass())) {
                long time = world.getTime();


                int remaining = playerData.getCooldown(time);
                int max = SAAbilities.COOLDOWNS.get(playerData.getPlayerClass()) * 20;

                player.experienceProgress = 1.0f - ((float) remaining / max);
            }
            else player.experienceProgress = 1.0f;

            // Jumping Input
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof DrivableMinecartEntity) {
                if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                    ClientPlayNetworking.send(new JumpInputPayload(vehicle.getId(), true));
                }

            }

        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            Vec3d teleportPoint = SlimeArenaModClient.mageTeleportPoint;
            if (teleportPoint == null) return;

            MatrixStack matrices = context.matrixStack();
            VertexConsumerProvider consumers = context.consumers();
            if (matrices == null || consumers == null) return;

            Vec3d camPos = context.camera().getPos();

            matrices.push();

            matrices.translate(
                    teleportPoint.x - camPos.x,
                    teleportPoint.y - camPos.y,
                    teleportPoint.z - camPos.z
            );

            VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());

            Box box = new Box(0, 0, 0, 1, 0, 1);
            WorldRenderer.drawBox(
                    matrices,
                    buffer,
                    box,
                    1.0F, 0.0F, 1.0F,
                    1.0F
            );

            matrices.pop();
        });
    }
}
