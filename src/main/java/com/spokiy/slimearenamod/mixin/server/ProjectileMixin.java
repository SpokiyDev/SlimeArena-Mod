package com.spokiy.slimearenamod.mixin.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class ProjectileMixin {

    // Projectile optimization
    @Inject(method = "tick", at = @At("HEAD"))
    private void limitLifetime(CallbackInfo ci) {
        ProjectileEntity projectile = (ProjectileEntity) (Object) this;
        if (!projectile.getWorld().isClient && projectile.age % 20 == 0) {

            if (projectile.age > 20 * 30) projectile.discard();

            PlayerEntity closest = projectile.getWorld().getClosestPlayer(projectile, 128);
            if (closest == null) projectile.discard();
        }

    }
}
