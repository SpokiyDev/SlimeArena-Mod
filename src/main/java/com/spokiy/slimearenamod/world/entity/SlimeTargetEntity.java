package com.spokiy.slimearenamod.world.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SlimeTargetEntity extends SlimeEntity {

    public SlimeTargetEntity(EntityType<? extends SlimeTargetEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlimeTargetEntity(World world, double x, double y, double z) {
        super(SAEntities.SLIME_TARGET, world);
        this.setPosition(x, y, z);
    }


    public static DefaultAttributeContainer.Builder createAttributes() {
        return SlimeEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0);
    }

    @Nullable
    @Override
    public EntityData initialize(net.minecraft.world.ServerWorldAccess world,
                                 net.minecraft.world.LocalDifficulty difficulty,
                                 SpawnReason spawnReason,
                                 @Nullable EntityData entityData) {
        this.setSize(2, true);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);

        if (result) {
            if (source.getSource() instanceof ServerPlayerEntity player && player.isCreative()) return true;

            this.setHealth(this.getMaxHealth());
        }

        return result;
    }

    @Override
    protected int getXpToDrop() {
        return 0;
    }

    @Override
    protected void initGoals() {
    }

    @Override
    public void setSize(int size, boolean heal) {
        super.setSize(2, heal);
    }

    @Override
    protected boolean canAttack() {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSize(2, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Size", 1);
    }
}