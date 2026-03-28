package com.spokiy.slimearenamod.client.render;

import com.spokiy.slimearenamod.world.block.SABlocks;
import com.spokiy.slimearenamod.world.entity.SlimeTrapEntity;
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

@Environment(EnvType.CLIENT)
public class SlimeTrapEntityRenderer extends EntityRenderer<SlimeTrapEntity> {
    private final BlockRenderManager blockRenderManager;

    public SlimeTrapEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(SlimeTrapEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        BlockState state = SABlocks.SLIME_LAYER.getDefaultState();

        if (state.getRenderType() != BlockRenderType.MODEL) return;

        matrices.push();

        matrices.scale(1.6F, 1.0F, 1.6F);
        matrices.translate(-0.5, 0.0, -0.5);

        this.blockRenderManager.getModelRenderer().render(
                entity.getWorld(),
                this.blockRenderManager.getModel(state),
                state,
                entity.getBlockPos(),
                matrices,
                vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(state)),
                false,
                entity.getWorld().random,
                state.getRenderingSeed(entity.getBlockPos()),
                OverlayTexture.DEFAULT_UV
        );

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SlimeTrapEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}