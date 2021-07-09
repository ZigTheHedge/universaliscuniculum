package com.cwelth.universaliscuniculum.tileentities.renderers;

import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.tileentities.PortalFrameTE;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;

public class PortalFrameRenderer extends TileEntityRenderer<PortalFrameTE> {

    public PortalFrameRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(Content.PORTAL_FRAME_TE.get(), PortalFrameRenderer::new);
    }

    @Override
    public void render(PortalFrameTE te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        BlockState renderBlock = ForgeRegistries.BLOCKS.getValue(te.getLooksLike()).defaultBlockState();
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        blockRenderer.renderBlock(renderBlock, matrixStack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

    }
}
