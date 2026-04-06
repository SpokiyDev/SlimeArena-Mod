package com.spokiy.slimearenamod.util.class_selection;

import com.spokiy.slimearenamod.config.Config;
import com.spokiy.slimearenamod.data.PlayerClass;
import com.spokiy.slimearenamod.data.PlayerData;
import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.util.shop.ShopItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ClassSelectionMenu  extends ScreenHandler {
    private final Inventory inventory;

    public ClassSelectionMenu(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_3X3, syncId);
        checkSize(inventory, 9);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // GUI
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                ClassSelectionUtil.fillShopSlot(inventory, j + i * 3);

                this.addSlot(new Slot(inventory, j + i * 3, 62 + j * 18, 17 + i * 18) {
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

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }


    }

    public static void open(ServerPlayerEntity player) {
        Inventory inventory = new SimpleInventory(9);

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, p) ->
                        new ClassSelectionMenu(syncId, playerInventory, inventory),
                Text.translatable("menu.slimearenamod.class_selection.label")
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
        if (slotIndex < 0 || slotIndex >= (3 - 1) * 3) {
            super.onSlotClick(slotIndex, button, actionType, player);
            return;
        }

        // Check cooldown
        if (operationCooldown == 0) executeOperation(slotIndex, player);
        // Set cooldown
        operationCooldown = 4;

    }
    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        if (operationCooldown > 0) operationCooldown--;
    }

    private void executeOperation(int slotIndex, ServerPlayerEntity player) {
        ClassSelectionItem classItem = ClassSelectionUtil.CLASS_SELECTION_ITEMS.get(slotIndex);
        if (classItem == null) return;

        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        playerData.setSlimeClass(classItem.playerClass());
        PlayerClass playerClass = classItem.playerClass();
        player.sendMessage(Text.literal("Обрано: ")
                .append(Text.translatable("class.slimearenamod." + playerClass.name().toLowerCase())
                        .formatted(Config.CLASS_COLORS.get(playerClass))), true);

        player.getWorld().playSound(
                null, player.getBlockPos(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                1.0F, 1.0F
        );
    }

}

