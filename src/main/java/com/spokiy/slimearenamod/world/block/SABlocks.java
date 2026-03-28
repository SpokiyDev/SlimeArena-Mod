package com.spokiy.slimearenamod.world.block;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SABlocks {
    public static final Block SLIME_LAYER = registerBlock("slime_layer",
            new SlimeLayerBlock(AbstractBlock.Settings.create()));


    private static Block registerBlock(String id, Block block) {
        registerBlockItem(id, block);
        return Registry.register(Registries.BLOCK, SlimeArenaMod.prefix(id), block);
    }

    private static void registerBlockItem(String id, Block block) {
        Registry.register(Registries.ITEM, SlimeArenaMod.prefix(id), new BlockItem(block, new Item.Settings()));
    }

    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Mod Blocks for " + SlimeArenaMod.MOD_ID);

    }
}
