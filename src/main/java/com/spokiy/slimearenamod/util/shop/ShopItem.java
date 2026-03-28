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
    public ShopItem(ItemStack stack, Integer price) {
        this(stack, price, null, null);
    }
    // item + price + name
    public ShopItem(Item item, Integer price) {
        this(new ItemStack(item), price, null, null);
    }

    // itemStack + price + name
    public ShopItem(ItemStack stack, Integer price, Text name) {
        this(stack, price, name, null);
    }
    // item + price + name
    public ShopItem(Item item, Integer price, Text name) {
        this(new ItemStack(item), price, name, null);
    }

    // itemStack + price + lore
    public ShopItem(ItemStack stack, Integer price,List<Text> lore) {
        this(stack, price, null, lore);
    }
    // item + price + lore
    public ShopItem(Item item, Integer price, List<Text> lore) {
        this(new ItemStack(item), price, null, lore);
    }

    // item + price + quickLore
    public static ShopItem withQuickLore(ItemStack stack, Integer price) {
        return new ShopItem(stack, price, null, Util.quickLore(stack, ShopUtil.getLoreColorByPrice(price)));
    }
    // item + price + quickLore
    public static ShopItem withQuickLore(Item item, Integer price) {
        return new ShopItem(new ItemStack(item), price, null, Util.quickLore(item, ShopUtil.getLoreColorByPrice(price)));
    }

    // item + price + quickLore
    public static ShopItem withQuickLore(int lines, ItemStack stack, Integer price) {
        return new ShopItem(stack, price, null, Util.quickLore(stack, ShopUtil.getLoreColorByPrice(price), lines));
    }
    // item + price + quickLore
    public static ShopItem withQuickLore(int lines, Item item, Integer price) {
        return new ShopItem(new ItemStack(item), price, null, Util.quickLore(item, ShopUtil.getLoreColorByPrice(price), lines));
    }

    // FULL constructor
    public ShopItem(ItemStack stack, Integer price, Text name, List<Text> lore) {
        this.stack = stack;
        this.price = price;
        this.name = name;
        this.lore = lore;
    }
    // FULL constructor
    public ShopItem(Item item, Integer price, Text name, List<Text> lore) {
        this(new ItemStack(item), price, name, lore);
    }

}
