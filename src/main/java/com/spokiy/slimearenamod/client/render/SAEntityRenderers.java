package com.spokiy.slimearenamod.client.render;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.world.entity.SAEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;

public class SAEntityRenderers {

    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Mod Entity Renderers for " + SlimeArenaMod.MOD_ID);

        // Humans
        EntityRendererRegistry.register(SAEntities.SLIME_BALL, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(SAEntities.THROWN_SPAWNER, ThrownBlockEntityRenderer::new);
        EntityRendererRegistry.register(SAEntities.THROWN_HEAVY_CORE, ThrownBlockEntityRenderer::new);
        EntityRendererRegistry.register(SAEntities.THROWN_PUMPKIN, ThrownBlockEntityRenderer::new);

        EntityRendererRegistry.register(SAEntities.DRIVABLE_MINECART, context -> new MinecartEntityRenderer<>(context, EntityModelLayers.MINECART));

        // Slimes
        EntityRendererRegistry.register(SAEntities.SLIME_TRAP, SlimeTrapEntityRenderer::new);

    }

}
