package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.world.entity.projectile.*;
import com.spokiy.slimearenamod.world.entity.projectile.blocks.ThrownHeavyCore;
import com.spokiy.slimearenamod.world.entity.projectile.blocks.ThrownHoneyBlockEntity;
import com.spokiy.slimearenamod.world.entity.projectile.blocks.ThrownPumpkinEntity;
import com.spokiy.slimearenamod.world.entity.projectile.blocks.ThrownSpawnerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.util.TriConsumer;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

public class UseItemUtil {

    @Unique
    public static Map<Item, TriConsumer<World, PlayerEntity, ItemStack>> ACTIONS = new HashMap<>();
    public static void initActions() {
        ACTIONS.put(Items.FIRE_CHARGE, UseItemUtil::fireCharge);
        ACTIONS.put(Items.SLIME_BALL, UseItemUtil::slimeBall);
        ACTIONS.put(Items.PUFFERFISH, UseItemUtil::pufferFish);
        ACTIONS.put(Items.CHORUS_FRUIT, UseItemUtil::chorusFruit);
        ACTIONS.put(Items.HONEY_BLOCK, UseItemUtil::honeyBlock);
        ACTIONS.put(Items.ECHO_SHARD, UseItemUtil::echoShard);
        ACTIONS.put(Items.TNT, UseItemUtil::tnt);
        ACTIONS.put(Items.SPAWNER, UseItemUtil::spawner);
        ACTIONS.put(Items.PUMPKIN, UseItemUtil::pumpkin);
        ACTIONS.put(Items.HEAVY_CORE, UseItemUtil::heavyCore);
    }

    public static void fireCharge(World world, PlayerEntity user, ItemStack stack) {
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!world.isClient) {
            SmallFireballEntity projectile = new SmallFireballEntity(world, user, new Vec3d(0, 0, 0));
            projectile.setPosition(user.getEyePos());
            projectile.setItem(stack);
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(projectile);

        }
    }

    public static void slimeBall(World world, PlayerEntity user, ItemStack stack) {
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SLIME_JUMP_SMALL, SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (!world.isClient) {
            SlimeBallEntity projectile = new SlimeBallEntity(world, user);
            projectile.setItem(stack);
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(projectile);
        }

    }

    public static void pufferFish(World world, PlayerEntity user, ItemStack stack) {
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_PUFFER_FISH_FLOP, SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (!world.isClient) {
            ThrownPufferfishEntity projectile = new ThrownPufferfishEntity(world, user);
            projectile.setItem(stack);
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(projectile);
        }

    }

    public static void chorusFruit(World world, PlayerEntity user, ItemStack stack) {
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (!world.isClient) {
            ChorusFruitEntity projectile = new ChorusFruitEntity(world, user);
            projectile.setItem(stack);
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(projectile);
        }

    }

    public static void echoShard(World world, PlayerEntity user, ItemStack stack) {
        if (world instanceof ServerWorld serverWorld && user instanceof ServerPlayerEntity player) {
            Vec3d start = player.getEyePos();
            Vec3d direction = player.getRotationVec(1.0F).normalize();

            // Visuals
            for (int i = 1; i <= Config.ECHO_SHARD_SONIC_BOOM_RANGE; i += 2) {
                Vec3d pos = start.add(direction.multiply(i));
                serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
            }

            serverWorld.playSound(null, start.x, start.y, start.z, SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.NEUTRAL, 3.0F, 1.0F);


            double width = Config.ECHO_SHARD_SONIC_BOOM_HITBOX_WIDTH;

            Set<LivingEntity> targets = new HashSet<>();
            for (double i = 0; i <= Config.ECHO_SHARD_SONIC_BOOM_RANGE; i += Config.ECHO_SHARD_SONIC_BOOM_HITBOX_STEP) {
                Vec3d pos = start.add(direction.multiply(i));

                Box box = new Box(
                        pos.x - width, pos.y - width, pos.z - width,
                        pos.x + width, pos.y + width, pos.z + width
                );
                List<LivingEntity> entities = serverWorld.getEntitiesByClass(
                        LivingEntity.class, box,
                        e -> e != player
                );

                targets.addAll(entities);
            }


            for (LivingEntity target : targets) {
                double resistance = (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                double knMultiXZ = Config.ECHO_SHARD_SONIC_BOOM_KNOCKBACK_XZ * resistance;
                double knMultiY  = Config.ECHO_SHARD_SONIC_BOOM_KNOCKBACK_Y  * resistance;
                double knY = (direction.y > -0.45 ? 0.5 : -0.5) * resistance;

                target.addVelocity(
                        direction.x * knMultiXZ,
                        direction.y * knMultiY + knY,
                        direction.z * knMultiXZ);
            }

        }
    }

    public static void honeyBlock(World world, PlayerEntity user, ItemStack stack) {
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.4F)
        );

        if (!world.isClient) {
            ThrownHoneyBlockEntity projectile = new ThrownHoneyBlockEntity(world, user);
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.2F, 1.0F);
            world.spawnEntity(projectile);
        }
    }

    public static void tnt(World world, PlayerEntity user, ItemStack stack) {
        if (!world.isClient) {
            Vec3d pos = user.getEyePos();
            TntEntity projectile = new TntEntity(world, pos.x, pos.y, pos.z, user);
            setProjectileVelocity(projectile, user, user.getPitch(), user.getYaw(), 0.0F, 1.0F, 0.0F);
            world.spawnEntity(projectile);
        }
    }

    public static void spawner(World world, PlayerEntity user, ItemStack stack) {
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.4F)
        );

        if (!world.isClient) {
            ThrownSpawnerEntity projectile = new ThrownSpawnerEntity(world, user);
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0.0F);
            world.spawnEntity(projectile);
        }

    }

    public static void heavyCore(World world, PlayerEntity user, ItemStack stack) {
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.4F)
        );

        if (!world.isClient) {
            ThrownHeavyCore projectile = new ThrownHeavyCore(world, user);
            projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0.0F);
            world.spawnEntity(projectile);
        }

    }

    public static void pumpkin(World world, PlayerEntity user, ItemStack stack) {
        NbtComponent nbt = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null || !nbt.copyNbt().getBoolean("slimearenamod:pumpkin_worn")) {

            world.playSound(
                    null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                    0.5F,
                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
            );

            if (!world.isClient) {
                ThrownPumpkinEntity projectile = new ThrownPumpkinEntity(world, user);
                projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.2F, 0.0F);
                world.spawnEntity(projectile);
            }
        }
    }



    private static void setProjectileVelocity(Entity entity, Vec3d dir, float speed, float divergence, Entity shooter) {
        Vec3d velocity = dir.normalize().add(entity.getRandom().nextTriangular(0.0, 0.0172275 * divergence), entity.getRandom().nextTriangular(0.0, 0.0172275 * divergence), entity.getRandom().nextTriangular(0.0, 0.0172275 * divergence)).multiply(speed);

        if (shooter != null) {
            Vec3d m = shooter.getMovement();
            velocity = velocity.add(m.x, shooter.isOnGround() ? 0.0 : m.y, m.z);
        }

        entity.setVelocity(velocity);
        entity.velocityDirty = true;

        double h = velocity.horizontalLength();
        entity.setYaw((float)(MathHelper.atan2(velocity.x, velocity.z) * 180.0F / Math.PI));
        entity.setPitch((float)(MathHelper.atan2(velocity.y, h) * 180.0F / Math.PI));
        entity.prevYaw = entity.getYaw();
        entity.prevPitch = entity.getPitch();
    }

    private static void setProjectileVelocity(Entity entity, Entity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
        Vec3d dir = new Vec3d(-MathHelper.sin(yaw * (float)Math.PI / 180F) * MathHelper.cos(pitch * (float)Math.PI / 180F),
                -MathHelper.sin((pitch + roll) * (float)Math.PI / 180F),
                MathHelper.cos(yaw * (float)Math.PI / 180F) * MathHelper.cos(pitch * (float)Math.PI / 180F)
        );

        setProjectileVelocity(entity, dir, speed, divergence, shooter);
    }

}
