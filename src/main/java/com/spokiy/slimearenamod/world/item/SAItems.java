package com.spokiy.slimearenamod.world.item;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class SAItems {
    public static final Item VACCINE = registerItem("vaccine",
            new VaccineItem(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item DRIVABLE_MINECART = registerItem("drivable_minecart",
            new DrivableMinecartItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));

    public static final Item SLIME_TRAP = registerItem("slime_trap",
            new SlimeTrapItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));


    public static Item registerItem(String id, Item item) {
        return Registry.register(Registries.ITEM, SlimeArenaMod.prefix(id), item);
    }

    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Mod Items for " + SlimeArenaMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(VACCINE);
            entries.add(DRIVABLE_MINECART);
            entries.add(SLIME_TRAP);
        });

    }
}
