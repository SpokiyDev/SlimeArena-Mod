package com.spokiy.slimearenamod.world.entity;

import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.world.item.SAItems;
import com.spokiy.slimearenamod.util.Util;
import net.minecraft.entity.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DrivableMinecartEntity extends MinecartEntity {
    public boolean jumping = false;

    public DrivableMinecartEntity(EntityType<? extends DrivableMinecartEntity> type, World world) {
        super(type, world);
    }

    public DrivableMinecartEntity(World world, double x, double y, double z) {
        super(SAEntities.DRIVABLE_MINECART, world);
        this.setPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && this.isLogicalSideForUpdatingMovement()) {
            Entity passenger = this.getFirstPassenger();

            if (!(passenger instanceof ServerPlayerEntity player)) this.moveOffRail();
            else {
                // We want to run ground checking before any movement
                boolean onGround = this.isOnGround();

                // Horizontal movement
                this.applyHorizontalMovement(player);

                if (onGround) {
                    // Jump
                    if (this.jumping) this.jump();

                    // Step
                    else if (this.horizontalCollision) {
                        this.setPosition(this.getX(), this.getY() + Config.DRIVABLE_MINECART_STEP, this.getZ());
                    }
                }

                this.jumping = false;
            }

        }

    }

    private void applyHorizontalMovement(ServerPlayerEntity player) {
        Vec3d velocity = this.getVelocity();

        // Movement Direction
        float yaw = player.getYaw();

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = new Vec3d(forward.z, 0, -forward.x);

        // WASD Input
        float forwardInput = player.forwardSpeed;
        float sideInput = player.sidewaysSpeed;

        // Movement Logic
        Vec3d inputMotion = forward.multiply(forwardInput).add(right.multiply(sideInput));

        double acceleration = 0.12;

        velocity = velocity.add(inputMotion.multiply(acceleration));

        // Friction
        velocity = new Vec3d(velocity.x * 0.88, velocity.y, velocity.z * 0.88);

        // Max Speed
        if (velocity.length() > this.getMaxSpeed()) {
            velocity = velocity.normalize().multiply(this.getMaxSpeed());
        }

        this.setVelocity(velocity);

        // Move the entity
        this.move(MovementType.SELF, velocity);

    }

    private void jump() {
        Vec3d vel = this.getVelocity();

        double jumpVelocity = Config.DRIVABLE_MINECART_JUMP_STRENGTH;
        if (this.isTouchingWater()) jumpVelocity /= 1.5;

        this.setVelocity(vel.x, jumpVelocity, vel.z);
        this.velocityDirty = true;
    }


    @Override
    protected double getMaxSpeed() {
        return (this.isTouchingWater() ? 1.0 : 2.0) / (5.0 * 1.8);
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(SAItems.DRIVABLE_MINECART);
    }

    @Override
    protected Item asItem() {
        return SAItems.DRIVABLE_MINECART;
    }

}