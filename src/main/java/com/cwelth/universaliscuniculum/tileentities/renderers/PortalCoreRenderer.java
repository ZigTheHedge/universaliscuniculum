package com.cwelth.universaliscuniculum.tileentities.renderers;

import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import com.cwelth.universaliscuniculum.tileentities.PortalFrameTE;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;

public class PortalCoreRenderer  extends TileEntityRenderer<PortalCoreTE> {
    public PortalCoreRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(Content.PORTAL_CORE_TE.get(), PortalCoreRenderer::new);
    }

    @Override
    public void render(PortalCoreTE te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        BlockState renderBlock = ForgeRegistries.BLOCKS.getValue(te.getLooksLike()).defaultBlockState();
        BlockState renderOverlayBlock = Content.PORTAL_CORE_OVERLAY.get().defaultBlockState();
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        blockRenderer.renderBlock(renderBlock, matrixStack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
        blockRenderer.renderBlock(renderOverlayBlock, matrixStack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

    }
}
