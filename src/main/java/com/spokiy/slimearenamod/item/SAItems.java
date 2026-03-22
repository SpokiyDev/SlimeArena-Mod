package com.spokiy.slimearenamod.item;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SAItems {
    public static final Item MY_ITEM = registerItem("my_item", new Item(new Item.Settings()));


    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SlimeArenaMod.MOD_ID, name), item);
    }

    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Mod Items for" + SlimeArenaMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(MY_ITEM);
        });

    }
}
