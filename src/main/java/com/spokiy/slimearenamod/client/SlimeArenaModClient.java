package com.spokiy.slimearenamod.client;

import com.spokiy.slimearenamod.client.render.SAEntityRenderers;
import com.spokiy.slimearenamod.event.SAClientEvents;
import com.spokiy.slimearenamod.event.SAKeyInputHandler;
import net.fabricmc.api.ClientModInitializer;

public class SlimeArenaModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SAKeyInputHandler.register();
		SAClientEvents.register();

		SAEntityRenderers.register();
//		SAMessages.registerS2CPackets();

	}
}