package com.spokiy.slimearenamod.util.class_selection;


import com.spokiy.slimearenamod.data.PlayerClass;
import com.spokiy.slimearenamod.util.shop.ShopCategory;
import com.spokiy.slimearenamod.util.shop.ShopItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public record ClassSelectionItem(
        ItemStack stack,
        PlayerClass playerClass,
        int loreLines){

    public static ClassSelectionItem create(Item item, PlayerClass playerClass, int loreLines) {
        return new ClassSelectionItem(new ItemStack(item), playerClass, loreLines);
    }

    // FULL constructor
    public ClassSelectionItem(ItemStack stack, PlayerClass playerClass, int loreLines) {
        this.stack = stack;
        this.playerClass = playerClass;
        this.loreLines = loreLines;
    }

    public String getTranslationKey() {
        return "class.slimearenamod." + playerClass().name().toLowerCase();
    }

}
