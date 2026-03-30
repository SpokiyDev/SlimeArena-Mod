package com.spokiy.slimearenamod.world.entity.projectile.blocks;

import com.spokiy.slimearenamod.data.PlayerTeam;
import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.util.EffectConfig;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThrownCakeEntity extends ThrownBlockEntity {
    private static final Block BLOCK_TYPE = Blocks.CAKE;

    public ThrownCakeEntity(EntityType<? extends ThrownCakeEntity> entityType, World world) {
        super(entityType, BLOCK_TYPE.getDefaultState(), world);
    }
    public ThrownCakeEntity(World world, LivingEntity owner) {
        super(SAEntities.THROWN_CAKE, BLOCK_TYPE.getDefaultState(), world, owner);
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

        if (!this.getWorld().isClient) {
            if (entityHitResult.getEntity() instanceof LivingEntity target && this.getOwner() instanceof LivingEntity owner) {
                PlayerTeam targetTeam = SAComponents.PLAYER_DATA.get(target).getPlayerTeam();
                PlayerTeam ownerTeam = SAComponents.PLAYER_DATA.get(owner).getPlayerTeam();

                if (targetTeam == ownerTeam) {
                    for (EffectConfig effect : Config.CAKE_EFFECTS) {
                        target.addStatusEffect(effect.create());
                        owner.addStatusEffect(effect.create());
                    }

                }
            }

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
