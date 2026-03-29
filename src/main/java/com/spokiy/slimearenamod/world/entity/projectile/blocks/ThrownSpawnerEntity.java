package com.spokiy.slimearenamod.world.entity.projectile.blocks;

import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.world.World;

public class ThrownSpawnerEntity extends ThrownBlockEntity {
    private static final Block BLOCK_TYPE = Blocks.SPAWNER;

    public Entity stuckEntity = null;

    public ThrownSpawnerEntity(EntityType<? extends ThrownSpawnerEntity> entityType, World world) {
        super(entityType, BLOCK_TYPE.getDefaultState(), world);
    }
    public ThrownSpawnerEntity(World world, LivingEntity owner) {
        super(SAEntities.THROWN_SPAWNER, BLOCK_TYPE.getDefaultState(), world, owner);
    }

//    @Override
//    public void tick() {
//        super.tick();
//
//        if (stuckEntity != null) {
//            this.setScale(Math.max(stuckEntity.getWidth(), stuckEntity.getHeight()));
//
//            if (stuckEntity.isAlive()) {
//                stuckEntity.setPosition(this.getX(), this.getY(), this.getZ());
//                stuckEntity.setVelocity(Vec3d.ZERO);
//            }
//            else stuckEntity = null;
//        }
//
//    }
//
//
//    @Override
//    protected void onEntityHit(EntityHitResult entityHitResult) {
//        super.onEntityHit(entityHitResult);
//
//        if (stuckEntity == null) {
//            Entity entity = entityHitResult.getEntity();
//            if (entity.isAlive()) {
//                stuckEntity = entity;
//
//                EntityDimensions dimensions = entity.getDimensions(EntityPose.STANDING);
//                this.setScale(Math.max(dimensions.width(), dimensions.height()) * 1.1F);
//
//            }
//        }
//
//    }

//    @Override
//    protected void onCollision(HitResult hitResult) {
//        super.onCollision(hitResult);
//        if (!this.getWorld().isClient) {
//            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
//            this.discard();
//        }
//    }


}