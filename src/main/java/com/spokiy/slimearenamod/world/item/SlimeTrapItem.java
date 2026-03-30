package com.spokiy.slimearenamod.world.item;

import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.util.Util;
import com.spokiy.slimearenamod.world.entity.projectile.SlimeBallEntity;
import com.spokiy.slimearenamod.world.entity.SlimeTrapEntity;
import com.spokiy.slimearenamod.data.item.SAItemDataComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.UUID;

public class SlimeTrapItem extends Item implements ProjectileItem {

    public SlimeTrapItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.contains(SAItemDataComponents.UUID_COMPONENT) || super.hasGlint(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world instanceof ServerWorld serverWorld && entity instanceof PlayerEntity) {
            if (entity.age < 20) return;

            String uuid = stack.get(SAItemDataComponents.UUID_COMPONENT);
            if (uuid == null) return;
            if (serverWorld.getEntity(UUID.fromString(uuid)) == null) {
                stack.remove(SAItemDataComponents.UUID_COMPONENT);
            }

        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        String uuid = stack.get(SAItemDataComponents.UUID_COMPONENT);
        if (uuid == null) {
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ENTITY_SLIME_JUMP_SMALL,
                    SoundCategory.NEUTRAL,
                    0.5F,
                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
            );

            if (!world.isClient) {
                SlimeBallEntity slimeballEntity = new SlimeBallEntity(world, user);
                slimeballEntity.setSlimeTrap(true);
                slimeballEntity.setItem(stack);
                slimeballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
                world.spawnEntity(slimeballEntity);

                stack.set(SAItemDataComponents.UUID_COMPONENT, slimeballEntity.getUuidAsString());
            }

            Util.customCooldown(user, this, Config.TRAPPER_PLACE_TRAP_COOLDOWN);
        }
        else {
            if (world instanceof ServerWorld serverWorld) {
                Entity trap = serverWorld.getEntity(UUID.fromString(uuid));
                if (trap != null) {
                    stack.remove(SAItemDataComponents.UUID_COMPONENT);

                    // Visuals
                    world.sendEntityStatus(trap, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
                    Util.playSlimeSound(serverWorld, trap, 0.25F);

                    trap.discard();

                    Util.customCooldown(user, this, Config.TRAPPER_REMOVE_TRAP_COOLDOWN);
                }
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.FAIL;
        if (player.isSneaking()) return ActionResult.PASS;

        Direction direction = context.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        } else {
            ItemStack stack = context.getStack();
            if (stack.get(SAItemDataComponents.UUID_COMPONENT) != null) return ActionResult.PASS;

            World world = context.getWorld();
            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
            BlockPos pos = itemPlacementContext.getBlockPos();
            Vec3d vec3d = Vec3d.ofBottomCenter(pos).add(0, 0.1, 0);

            Box box = Box.of(vec3d, 1.0, 0.125, 1.0);
            if (world.isSpaceEmpty(null, box) && world.getOtherEntities(null, box).isEmpty()) {
                if (world instanceof ServerWorld serverWorld) {

                    SlimeTrapEntity trap = new SlimeTrapEntity(serverWorld, vec3d.getX(), pos.getY(), vec3d.getZ(), player);
                    stack.set(SAItemDataComponents.UUID_COMPONENT, trap.getUuidAsString());
                    Util.customCooldown(player, this, Config.TRAPPER_PLACE_TRAP_COOLDOWN);

                    serverWorld.spawnEntity(trap);
                    trap.emitGameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());

                    // Visuals
                    world.sendEntityStatus(trap, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
                    Util.playSlimeSound(serverWorld, trap, 0.75F);

                }

                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.FAIL;
            }
        }
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        SlimeBallEntity slimeballEntity = new SlimeBallEntity(world, pos.getX(), pos.getY(), pos.getZ());
        slimeballEntity.setSlimeTrap(true);
        slimeballEntity.setItem(stack);
        return slimeballEntity;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (stack.get(SAItemDataComponents.UUID_COMPONENT) != null) {
            tooltip.addAll(Util.quickLore(stack, Formatting.GOLD));
        }

    }
}
