package com.cwelth.universaliscuniculum.inits;

import com.cwelth.universaliscuniculum.UniversalisCuniculum;
import com.cwelth.universaliscuniculum.networking.NetworkInit;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = UniversalisCuniculum.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InitCommon {
    public static final ItemGroup creativeTab = new ItemGroup("universaliscuniculum") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Content.PORTAL_ACTIVATOR_EMPTY.get());
        }
    };

    public static void init(final FMLCommonSetupEvent event) {
        initCapabilities();
        NetworkInit.registerMessages();
    }

    public static void initCapabilities()
    {
    }

}
