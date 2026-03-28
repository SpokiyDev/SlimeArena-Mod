package com.spokiy.slimearenamod.util.shop;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.spokiy.slimearenamod.util.shop.ShopUtil.SHOP_ITEMS;

public class ShopMenu extends ScreenHandler {
    public static final int SLOTS = 54;
    public static final int ROWS = 6;
    private final Inventory inventory;
    private ShopCategory shopCategory = ShopCategory.THROWABLES;

    public ShopMenu(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId);

        checkSize(inventory, ROWS * 9);

        this.inventory = inventory;

        inventory.onOpen(playerInventory.player);

        int i = (ROWS - 4) * 18;
        playerInventory.player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT);

        // GUI
        for (int j = 0; j < ROWS; j++) {
            for (int k = 0; k < 9; k++) {
                System.out.println("Category: " + shopCategory);
                System.out.println("Map: " + SHOP_ITEMS.get(shopCategory));

                ShopUtil.fillShopSlot(inventory, shopCategory, k + j * 9);

                this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        return false;
                    }

                });
            }
        }

        // Inventory
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9,
                        8 + k * 18, 103 + j * 18 + i));
            }
        }

        // Hotbar
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j,
                    8 + j * 18, 161 + i));
        }
    }

    public static void open(ServerPlayerEntity player) {
        Inventory inventory = new SimpleInventory(SLOTS);

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, p) ->
                        new ShopMenu(syncId, playerInventory, inventory),
                Text.translatable("shop.slimearenamod.shop_label")
        ));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }


    private int operationCooldown = 0;

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity playerEntity) {
        if (!(playerEntity instanceof ServerPlayerEntity player)) return;
        if (slotIndex < 0 || slotIndex >= (ROWS - 1) * 9) {
            super.onSlotClick(slotIndex, button, actionType, player);
            return;
        }

        // Check cooldown
        if (operationCooldown == 0) executeOperation(slotIndex, button, actionType, player);
        // Set cooldown
        if (button == 0)      operationCooldown = 4;
        else if (button == 1) operationCooldown = 3;

    }
    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        if (operationCooldown > 0) operationCooldown--;
    }

    private void executeOperation(int slotIndex, int button, SlotActionType actionType, ServerPlayerEntity player) {
        Slot slot = this.slots.get(slotIndex);
        ShopItem shopItem = SHOP_ITEMS.get(shopCategory).get(slotIndex);
        Integer price = shopItem.price();
        if (!slot.hasStack() || price == null) return;

        ItemStack shopStack = slot.getStack();

        // BUY
        if (button == 0) {

            int stackSize = shopStack.getCount();
            int playerEmeralds = countItems(player, Items.EMERALD);
            int purchases;

            if (actionType == SlotActionType.QUICK_MOVE) purchases = playerEmeralds / price;
            else                                         purchases = playerEmeralds >= price ? 1 : 0;

            if (purchases <= 0) {
                player.sendMessage(Text.translatable("shop.slimearenamod.not_enough_money")
                        .formatted(Formatting.RED), false);
                return;
            }

            int totalPrice = purchases * price;
            removeItems(player, Items.EMERALD, totalPrice);

            int totalItems = purchases * stackSize;
            ItemStack stack = ShopUtil.prepareShopStack(shopItem, false);
            stack.setCount(totalItems);

            player.giveItemStack(stack);

        }

        // SELL
        if (button == 1) {

            int stackSize = shopStack.getCount();
            int totalItems = countItems(player, shopItem.stack());
            int sales;

            if (actionType == SlotActionType.QUICK_MOVE) sales = totalItems / stackSize;
            else                                         sales = totalItems >= stackSize ? 1 : 0;

            if (sales <= 0) {
                player.sendMessage(Text.translatable("shop.slimearenamod.no_item_to_sell")
                        .formatted(Formatting.RED), false);
                return;
            }

            int itemsToRemove = sales * stackSize;
            removeItems(player, shopStack, itemsToRemove);

            int totalPrice = sales * price;
            player.giveItemStack(new ItemStack(Items.EMERALD, totalPrice));

        }

        player.getWorld().playSound(
                null, player.getBlockPos(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                1.0F, 1.0F
        );
    }

    private int countItems(PlayerEntity player, ItemStack item) {
        int total = 0;
        for (ItemStack stack : player.getInventory().main) if (ShopUtil.areTrueComponentsEqual(stack, item)) total += stack.getCount();
        return total;
    }
    private int countItems(PlayerEntity player, Item item) {
        int total = 0;
        for (ItemStack stack : player.getInventory().main) if (stack.isOf(item)) total += stack.getCount();
        return total;
    }

    private void removeItems(PlayerEntity player,  ItemStack item, int amount) {
        for (ItemStack stack : player.getInventory().main) {
            if (!ShopUtil.areTrueComponentsEqual(stack, item)) continue;

            int remove = Math.min(amount, stack.getCount());
            stack.decrement(remove);

            amount -= remove;
            if (amount <= 0) break;
        }
    }
    private void removeItems(PlayerEntity player,  Item item, int amount) {
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isOf(item)) continue;

            int remove = Math.min(amount, stack.getCount());
            stack.decrement(remove);

            amount -= remove;
            if (amount <= 0) break;
        }
    }

}

