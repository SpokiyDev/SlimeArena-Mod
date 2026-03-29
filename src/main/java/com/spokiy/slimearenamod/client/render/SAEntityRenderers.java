package com.spokiy.slimearenamod.client.render;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import com.spokiy.slimearenamod.world.entity.projectile.blocks.ThrownBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;

import java.util.List;

public class SAEntityRenderers {

    private static final List<EntityType<? extends ThrownItemEntity>> THROWN_ITEMS = List.of(
            SAEntities.SLIME_BALL, SAEntities.THROWN_PUFFERFISH, SAEntities.CHORUS_FRUIT
    );
    private static final List<EntityType<? extends ThrownBlockEntity>> THROWN_BLOCKS = List.of(
            SAEntities.THROWN_HEAVY_CORE, SAEntities.THROWN_HONEY_BLOCK, SAEntities.THROWN_PUMPKIN,
            SAEntities.THROWN_SPAWNER
    );

    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Entity Renderers for " + SlimeArenaMod.MOD_ID);

        // Humans
        THROWN_ITEMS.forEach(entityType ->
                EntityRendererRegistry.register(entityType, FlyingItemEntityRenderer::new));
        THROWN_BLOCKS.forEach(entityType ->
                EntityRendererRegistry.register(entityType, ThrownBlockEntityRenderer::new));

        EntityRendererRegistry.register(SAEntities.DRIVABLE_MINECART, context -> new MinecartEntityRenderer<>(context, EntityModelLayers.MINECART));

        // Slimes
        EntityRendererRegistry.register(SAEntities.SLIME_TRAP, SlimeTrapEntityRenderer::new);

    }

}
