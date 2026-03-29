package com.spokiy.slimearenamod.world.entity.projectile.blocks;

import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.util.EntityTickTimers;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThrownPumpkinEntity extends ThrownBlockEntity {
    private static final Block BLOCK_TYPE = Blocks.PUMPKIN;
    private static final Logger log = LoggerFactory.getLogger(ThrownPumpkinEntity.class);

    public ThrownPumpkinEntity(EntityType<? extends ThrownPumpkinEntity> entityType, World world) {
        super(entityType, BLOCK_TYPE.getDefaultState(), world);
    }
    public ThrownPumpkinEntity(World world, LivingEntity owner) {
        super(SAEntities.THROWN_PUMPKIN, BLOCK_TYPE.getDefaultState(), world, owner);
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

        if (this.isOnGround()) {
            if (!this.getWorld().isClient) this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }

        this.tickPortalTeleportation();
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (this.getWorld() instanceof ServerWorld world) {
            if (entityHitResult.getEntity() instanceof LivingEntity entity) {
                if (!entity.getEquippedStack(EquipmentSlot.HEAD).isOf(BLOCK_TYPE.asItem())) {
                    ItemStack stack = new ItemStack(BLOCK_TYPE);

                    stack.set(DataComponentTypes.ITEM_NAME, stack.getName().copy().formatted(Formatting.RED));
                    stack.addEnchantment(
                            world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(Enchantments.BINDING_CURSE), 1);

                    entity.equipStack(EquipmentSlot.HEAD, stack);
                }

                // Timer
                StatusEffectInstance effect = Config.THROWN_PUMPKIN_EFFECT.create();

                EntityTickTimers timers = (EntityTickTimers) entity;
                timers.sa$setTimer("blockOnHead", effect.getDuration());

                entity.addStatusEffect(effect);

            } else world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
        }

        this.discard();
    }

    @Override
    protected double getGravity() {
        return 0.035;
    }

}
