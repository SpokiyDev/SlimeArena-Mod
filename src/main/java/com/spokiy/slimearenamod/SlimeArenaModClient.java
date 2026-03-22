package com.spokiy.slimearenamod;

import com.spokiy.slimearenamod.event.SAClientEvents;
import com.spokiy.slimearenamod.event.SAKeyInputHandler;
import com.spokiy.slimearenamod.networking.SAMessages;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimeArenaModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SAKeyInputHandler.register();
		SAClientEvents.register();
//		SAMessages.registerS2CPackets();

	}
}