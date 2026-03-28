package com.spokiy.slimearenamod.event;

import com.spokiy.slimearenamod.components.PlayerData;
import com.spokiy.slimearenamod.components.SAComponents;
import com.spokiy.slimearenamod.world.entity.DrivableMinecartEntity;
import com.spokiy.slimearenamod.networking.packet.JumpInputPayload;
import com.spokiy.slimearenamod.util.SAAbilities;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class SAClientEvents {
    static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;

            // Cooldowns
            PlayerData playerData = SAComponents.PLAYER_DATA.get(client.player);

            if (SAAbilities.COOLDOWNS.containsKey(playerData.getPlayerClass())) {
                long time = client.world.getTime();


                int remaining = playerData.getCooldown(time);
                int max = SAAbilities.COOLDOWNS.get(playerData.getPlayerClass());

                client.player.experienceProgress = 1.0f - ((float) remaining / max);
            }
            else client.player.experienceProgress = 1.0f;

            // Jumping Input
            Entity vehicle = client.player.getVehicle();
            if (vehicle instanceof DrivableMinecartEntity) {
                if ( MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                    ClientPlayNetworking.send(new JumpInputPayload(vehicle.getId(), true));
                }

            }
        });
    }
}
