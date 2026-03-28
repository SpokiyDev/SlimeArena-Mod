package com.spokiy.slimearenamod.mixin.server;

import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoatHornItem.class)
public abstract class GoatHornItemMixin {

    @Inject(method = "use", at = @At("RETURN"))
    private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        for (LivingEntity livingEntity : user.getWorld().getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(8.0), entity -> entity.isAlive() && entity != user)) {
            this.knockBack(user, livingEntity);
        }

        Vec3d vec3d = user.getBoundingBox().getCenter();

        for (int i = 0; i < 40; i++) {
            double d = user.getRandom().nextGaussian() * 0.2;
            double e = user.getRandom().nextGaussian() * 0.2;
            double f = user.getRandom().nextGaussian() * 0.2;
            user.getWorld().addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d, e, f);
        }

        Util.customCooldown(user, (Item)(Object) this, Config.KNOCKBACK_HORN_COOLDOWN);

    }
    @Unique
    private void knockBack(PlayerEntity user, Entity entity) {
        Vec3d direction = entity.getPos().subtract(user.getPos()).normalize();

        double horizontalStrength = 1.0;
        double verticalStrength = 0.8;

        entity.addVelocity(
                direction.x * horizontalStrength,
                 direction.y * verticalStrength / 2 + verticalStrength,
                direction.z * horizontalStrength
        );

    }

}
