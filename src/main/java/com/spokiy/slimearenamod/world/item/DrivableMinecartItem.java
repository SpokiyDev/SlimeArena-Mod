package com.spokiy.slimearenamod.world.item;

import com.spokiy.slimearenamod.world.entity.DrivableMinecartEntity;
import com.spokiy.slimearenamod.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class DrivableMinecartItem extends Item {

    private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
        private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();

        @Override
        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            Direction direction = pointer.state().get(DispenserBlock.FACING);
            ServerWorld world = pointer.world();

            Vec3d center = pointer.centerPos();

            double x = center.getX() + direction.getOffsetX() * 1.125;
            double y = center.getY();
            double z = center.getZ() + direction.getOffsetZ() * 1.125;

            DrivableMinecartEntity entity = new DrivableMinecartEntity(world, x, y, z);

            world.spawnEntity(entity);

            stack.decrement(1);
            return stack;
        }

        @Override
        protected void playSound(BlockPointer pointer) {
            pointer.world().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.pos(), 0);
        }
    };

    public DrivableMinecartItem(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        ItemStack stack = context.getStack();

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;

        // New Entity
        DrivableMinecartEntity entity =
                new DrivableMinecartEntity(serverWorld, x, y, z);

        if (!serverWorld.isSpaceEmpty(entity, entity.getBoundingBox())) return ActionResult.FAIL;

        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);

        if (belowState.isAir() || !belowState.isSolidBlock(world, below)) return ActionResult.FAIL;

        serverWorld.spawnEntity(entity);

        serverWorld.emitGameEvent(
                GameEvent.ENTITY_PLACE,
                pos,
                GameEvent.Emitter.of(world.getBlockState(pos))
        );

        stack.decrement(1);

        return ActionResult.success(world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.addAll(Util.quickLore(itemStack, Formatting.AQUA));
    }

}
