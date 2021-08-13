package com.cwelth.universaliscuniculum.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IPortalStructurePart {
    BlockPos getPortalCore(BlockPos pos, IWorld worldIn);
}
