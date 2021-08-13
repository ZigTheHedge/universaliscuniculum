package com.cwelth.universaliscuniculum.blocks;

import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import com.cwelth.universaliscuniculum.tileentities.PortalFrameTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class PortalFrame extends Block implements IPortalStructurePart {
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

    public static BlockPos getPortalCoreStatic(BlockPos pos, IWorld worldIn)
    {
        int coresFound = 0;
        BlockPos curPos = PortalCore.getPortalCenter(pos, worldIn);
        if(curPos == null)return null;
        BlockPos centerPos = curPos;
        BlockPos corePos = null;

        if(worldIn.getBlockState(curPos).getBlock() == Content.PORTAL_CORE_BLOCK.get())
        {
            coresFound++;
            corePos = curPos;
        }
        //Bottom layer
        for(Direction direction: Direction.values())
        {
            if(direction == Direction.UP || direction == Direction.DOWN)continue;
            for(int ix = 0; ix < 2; ix++) {
                curPos = curPos.relative(direction);
                if (worldIn.getBlockState(curPos).getBlock() == Content.PORTAL_CORE_BLOCK.get()) {
                    coresFound++;
                    corePos = curPos;
                }
            }
            for(int iy = 0; iy < 3; iy++)
            {
                curPos = curPos.above();
                if (worldIn.getBlockState(curPos).getBlock() == Content.PORTAL_CORE_BLOCK.get()) {
                    coresFound++;
                    corePos = curPos;
                }
            }
            curPos = centerPos;
        }
        centerPos = centerPos.above(4);
        curPos = centerPos;
        if(worldIn.getBlockState(curPos).getBlock() == Content.PORTAL_CORE_BLOCK.get())
        {
            coresFound++;
            corePos = curPos;
        }

        for(Direction direction: Direction.values())
        {
            if(direction == Direction.UP || direction == Direction.DOWN)continue;
            curPos = curPos.relative(direction);
            if (worldIn.getBlockState(curPos).getBlock() == Content.PORTAL_CORE_BLOCK.get()) {
                coresFound++;
                corePos = curPos;
            }
            curPos = centerPos;
        }

        if(coresFound == 1)return corePos;
        else return null;
    }


    public BlockPos getPortalCore(BlockPos pos, IWorld worldIn)
    {
        return PortalFrame.getPortalCoreStatic(pos, worldIn);
    }

    @Override
    public void destroy(IWorld worldIn, BlockPos pos, BlockState blockState) {
        super.destroy(worldIn, pos, blockState);
        BlockPos portalCorePos = getPortalCore(pos, worldIn);
        if(portalCorePos != null) {
            Block portal = worldIn.getBlockState(portalCorePos).getBlock();
            PortalCoreTE portalCoreTE = (PortalCoreTE) worldIn.getBlockEntity(portalCorePos);
            if (portalCoreTE != null)
                portalCoreTE.deactivatePortal(false);
        }
    }
}
