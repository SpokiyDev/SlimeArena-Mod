package com.spokiy.slimearenamod.event;

import com.spokiy.slimearenamod.networking.SAC2SPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;


public class SAKeyInputHandler {

    public static final String KEY_CATEGORY = "key.slimearenamod.category";
    public static final String KEY_ABILITY = "key.slimearenamod.ability";

    public static KeyBinding abilityKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (abilityKey.wasPressed()) {

                if (client.player == null) return;

                ClientPlayNetworking.send(new SAC2SPayload(client.player.getBlockPos()));
            }
        });
    }

    public static void register() {
        abilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ABILITY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_ENTER,
                KEY_CATEGORY
        ));

        registerKeyInputs();
    }
}