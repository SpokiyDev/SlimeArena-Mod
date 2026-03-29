package com.spokiy.slimearenamod.client;

import com.spokiy.slimearenamod.client.render.SAEntityRenderers;
import com.spokiy.slimearenamod.event.SAClientEvents;
import com.spokiy.slimearenamod.event.SAKeyInputHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.math.Vec3d;

public class SlimeArenaModClient implements ClientModInitializer {
	public static Vec3d mageTeleportPoint;

	@Override
	public void onInitializeClient() {
		SAKeyInputHandler.register();
		SAEntityRenderers.register();
//		SAMessages.registerS2CPackets();

		SAClientEvents.register();

	}
}