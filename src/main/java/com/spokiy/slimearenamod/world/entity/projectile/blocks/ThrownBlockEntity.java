package com.spokiy.slimearenamod.world.entity.projectile.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ThrownBlockEntity extends ProjectileEntity {
    public BlockState blockState;
    private static final TrackedData<Float> SCALE = DataTracker.registerData(ThrownBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public ThrownBlockEntity(EntityType<? extends ThrownBlockEntity> type, BlockState state, World world) {
        super(type, world);
        blockState = state;
        this.intersectionChecked = true;
    }
    public ThrownBlockEntity(EntityType<? extends ThrownBlockEntity> type, BlockState state, World world, double x, double y, double z) {
        this(type, state, world);
        this.setPos(x, y, z);
    }
    public ThrownBlockEntity(EntityType<? extends ThrownBlockEntity> type, BlockState state, World world, Vec3d pos) {
        this(type, state, world, pos.x, pos.y, pos.z);
    }
    public ThrownBlockEntity(EntityType<? extends ThrownBlockEntity> type, BlockState state, World world, LivingEntity owner) {
        this(type, state, world, owner.getEyePos());
        this.setOwner(owner);
    }


    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
        } else {
            super.tick();
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.hitOrDeflect(hitResult);
            }

            this.checkBlockCollision();

        }

    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) { return EntityDimensions.fixed(getScale(), getScale()); }

    public void setScale(float scale) { this.dataTracker.set(SCALE, scale); }

    public float getScale() { return this.dataTracker.get(SCALE); }

    @Override
    protected Entity.MoveEffect getMoveEffect() { return Entity.MoveEffect.NONE; }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return true;
    }

    @Override
    protected double getGravity() { return 0.04; }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SCALE, 1.0F);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("scale", getScale());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setScale(nbt.getFloat("scale"));

    }

}