package com.cwelth.universaliscuniculum.networking;

import com.cwelth.universaliscuniculum.UniversalisCuniculum;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkInit {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(UniversalisCuniculum.MOD_ID, "universaliscuniculum"), () -> "1.0", s -> true, s -> true);
    }
}
