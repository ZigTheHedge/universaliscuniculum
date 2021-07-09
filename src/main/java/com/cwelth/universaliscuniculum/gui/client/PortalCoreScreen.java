package com.cwelth.universaliscuniculum.gui.client;

import com.cwelth.universaliscuniculum.UniversalisCuniculum;
import com.cwelth.universaliscuniculum.gui.server.PortalCoreContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortalCoreScreen extends ContainerScreen<PortalCoreContainer> {

    private ResourceLocation GUI = new ResourceLocation(UniversalisCuniculum.MOD_ID, "textures/gui/portal_core.png");

    public PortalCoreScreen(PortalCoreContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    
}
