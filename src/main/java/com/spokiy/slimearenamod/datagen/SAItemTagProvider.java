package com.spokiy.slimearenamod.datagen;

import com.spokiy.slimearenamod.world.item.SAItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class SAItemTagProvider extends FabricTagProvider<Item> {
    public SAItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, Registries.ITEM.getKey(), registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(SATags.Items.ARENA_ITEMS)
                // Items
                .add(Items.EMERALD)

                .add(Items.SNOWBALL)
                .add(Items.SLIME_BALL)
                .add(Items.PUFFERFISH)
                .add(Items.FIRE_CHARGE)
                .add(Items.WIND_CHARGE)
                .add(Items.ECHO_SHARD)
                .add(Items.ENDER_PEARL)

                .add(Items.POTION)
                .add(Items.SPLASH_POTION)

                .add(Items.GOAT_HORN)

                // Blocks
                .add(Items.HONEY_BLOCK)
                .add(Items.TNT)
                .add(Items.PUMPKIN)
                .add(Items.CAKE)
                .add(Items.HEAVY_CORE)


                // Custom
                .add(SAItems.SLIME_TRAP)
                .add(SAItems.DRIVABLE_MINECART)
                .add(SAItems.VACCINE);

    }
}
