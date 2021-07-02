package com.cwelth.universaliscuniculum.inits;

import com.cwelth.universaliscuniculum.blocks.PortalCore;
import com.cwelth.universaliscuniculum.blocks.PortalFrame;
import com.cwelth.universaliscuniculum.tileentities.PortalFrameTE;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.cwelth.universaliscuniculum.UniversalisCuniculum.MOD_ID;

public class Content {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILEENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILEENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //Blocks/BlockItems
    public static final RegistryObject<PortalCore> PORTAL_CORE_BLOCK = BLOCKS.register("portal_core", () -> new PortalCore());
    public static final RegistryObject<Item> PORTAL_CORE_ITEM = ITEMS.register("portal_core", () -> new BlockItem(PORTAL_CORE_BLOCK.get(), new Item.Properties().tab(InitCommon.creativeTab)));
    public static final RegistryObject<PortalFrame> PORTAL_FRAME_BLOCK = BLOCKS.register("portal_block", () -> new PortalFrame());
    public static final RegistryObject<Item> PORTAL_FRAME_ITEM = ITEMS.register("portal_block", () -> new BlockItem(PORTAL_FRAME_BLOCK.get(), new Item.Properties().tab(InitCommon.creativeTab)));

    //TileEntities
    public static final RegistryObject<TileEntityType<PortalFrameTE>> PORTAL_FRAME_TE = TILEENTITIES.register("portalframe", () -> TileEntityType.Builder.of(PortalFrameTE::new, PORTAL_FRAME_BLOCK.get()).build(null));
}
