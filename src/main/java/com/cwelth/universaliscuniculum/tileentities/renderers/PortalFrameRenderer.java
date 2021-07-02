package com.cwelth.universaliscuniculum.tileentities.renderers;

import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.tileentities.PortalFrameTE;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class PortalFrameRenderer extends TileEntityRenderer<PortalFrameTE> {

    public PortalFrameRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(Content.PORTAL_FRAME_TE.get(), PortalFrameRenderer::new);
    }

    @Override
    public void render(PortalFrameTE te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        BlockState renderBlock = Blocks.DIAMOND_BLOCK.defaultBlockState();
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        blockRenderer.renderBlock(renderBlock, matrixStack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
    }
}
