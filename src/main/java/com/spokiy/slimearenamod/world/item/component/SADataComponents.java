package com.spokiy.slimearenamod.world.item.component;

import com.mojang.serialization.Codec;
import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SADataComponents {
    public static final ComponentType<String> UUID_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            SlimeArenaMod.prefix("uuid_component"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Mod Items for " + SlimeArenaMod.MOD_ID);

    }
}
