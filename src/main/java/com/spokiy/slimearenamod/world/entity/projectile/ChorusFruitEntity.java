package com.spokiy.slimearenamod.world.entity.projectile;

import com.spokiy.slimearenamod.networking.packet.ChangeYawPayload;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ChorusFruitEntity extends ThrownItemEntity {

    public ChorusFruitEntity(EntityType<? extends ChorusFruitEntity> entityType, World world) {
        super(entityType, world);
    }
    public ChorusFruitEntity(World world, LivingEntity owner) {
        super(SAEntities.CHORUS_FRUIT, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.CHORUS_FRUIT;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            for (int i = 0; i < 8; i++) {
                this.getWorld().addParticle(
                        new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()),
                        this.getX(), this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.1, this.random.nextGaussian() * 0.1, this.random.nextGaussian() * 0.1
                );
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (entityHitResult.getEntity() instanceof LivingEntity entity) {
            if (this.getWorld() instanceof ServerWorld world) {
                if (entity instanceof ServerPlayerEntity player) {
                    ServerPlayNetworking.send(player, new ChangeYawPayload());
                }
                else {
                    float yaw = entity.getYaw() + 180.0F;
                    entity.setYaw(yaw);
                    entity.setHeadYaw(yaw);
                }

                world.playSound(null,
                        entity.getBlockPos(),
                        SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.NEUTRAL,
                        0.25F, 1.0F
                );

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

}
