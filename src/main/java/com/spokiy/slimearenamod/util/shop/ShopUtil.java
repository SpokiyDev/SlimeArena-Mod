package com.spokiy.slimearenamod.util.shop;

import com.spokiy.slimearenamod.util.Util;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class ShopUtil {
    private static final List<Text> SELL_BUY_TIP = List.of(Text.empty(),
            Text.translatable("shop.slimearenamod.left_click_to_buy").setStyle(shopStyle(Formatting.GREEN)),
            Text.translatable("shop.slimearenamod.right_click_to_sell").setStyle(shopStyle(Formatting.RED)));

    private static final ShopItem DECORATIVE_SLOT = new ShopItem(Items.GRAY_STAINED_GLASS_PANE, null,
            null, List.of());
//    private static final ShopItem SELL_ALL_SLOT = new ShopItem(Items.BARRIER, null,
//            Text.translatable("shop.slimearenamod.sell_all").setStyle(shopStyle(Formatting.RED)), List.of());

    public static final Map<Integer, ShopItem> SHOP_ITEMS = createShop();
    private static Map<Integer, ShopItem> createShop() {
        Map<Integer, ShopItem> map = new HashMap<>();

        // Fill with decorative slots
        final int rows = ShopMenu.ROWS;
        final int cols = ShopMenu.SLOTS / ShopMenu.ROWS;
        for (int slot = 0; slot < rows * cols; slot++) {
            int row = slot / cols;
            int col = slot % cols;
            if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) map.put(slot, DECORATIVE_SLOT);
        }

        // My items
        // Row 1
        map.put(10, new ShopItem(new ItemStack(Items.SNOWBALL, 4), 1));
        map.put(11, new ShopItem(new ItemStack(Items.SLIME_BALL, 4), 1));
        map.put(12, new ShopItem(new ItemStack(Items.FIRE_CHARGE, 2), 1));
        map.put(13, new ShopItem(new ItemStack(Items.WIND_CHARGE, 2), 1));
        map.put(14, new ShopItem(new ItemStack(Items.TNT, 2), 2));
        map.put(15, new ShopItem(new ItemStack(Items.ECHO_SHARD, 2), 4));
        map.put(16, new ShopItem(new ItemStack(Items.ENDER_PEARL, 1), 4));
        // Row 2
        map.put(20, new ShopItem(Util.customPotion(Items.POTION, StatusEffects.JUMP_BOOST, 20 * 30, 1), 1));
        map.put(19, new ShopItem(Util.customPotion(Items.POTION, StatusEffects.SPEED, 20 * 15, 1), 2));
        map.put(22, new ShopItem(Util.customPotion(Items.POTION, StatusEffects.SLOW_FALLING, 20 * 15, 0), 3));
        map.put(21, new ShopItem(Util.customPotion(Items.POTION, StatusEffects.INVISIBILITY, 20 * 8, 0, false,
                List.of(Text.translatable("item.slimearenamod.potion.invisibility.lore"))), 4));
        map.put(23, new ShopItem(Util.customPotion(Items.SPLASH_POTION, StatusEffects.SLOWNESS, 20 * 5, 1), 3));

        return map;
    }

    public static void fillShopSlot(Inventory inventory, int slotIndex) {
        ShopItem shopItem = SHOP_ITEMS.get(slotIndex);
        if (shopItem == null) return;

        ItemStack stack = shopItem.stack().copy();

        // name
        if (shopItem.name() != null || shopItem.price() != null) {
            Text newName = Text.empty()
                    .setStyle(Style.EMPTY.withItalic(false))
                    .append(shopItem.name() != null
                            ? shopItem.name()
                            : shopItem.stack().getName())
                    .append(shopItem.stack().getCount() > 1
                                    ? Text.literal(" [x%d]".formatted(shopItem.stack().getCount()))
                                    : Text.empty())
                    .append(Text.literal(" — %d$".formatted(shopItem.price())));
            stack.set(DataComponentTypes.CUSTOM_NAME, newName);
        }

        List<Text> lore = new ArrayList<>();

        LoreComponent loreComp = shopItem.stack().get(DataComponentTypes.LORE);
        if (loreComp != null) lore.addAll(loreComp.lines());

        if (shopItem.lore() != null && !shopItem.lore().isEmpty()) {
            lore.addAll(shopItem.lore());
        }
        if (shopItem.lore() == null || !shopItem.lore().isEmpty()) {
            lore.addAll(SELL_BUY_TIP);
        }
        stack.set(DataComponentTypes.LORE, new LoreComponent(lore));

        // Add the item to Shop
        inventory.setStack(slotIndex, stack);
    }

    public static Style shopStyle() {
        return Style.EMPTY.withItalic(false);
    }
    public static Style shopStyle(Formatting color) {
        return Style.EMPTY.withItalic(false).withColor(color);
    }

    public static boolean areTrueComponentsEqual(ItemStack stack1, ItemStack stack2) {
        ItemStack a = stack1.copy();
        ItemStack b = stack2.copy();

        a.remove(DataComponentTypes.CUSTOM_NAME);
        a.remove(DataComponentTypes.LORE);

        b.remove(DataComponentTypes.CUSTOM_NAME);
        b.remove(DataComponentTypes.LORE);

        return ItemStack.areItemsAndComponentsEqual(a, b);
    }

}

