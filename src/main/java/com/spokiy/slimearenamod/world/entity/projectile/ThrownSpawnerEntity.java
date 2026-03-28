package com.spokiy.slimearenamod.world.entity.projectile;

import com.spokiy.slimearenamod.util.EntityTickTimers;
import com.spokiy.slimearenamod.util.shop.ShopUtil;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.List;

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