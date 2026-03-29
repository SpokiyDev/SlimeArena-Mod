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

    private static final ShopItem DECORATIVE_SLOT =
            ShopItem.create(new ItemStack(Items.GRAY_STAINED_GLASS_PANE), null, List.of());
//    private static final ShopItem SELL_ALL_SLOT = new ShopItem(Items.BARRIER, null,
//            Text.translatable("shop.slimearenamod.sell_all").setStyle(shopStyle(Formatting.RED)), List.of());

    public static final Map<ShopCategory, Map<Integer, ShopItem>> SHOP_ITEMS = createShop();
    private static Map<ShopCategory, Map<Integer, ShopItem>> createShop() {
        Map<ShopCategory, Map<Integer, ShopItem>> shopMap = new HashMap<>();

        shopMap.put(ShopCategory.THROWABLES, createThrowables());

        return shopMap;
    }

    public static void fillShopSlot(Inventory inventory, ShopCategory shopCategory, int slotIndex) {
        ShopItem shopItem = SHOP_ITEMS.get(shopCategory).get(slotIndex);
        if (shopItem == null) return;

        ItemStack stack = prepareShopStack(shopItem, true);

        // Add the item to Shop
        inventory.setStack(slotIndex, stack);
    }

    private static void createDecorative(Map<Integer, ShopItem> map) {
        // Fill with decorative slots
        final int rows = ShopMenu.ROWS;
        final int cols = ShopMenu.SLOTS / ShopMenu.ROWS;
        for (int slot = 0; slot < rows * cols; slot++) {
            int row = slot / cols;
            int col = slot % cols;
            if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) map.put(slot, DECORATIVE_SLOT);
        }

    }

    private static Map<Integer, ShopItem> createThrowables() {
        Map<Integer, ShopItem> map = new HashMap<>();
        createDecorative(map);

        // Row 1
        map.put(10, ShopItem.create(new ItemStack(Items.SNOWBALL, 4), 1));
        map.put(11, ShopItem.create(new ItemStack(Items.FIRE_CHARGE, 2), 1));
        map.put(12, ShopItem.create(new ItemStack(Items.SLIME_BALL, 3), 1));
        map.put(13, ShopItem.create(new ItemStack(Items.PUFFERFISH), 1));
        map.put(14, ShopItem.create(new ItemStack(Items.CHORUS_FRUIT), 2));
        map.put(15, ShopItem.create(Util.customPotion(Items.SPLASH_POTION, StatusEffects.SLOWNESS, 20 * 15, 1), 2));
        map.put(16, ShopItem.create(new ItemStack(Items.HONEY_BLOCK), 2));

        // Row 2
        map.put(19, ShopItem.create(new ItemStack(Items.WIND_CHARGE, 2), 2));
        map.put(20, ShopItem.withQuickLore(new ItemStack(Items.TNT, 3), 3));
        map.put(21, ShopItem.create(new ItemStack(Items.PUMPKIN), 3));
        map.put(22, ShopItem.create(new ItemStack(Items.ENDER_PEARL), 4));
        map.put(23, ShopItem.withQuickLore(2, new ItemStack(Items.ECHO_SHARD, 2), 4));
        map.put(24, ShopItem.withQuickLore(new ItemStack(Items.HEAVY_CORE), 4));

        // Row 3
        map.put(28, ShopItem.create(Util.customPotion(Items.POTION, StatusEffects.JUMP_BOOST, 20 * 20, 1), 1));
        map.put(29, ShopItem.create(Util.customPotion(Items.POTION, StatusEffects.SPEED, 20 * 15, 1), 2));
        map.put(30, ShopItem.create(Util.customPotion(Items.POTION, StatusEffects.SLOW_FALLING, 20 * 10, 0), 2));
        map.put(31, ShopItem.create(Util.customPotion(Items.POTION, StatusEffects.LEVITATION, 20 * 8, 2), 3));
        map.put(32, ShopItem.create(Util.customPotion(Items.POTION, StatusEffects.INVISIBILITY, 20 * 8, 0, false,
                List.of(Text.translatable("item.slimearenamod.potion.invisibility.lore"))), 4));

        // Row 4
        map.put(37, ShopItem.withQuickLore(new ItemStack(Items.GOAT_HORN), 6));

        return map;
    }


    public static ItemStack prepareShopStack(ShopItem shopItem, boolean isGUI) {
        ItemStack stack = shopItem.stack().copy();

        // NAME
        Text newName = Text.empty()
                .formatted(getNameColorByPrice(shopItem.price()))
                .append(shopItem.name() != null
                        ? shopItem.name()
                        : shopItem.stack().getName())
                .append(isGUI && shopItem.stack().getCount() > 1
                        ? Text.literal(" [x%d]".formatted(shopItem.stack().getCount()))
                        : Text.empty())
                .append(isGUI && shopItem.price() != null
                        ? Text.literal(" — %d$".formatted(shopItem.price()))
                        : Text.empty());

        stack.set(DataComponentTypes.ITEM_NAME, newName);

        // LORE
        List<Text> lore = new ArrayList<>();

        LoreComponent loreComp = shopItem.stack().get(DataComponentTypes.LORE);

        if (shopItem.lore() != null) lore.addAll(shopItem.lore());
        if (loreComp != null) lore.addAll(loreComp.lines());
        if (isGUI && shopItem.price() != null) lore.addAll(SELL_BUY_TIP);

        stack.set(DataComponentTypes.LORE, new LoreComponent(lore));

        return stack;
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

        a.remove(DataComponentTypes.ITEM_NAME);
        a.remove(DataComponentTypes.LORE);

        b.remove(DataComponentTypes.ITEM_NAME);
        b.remove(DataComponentTypes.LORE);

        return ItemStack.areItemsAndComponentsEqual(a, b);
    }

    public static Formatting getNameColorByPrice(Integer price) {
        return price == null
                || price < 3 ? Formatting.WHITE
                : price < 6 ? Formatting.YELLOW
                : price < 9 ? Formatting.AQUA
                : Formatting.LIGHT_PURPLE;
    }
    public static Formatting getLoreColorByPrice(Integer price) {
        return price == null
                || price < 3 ? Formatting.GRAY
                : price < 6 ? Formatting.GREEN
                : price < 9 ? Formatting.YELLOW
                : Formatting.AQUA;
    }

}

