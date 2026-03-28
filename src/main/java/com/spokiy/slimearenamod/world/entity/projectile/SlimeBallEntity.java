package com.spokiy.slimearenamod.world.entity.projectile;

import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.util.Util;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import com.spokiy.slimearenamod.world.entity.SlimeTrapEntity;
import com.spokiy.slimearenamod.world.item.SAItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SlimeBallEntity extends ThrownItemEntity {
    private static final TrackedData<Boolean> SLIME_TRAP = DataTracker.registerData(SlimeBallEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public SlimeBallEntity(EntityType<? extends SlimeBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlimeBallEntity(World world, LivingEntity owner) {
        super(SAEntities.SLIME_BALL, owner, world);
    }

    public SlimeBallEntity(World world, double x, double y, double z) {
        super(SAEntities.SLIME_BALL, x, y, z, world);
    }


    public void setSlimeTrap(boolean value) {
        this.dataTracker.set(SLIME_TRAP, value);
    }

    public boolean isSlimeTrap() {
        return this.dataTracker.get(SLIME_TRAP);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SLIME_TRAP, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("isSlimeTrap", isSlimeTrap());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setSlimeTrap(nbt.getBoolean("isSlimeTrap"));


    }

    @Override
    protected Item getDefaultItem() {
        return Items.SLIME_BALL;
    }

    private ParticleEffect getParticleParameters() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particleEffect = this.getParticleParameters();

            int count = !isSlimeTrap() ? 8 : 12;
            double yOffset = !isSlimeTrap() ? 0 : Config.TRAPPER_TRAP_PARTICLE_Y_OFFSET;
            for (int i = 0; i < count; i++) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY() + yOffset, this.getZ(),
                        0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        // Slime Trap
        if (isSlimeTrap()) {
            return;
        }

        // Slime Ball
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (!this.getWorld().isClient) {
                Util.playSlimeSound((ServerWorld) this.getWorld(), this, 0.25F);
            }

            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 5, 0));

        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();

            // Place Slime Trap
            if (isSlimeTrap() &&
                    hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getSide().equals(Direction.UP)) {
                Util.playSlimeSound((ServerWorld) this.getWorld(), this, 0.25F);

                Vec3d pos = this.getBoundingBox().getCenter();
                SlimeTrapEntity slimeTrapEntity = new SlimeTrapEntity(this.getWorld(), pos.x, blockHitResult.getPos().y, pos.z, this.getOwner());
                slimeTrapEntity.setUuid(this.getUuid());

                this.getWorld().spawnEntity(slimeTrapEntity);

            }

        }
    }

}
