package com.spokiy.slimearenamod.util.class_selection;

import com.spokiy.slimearenamod.config.Config;
import com.spokiy.slimearenamod.data.PlayerClass;
import com.spokiy.slimearenamod.util.Util;
import com.spokiy.slimearenamod.util.shop.ShopUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassSelectionUtil {
    public static final Map<Integer, ClassSelectionItem> CLASS_SELECTION_ITEMS = Map.of(
            0, ClassSelectionItem.create(Items.LIME_WOOL, PlayerClass.SLIME, 1),
            1, ClassSelectionItem.create(Items.LIGHT_BLUE_WOOL, PlayerClass.SPRINTER, 1),
            2, ClassSelectionItem.create(Items.MAGENTA_WOOL, PlayerClass.MAGE, 2),
            3, ClassSelectionItem.create(Items.RED_WOOL, PlayerClass.HUNTER, 1),
            4, ClassSelectionItem.create(Items.ORANGE_WOOL, PlayerClass.TRAPPER, 1),
            5, ClassSelectionItem.create(Items.YELLOW_WOOL, PlayerClass.SUPPORT, 4)
    );

    public static void fillShopSlot(Inventory inventory, int slotIndex) {
        ClassSelectionItem classItem = CLASS_SELECTION_ITEMS.get(slotIndex);
        if (classItem == null) return;

        // Name
        ItemStack stack = classItem.stack();
        stack.set(DataComponentTypes.ITEM_NAME,
                Text.translatable(classItem.getTranslationKey())
                        .formatted(Config.CLASS_COLORS.get(classItem.playerClass())));
        // Lore
        stack.set(DataComponentTypes.LORE, new LoreComponent(Util.quickLore(classItem.stack())));

        // Add the item to Shop
        inventory.setStack(slotIndex, stack);
    }

}
