package com.spokiy.slimearenamod.data;

import com.spokiy.slimearenamod.SlimeArenaMod;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class SAComponents implements EntityComponentInitializer, WorldComponentInitializer {
    public static final ComponentKey<PlayerData> PLAYER_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    SlimeArenaMod.prefix("player_data"),
                    PlayerData.class
            );

    public static final ComponentKey<WorldData> WORLD_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    SlimeArenaMod.prefix("world_data"),
                    WorldData.class
            );


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                PLAYER_DATA,
                player -> new PlayerData(),
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(WORLD_DATA, world -> new WorldData());
    }
}