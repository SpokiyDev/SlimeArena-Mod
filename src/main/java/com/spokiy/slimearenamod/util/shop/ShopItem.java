package com.spokiy.slimearenamod.util.shop;

import com.spokiy.slimearenamod.util.Util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import java.util.List;

public record ShopItem(
        ItemStack stack,
        Integer price,
        Text name,
        List<Text> lore){

    // itemStack + price only
    public static ShopItem create(ItemStack stack, Integer price) {
        return new ShopItem(stack, price, null, null);
    }

    // itemStack + price + name
    public static ShopItem create(ItemStack stack, Integer price, Text name) {
        return new ShopItem(stack, price, name, null);
    }

    // itemStack + price + lore
    public static ShopItem create(ItemStack stack, Integer price,List<Text> lore) {
        return new ShopItem(stack, price, null, lore);
    }

    // item + price + quickLore
    public static ShopItem withQuickLore(ItemStack stack, Integer price) {
        return new ShopItem(stack, price, null, Util.quickLore(stack, ShopUtil.getLoreColorByPrice(price)));
    }
    // item + price + quickLore
    public static ShopItem withQuickLore(int lines, ItemStack stack, Integer price) {
        return new ShopItem(stack, price, null, Util.quickLore(stack, ShopUtil.getLoreColorByPrice(price), lines));
    }

    // FULL constructor
    public ShopItem(ItemStack stack, Integer price, Text name, List<Text> lore) {
        this.stack = stack;
        this.price = price;
        this.name = name;
        this.lore = lore;
    }

}
