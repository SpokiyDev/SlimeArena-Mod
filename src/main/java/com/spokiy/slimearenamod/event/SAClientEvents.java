package com.spokiy.slimearenamod.event;

import com.spokiy.slimearenamod.component.PlayerData;
import com.spokiy.slimearenamod.component.SAComponents;
import com.spokiy.slimearenamod.util.SAAbilities;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class SAClientEvents {
    static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;
            PlayerData playerData = SAComponents.PLAYER_DATA.get(client.player);

            if (SAAbilities.COOLDOWNS.containsKey(playerData.getPlayerClass())) {
                long time = client.world.getTime();


                int remaining = playerData.getCooldown(time);
                int max = SAAbilities.COOLDOWNS.get(playerData.getPlayerClass());

                client.player.experienceProgress = 1.0f - ((float) remaining / max);
            }
            else client.player.experienceProgress = 1.0f;
        });
    }
}
