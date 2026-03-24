package com.spokiy.slimearenamod.components;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;

public class SAComponents {
    public static final ComponentKey<PlayerData> PLAYER_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    Identifier.of("slimearenamod:player_data"),
                    PlayerData.class
            );
}