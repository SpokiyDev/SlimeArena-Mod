package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SATags {
    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, SlimeArenaMod.prefix(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ARENA_ITEMS = createTag("arena_items");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, SlimeArenaMod.prefix(name));
        }
    }

}
