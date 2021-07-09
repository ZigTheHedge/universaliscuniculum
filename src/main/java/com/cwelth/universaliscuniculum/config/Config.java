package com.cwelth.universaliscuniculum.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import javafx.util.Pair;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final String CATEGORY_MAIN = "main";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<? extends List<String>>> ACTIVATORS_LIST;

    static {

        COMMON_BUILDER.comment("Main config").push(CATEGORY_MAIN);
        //MOB_GRIEFS = COMMON_BUILDER.comment("Disable \"explosions destroying blocks\" mechanics for the following entities").define("explosionGriefs", false);

        ACTIVATORS_LIST = COMMON_BUILDER.comment("Specifies pairs of dimension and portal decoration blocks").defineList("activators", new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList("minecraft:overworld", "minecraft:obsidian")),
                new ArrayList<>(Arrays.asList("minecraft:nether", "minecraft:netherack")),
                new ArrayList<>(Arrays.asList("minecraft:end", "minecraft:end_stone"))
        )), obj -> obj instanceof List);

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static RegistryKey<World> getTargetDimension(String dimensionRegistryName)
    {
        if(dimensionRegistryName.startsWith("minecraft:"))
        {
            if(dimensionRegistryName.endsWith("nether")) return World.NETHER;
            if(dimensionRegistryName.endsWith("overworld")) return World.OVERWORLD;
            if(dimensionRegistryName.endsWith("end")) return World.END;
            return World.OVERWORLD;
        } else {
            RegistryKey<World> dimensionKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimensionRegistryName));
            return dimensionKey;
        }
    }

    public static ResourceLocation getDecorator(ResourceLocation dimension)
    {
        List<? extends List<String>> activators = ACTIVATORS_LIST.get();
        for(int i = 0; i < activators.size(); i++)
        {
            if(activators.get(i).get(0) == dimension.toString())
            {
                return new ResourceLocation(activators.get(i).get(1));
            }
        }
        return new ResourceLocation("universaliscuniculum:portal_block_deact");
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}
