package com.spokiy.slimearenamod.mixin.server;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TntEntity.class)
public class TntEntityMixin {

    @Unique
    ExplosionBehavior CUSTOM_EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return false;
        }

        @Override
        public boolean shouldDamage(Explosion explosion, Entity entity) { return false;}

        @Override
        public float getKnockbackModifier(Entity entity) { return super.getKnockbackModifier(entity) * 1.5F;}
    };

    @Inject(method = "tick", at = @At("HEAD"))
    private void explodeOnImpact(CallbackInfo ci) {
        TntEntity tnt = (TntEntity)(Object)this;
        if (tnt.getWorld().isClient) return;

        // Block Collision
        if (tnt.horizontalCollision || tnt.verticalCollision || tnt.isOnGround()) {
            tnt.setFuse(0);
        }

        // Entity Collision
//        List<Entity> entities = tnt.getWorld().getOtherEntities(tnt, tnt.getBoundingBox().expand(0.1));
//        if (!entities.isEmpty()) tnt.setFuse(0);
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"), cancellable = true)
    private void onExplode(CallbackInfo ci) {
        TntEntity tntEntity = (TntEntity) (Object) this;
        tntEntity.getWorld()
                .createExplosion(
                        null,
                        null,
                        CUSTOM_EXPLOSION_BEHAVIOR,
                        tntEntity.getX(),
                        tntEntity.getBodyY(0.0625),
                        tntEntity.getZ(),
                        4.0F,
                        false,
                        World.ExplosionSourceType.TNT
                );

        ci.cancel();
    }
}
