package com.spokiy.slimearenamod.world.entity.projectile;

import com.spokiy.slimearenamod.config.Config;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

public class ThrownPufferfishEntity extends ThrownItemEntity {

    public ThrownPufferfishEntity(EntityType<? extends ThrownPufferfishEntity> entityType, World world) {
        super(entityType, world);
    }
    public ThrownPufferfishEntity(World world, LivingEntity owner) {
        super(SAEntities.THROWN_PUFFERFISH, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.PUFFERFISH;
    }

    private ParticleEffect getParticleParameters() {
        return ParticleTypes.SPLASH;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; i++) {
                this.getWorld().addParticle(
                        particleEffect,
                        this.getX(), this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.1, this.random.nextGaussian() * 0.1, this.random.nextGaussian() * 0.1
                );
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (this.getWorld() instanceof ServerWorld world) {
                world.playSound(null,
                        entity.getBlockPos(),
                        SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL,
                        0.25F, 1.0F
                );
            }

            livingEntity.addStatusEffect(Config.PUFFERFISH_EFFECT.create());

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

}
