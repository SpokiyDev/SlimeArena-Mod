package com.spokiy.slimearenamod.event;

import com.spokiy.slimearenamod.client.SlimeArenaModClient;
import com.spokiy.slimearenamod.components.PlayerClass;
import com.spokiy.slimearenamod.components.SAComponents;
import com.spokiy.slimearenamod.networking.packet.KeyPressPayload;
import com.spokiy.slimearenamod.util.Config;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;


public class SAKeyInputHandler {

    public static final String KEY_CATEGORY = "key.slimearenamod.category";
    public static final String KEY_ABILITY = "key.slimearenamod.ability";

    public static KeyBinding abilityKey;

    private static final Map<KeyBinding, Integer> timePressed = new HashMap<>();

    public static void tickKeyInputs(ClientWorld world, ClientPlayerEntity player) {

        if (abilityKey.isPressed()) {
            timePressed.put(abilityKey, timePressed.getOrDefault(abilityKey, 0) + 1);
            if (player.experienceProgress < 1.0F) return;

            if (timePressed.get(abilityKey) > 3) {
                PlayerClass playerClass = SAComponents.PLAYER_DATA.get(player).getPlayerClass();

                // Mage Slime teleport point
                if (playerClass == PlayerClass.MAGE) {

                    Vec3d start = player.getEyePos();
                    Vec3d direction = player.getRotationVec(1.0F);
                    Vec3d end = start.add(direction.multiply(Config.MAGE_TELEPORT_DISTANCE));

                    HitResult hit = world.raycast(new RaycastContext(
                            start,
                            end,
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE,
                            player
                    ));

                    BlockPos target = null;
                    if (hit.getType() == HitResult.Type.MISS) {
                        target = BlockPos.ofFloored(end);
                    }
                    if (hit instanceof BlockHitResult blockHitResult) {
                        BlockPos blockPos = blockHitResult.getBlockPos();
                        target = blockPos.offset(blockHitResult.getSide());
                    }

                    SlimeArenaModClient.mageTeleportPoint = new Vec3d(target.getX(), hit.getPos().getY(), target.getZ());

                } else SlimeArenaModClient.mageTeleportPoint = null;

            }
        } else {
            if (timePressed.get(abilityKey) > 0 && player.experienceProgress == 1.0F) {
                SlimeArenaModClient.mageTeleportPoint = null;
                ClientPlayNetworking.send(new KeyPressPayload());
            }

            timePressed.put(abilityKey, 0);
        }

    }

    public static void register() {
        abilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ABILITY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_ENTER,
                KEY_CATEGORY
        ));
        timePressed.put(abilityKey, 0);

    }
}