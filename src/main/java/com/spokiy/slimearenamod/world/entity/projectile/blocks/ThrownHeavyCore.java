package com.spokiy.slimearenamod.world.entity.projectile.blocks;

import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThrownHeavyCore extends ThrownBlockEntity {
    private static final Block BLOCK_TYPE = Blocks.HEAVY_CORE;

    public ThrownHeavyCore(EntityType<? extends ThrownHeavyCore> entityType, World world) {
        super(entityType, BLOCK_TYPE.getDefaultState(), world);
    }
    public ThrownHeavyCore(World world, LivingEntity owner) {
        super(SAEntities.THROWN_HEAVY_CORE, BLOCK_TYPE.getDefaultState(), world, owner);
    }


    private ParticleEffect getParticleParameters() {
        return ParticleTypes.CRIT;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; i++) {
                double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * this.getWidth() * 0.5;
                double e = this.getY();
                double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * this.getWidth() * 0.5;
                double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                double h = 0.3 + this.random.nextDouble() * 0.3;
                double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                this.getWorld().addParticle(particleEffect, d, e, f, g, h, j);
            }

        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isOnGround()) this.discard();

        this.tickPortalTeleportation();
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient) {
            Vec3d velocity = this.getVelocity();
            Entity entity = entityHitResult.getEntity();

            entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 4);

            double horizontalStrength = 2.0;
            double verticalStrength = 0.4;

            this.setVelocity(
                    -0.2 * velocity.x,
                    0.2,
                    -0.2 * velocity.z
            );
            this.velocityModified = true;
            entity.addVelocity(
                    velocity.x * horizontalStrength,
                    velocity.y * verticalStrength / 2 + verticalStrength,
                    velocity.z * horizontalStrength
            );
            entity.velocityModified = true;

        }

    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            playSoundOnHit((ServerWorld) this.getWorld());
        }
    }

    private void playSoundOnHit(ServerWorld world) {
        world.playSound(
                null,
                this.getBlockPos(),
                SoundEvents.BLOCK_HEAVY_CORE_PLACE,
                SoundCategory.NEUTRAL,
                1.0F,
                1.0F
        );
    }


}