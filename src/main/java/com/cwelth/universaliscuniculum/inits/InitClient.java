package com.cwelth.universaliscuniculum.inits;

import com.cwelth.universaliscuniculum.UniversalisCuniculum;
import com.cwelth.universaliscuniculum.tileentities.renderers.PortalFrameRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = UniversalisCuniculum.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InitClient {
    public static void init(final FMLClientSetupEvent event) {
        PortalFrameRenderer.register();
    }


    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {

    }
}
