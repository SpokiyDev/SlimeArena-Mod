package com.spokiy.slimearenamod;

import com.spokiy.slimearenamod.util.UseItemUtil;
import com.spokiy.slimearenamod.world.block.SABlocks;
import com.spokiy.slimearenamod.world.effect.SAStatusEffects;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import com.spokiy.slimearenamod.util.SACommands;
import com.spokiy.slimearenamod.event.SAEvents;
import com.spokiy.slimearenamod.world.item.SAItems;
import com.spokiy.slimearenamod.networking.SAMessages;
import com.spokiy.slimearenamod.world.item.component.SADataComponents;
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
		SADataComponents.register();
		SABlocks.register();
		SAEntities.register();
		SAStatusEffects.register();
		SAEvents.register();
		SAMessages.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			SACommands.register(dispatcher);
		});

		UseItemUtil.initActions();

	}

	public static Identifier prefix(String path) {
		return Identifier.of(MOD_ID, path);
	}

}