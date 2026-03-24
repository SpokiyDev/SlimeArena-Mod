package com.spokiy.slimearenamod;

import com.spokiy.slimearenamod.entity.SAEntities;
import com.spokiy.slimearenamod.util.SACommands;
import com.spokiy.slimearenamod.event.SAEvents;
import com.spokiy.slimearenamod.item.SAItems;
import com.spokiy.slimearenamod.networking.SAMessages;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimeArenaMod implements ModInitializer {
	public static final String MOD_ID = "slimearenamod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		SAItems.register();
		SAEntities.register();
		SAEvents.register();
		SAMessages.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			SACommands.register(dispatcher);
		});

	}

	public static Identifier prefix(String path) {
		return Identifier.of(MOD_ID, path);
	}

}