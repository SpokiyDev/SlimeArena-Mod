package com.spokiy.slimearenamod.world.entity;

import com.spokiy.slimearenamod.data.PlayerTeam;
import com.spokiy.slimearenamod.data.SAComponents;
import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.util.EffectConfig;
import com.spokiy.slimearenamod.util.Util;
import com.spokiy.slimearenamod.world.item.SAItems;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SlimeTrapEntity extends Entity implements Ownable {
    @Nullable
    private UUID ownerUuid;
    @Nullable
    private Entity owner;

    public SlimeTrapEntity(EntityType<? extends SlimeTrapEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
    }
    public SlimeTrapEntity(World world, double x, double y, double z, @Nullable Entity owner) {
        this(SAEntities.SLIME_TRAP, world);
        setOwner(owner);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }


    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getUuid();
            this.owner = entity;
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.owner != null && !this.owner.isRemoved()) {
            return this.owner;
        } else if (this.ownerUuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
            this.owner = serverWorld.getEntity(this.ownerUuid);
            return this.owner;
        } else {
            return null;
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
            this.owner = null;
        }
    }


    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    protected double getGravity() {
        return 0.03;
    }

    private ParticleEffect getParticleParameters() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            this.getWorld().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 0.75F, 0.8F
            );

            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < 12; i++) {
                this.getWorld().addParticle(particleEffect,
                        this.getX(), this.getY() + Config.TRAPPER_TRAP_PARTICLE_Y_OFFSET, this.getZ(),
                        0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.tickPortalTeleportation();
        this.applyGravity();

        // Prevent trap from getting knocked back
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(0, Math.min(vec3d.y, 0), 0);

        // Check entity collision
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            List<Entity> entities = this.getWorld().getOtherEntities(
                    this,
                    this.getBoundingBox(),
                    e -> e instanceof LivingEntity
            );

            for (Entity entity : entities) {
                activate(serverWorld, (LivingEntity) entity);
            }
        }

        this.move(MovementType.SELF, this.getVelocity());

    }

    private void activate(ServerWorld world, LivingEntity entity) {
        if (entity instanceof PlayerEntity player && SAComponents.PLAYER_DATA.get(player).getPlayerTeam() == PlayerTeam.SLIME) return;

        for (EffectConfig effectConfig : Config.TRAPPER_TRAP_EFFECTS) entity.addStatusEffect(effectConfig.create());

        Entity owner = this.getOwner();
        if (owner instanceof PlayerEntity player) {
            Util.customCooldown(player, SAItems.SLIME_TRAP, Config.TRAPPER_ACTIVATE_TRAP_COOLDOWN);
            player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.NEUTRAL, 1, 2);
            player.sendMessage(Text.translatable("message.slimearenamod.trap_activated").formatted(Formatting.GOLD), true);
        }

        world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
        Util.playSlimeSound(world, this, 1F);

        this.discard();
    }


}
