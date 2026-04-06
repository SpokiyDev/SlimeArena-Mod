package com.spokiy.slimearenamod.datagen;

import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class SAEntityTypeTagProvider extends FabricTagProvider<EntityType<?>> {
    public SAEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, Registries.ENTITY_TYPE.getKey(), registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(SAEntities.SLIME_TARGET);

        getOrCreateTagBuilder(EntityTypeTags.NON_CONTROLLING_RIDER)
                .add(SAEntities.SLIME_TARGET);

    }
}
