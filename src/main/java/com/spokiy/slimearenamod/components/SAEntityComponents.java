package com.spokiy.slimearenamod.components;

import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class SAEntityComponents implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                SAComponents.PLAYER_DATA,
                player -> new PlayerData(),
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}