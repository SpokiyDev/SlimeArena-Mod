package com.spokiy.slimearenamod.util.shop;

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
    public ShopItem(ItemStack item, Integer price) {
        this(item, price, null, null);
    }
    // item + price + name
    public ShopItem(Item item, Integer price) {
        this(new ItemStack(item), price, null, null);
    }

    // itemStack + price + name
    public ShopItem(ItemStack item, Integer price, Text name) {
        this(item, price, name, null);
    }
    // item + price + name
    public ShopItem(Item item, Integer price, Text name) {
        this(new ItemStack(item), price, name, null);
    }

    // item + price + name
    public ShopItem(Item item, Integer price, Text name, List<Text> lore) {
        this(new ItemStack(item), price, name, lore);
    }
    // FULL constructor (main)
    public ShopItem(ItemStack stack, Integer price, Text name, List<Text> lore) {
        this.stack = stack;
        this.price = price;
        this.name = name;
        this.lore = lore;
    }
}
