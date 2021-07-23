package com.cwelth.universaliscuniculum.blocks;

import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import com.cwelth.universaliscuniculum.tileentities.PortalFrameTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class PortalFrame extends Block {
    public PortalFrame() {
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
        return new PortalFrameTE();
    }


    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    public BlockPos getPortalCore(BlockPos pos, IWorld worldIn)
    {
        if(worldIn.getBlockState(pos.above()).getBlock() == this)
        {
            if(worldIn.getBlockState(pos.east()).getBlock() == this)
            {
                Block portal = worldIn.getBlockState(pos.above().east()).getBlock();
                if(portal == Content.PORTAL_BLOCK.get())
                    return pos.above().east();
            }
            if(worldIn.getBlockState(pos.west()).getBlock() == this)
            {
                Block portal = worldIn.getBlockState(pos.above().west()).getBlock();
                if(portal == Content.PORTAL_BLOCK.get())
                    return pos.above().west();
            }
            if(worldIn.getBlockState(pos.south()).getBlock() == this)
            {
                Block portal = worldIn.getBlockState(pos.above().south()).getBlock();
                if(portal == Content.PORTAL_BLOCK.get())
                    return pos.above().south();
            }
            if(worldIn.getBlockState(pos.north()).getBlock() == this)
            {
                Block portal = worldIn.getBlockState(pos.above().north()).getBlock();
                if(portal == Content.PORTAL_BLOCK.get())
                    return pos.above().north();
            }
        }
        if(worldIn.getBlockState(pos.above()).getBlock() == Content.PORTAL_BLOCK.get())
            return pos.above();

        if(worldIn.getBlockState(pos.below()).getBlock() == Content.PORTAL_BLOCK.get())
            return pos.below();

        if(worldIn.getBlockState(pos.east()).getBlock() == Content.PORTAL_BLOCK.get())
            return pos.east();

        if(worldIn.getBlockState(pos.west()).getBlock() == Content.PORTAL_BLOCK.get())
            return pos.west();

        if(worldIn.getBlockState(pos.south()).getBlock() == Content.PORTAL_BLOCK.get())
            return pos.south();

        if(worldIn.getBlockState(pos.north()).getBlock() == Content.PORTAL_BLOCK.get())
            return pos.north();

        return null;
    }

    @Override
    public void destroy(IWorld worldIn, BlockPos pos, BlockState blockState) {
        super.destroy(worldIn, pos, blockState);
        BlockPos portalCorePos = getPortalCore(pos, worldIn);
        if(portalCorePos != null) {
            Block portal = worldIn.getBlockState(portalCorePos).getBlock();
            PortalCoreTE portalCoreTE = (PortalCoreTE) worldIn.getBlockEntity(((Portal) portal).findPortalCore(worldIn, portalCorePos));
            if (portalCoreTE != null)
                portalCoreTE.deactivatePortal(false);
        }
    }
}
