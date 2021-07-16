package com.cwelth.universaliscuniculum.gui.client;

import com.cwelth.universaliscuniculum.UniversalisCuniculum;
import com.cwelth.universaliscuniculum.gui.server.PortalCoreContainer;
import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.java.accessibility.util.java.awt.TextComponentTranslator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PortalCoreScreen extends ContainerScreen<PortalCoreContainer> {

    private ResourceLocation GUI = new ResourceLocation(UniversalisCuniculum.MOD_ID, "textures/gui/portal_core.png");
    private PortalCoreTE te;

    public PortalCoreScreen(PortalCoreContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        imageWidth = 174;
        imageHeight = 186;
        inventoryLabelY = 92;
        this.te = container.tileEntity;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partilTicks) {
        super.render(matrixStack, mouseX, mouseY, partilTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
    }

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
        int relX = (this.width - this.getXSize()) / 2;
        int relY = (this.height - this.getYSize()) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.getXSize(), this.getYSize());
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        int relX = (this.width - this.getXSize()) / 2;
        int relY = (this.height - this.getYSize()) / 2;
        if(!te.structureComplete())drawCenteredString(matrixStack, this.font, new TranslationTextComponent("portalcore.nostructure"), relX, inventoryLabelY, 0xFFFFFFFF);
        if(te.isPortalActive)drawCenteredString(matrixStack, this.font, new TranslationTextComponent("portalcore.active"), relX, inventoryLabelY, 0xFFFFFFFF);
    }
}
