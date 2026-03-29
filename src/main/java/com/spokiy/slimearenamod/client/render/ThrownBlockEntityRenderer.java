package com.spokiy.slimearenamod.client.render;

import com.spokiy.slimearenamod.world.entity.projectile.blocks.ThrownBlockEntity;
import com.spokiy.slimearenamod.world.entity.projectile.blocks.ThrownSpawnerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ThrownBlockEntityRenderer extends EntityRenderer<ThrownBlockEntity> {

    private final BlockRenderManager blockRenderManager;

    public ThrownBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(
            ThrownBlockEntity entity,
            float yaw,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light
    ) {

        BlockState state = entity.blockState;

        if (state == null || state.getRenderType() != BlockRenderType.MODEL) {
            return;
        }

        World world = entity.getWorld();

        if (state != world.getBlockState(entity.getBlockPos())
                && state.getRenderType() != BlockRenderType.INVISIBLE) {

            matrices.push();

            BlockPos renderPos = BlockPos.ofFloored(
                    entity.getX(),
                    entity.getBoundingBox().maxY,
                    entity.getZ()
            );

            if (entity instanceof ThrownSpawnerEntity spawner && spawner.stuckEntity != null) {
                float scale = spawner.getScale();
                matrices.scale(scale, scale, scale);
            }
            matrices.translate(-0.5f, 0.0, -0.5f);

            this.blockRenderManager
                    .getModelRenderer()
                    .render(
                            world,
                            this.blockRenderManager.getModel(state),
                            state,
                            renderPos,
                            matrices,
                            vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(state)),
                            false,
                            Random.create(),
                            state.getRenderingSeed(entity.getBlockPos()),
                            OverlayTexture.DEFAULT_UV
                    );

            matrices.pop();
        }

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(ThrownBlockEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}