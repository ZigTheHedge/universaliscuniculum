package com.cwelth.universaliscuniculum;

import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.inits.InitClient;
import com.cwelth.universaliscuniculum.inits.InitCommon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(UniversalisCuniculum.MOD_ID)
public class UniversalisCuniculum
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "universaliscuniculum";

    public UniversalisCuniculum() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(InitCommon::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(InitClient::init);
        Content.init();
    }

}
