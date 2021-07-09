package com.cwelth.universaliscuniculum.blocks;

import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import com.cwelth.universaliscuniculum.tileentities.PortalFrameTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class PortalCore extends Block {
    public PortalCore() {
        super(Properties.of(Material.METAL)
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE)
                .strength(4F, 6000000F)
        );
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PortalCoreTE();
    }
}
