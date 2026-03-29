package com.spokiy.slimearenamod.world.entity.projectile.blocks;

import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.util.Util;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThrownHoneyBlockEntity extends ThrownBlockEntity {
    private static final Block BLOCK_TYPE = Blocks.HONEY_BLOCK;

    public ThrownHoneyBlockEntity(EntityType<? extends ThrownHoneyBlockEntity> entityType, World world) {
        super(entityType, BLOCK_TYPE.getDefaultState(), world);
    }
    public ThrownHoneyBlockEntity(World world, LivingEntity owner) {
        super(SAEntities.THROWN_HONEY_BLOCK, BLOCK_TYPE.getDefaultState(), world, owner);
    }


    private ParticleEffect getParticleParameters() {
        return new BlockStateParticleEffect(ParticleTypes.BLOCK, BLOCK_TYPE.getDefaultState());
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < this.random.nextBetween(7, 12); i++) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(),
                        0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;

        this.setVelocity(vec3d.multiply(0.98));
        this.applyGravity();
        this.setPosition(d, e, f);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient && entityHitResult.getEntity() instanceof LivingEntity entity) {
            entity.addStatusEffect(Config.THROWN_HONEY_BLOCK_EFFECT.create());
            this.getWorld().playSound(
                    null,
                    entity.getBlockPos(),
                    SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                    SoundCategory.NEUTRAL,
                    0.25F,
                    Util.randomRange(this.random, 0.85F, 1.15F)
            );
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    @Override
    protected double getGravity() {
        return 0.03;
    }
}
