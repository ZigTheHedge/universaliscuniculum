package com.cwelth.universaliscuniculum.blocks;

import com.cwelth.universaliscuniculum.config.Config;
import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class Portal extends Block {
    public Portal() {
        super(Properties.of(Material.PORTAL)
                .strength(-1.0F, 3600000.0F)
                .noDrops()
                .noCollission()
                .lightLevel((light) -> 6)
        );
    }

    @Override
    public void entityInside(BlockState p_196262_1_, World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isClientSide()) {
            if (entityIn instanceof ServerPlayerEntity) {
                BlockPos core = findPortalCore(worldIn, pos);
                if(core == null)return;
                PortalCoreTE te = (PortalCoreTE)worldIn.getBlockEntity(core);
                MinecraftServer mc = entityIn.getServer();
                BlockPos spawn = te.linkedPortalPosition.east(2).north(2).above();
                RegistryKey<World> dimId = Config.getTargetDimension(te.linkedPortalDimension.toString());
                ServerWorld dim = mc.getLevel(dimId);
                dim.getChunk(spawn);
                ((ServerPlayerEntity) entityIn).teleportTo(dim, spawn.getX() + .5D, spawn.getY(), spawn.getZ() + .5D, ((ServerPlayerEntity) entityIn).yHeadRot, ((ServerPlayerEntity) entityIn).yBodyRot);

            }
        }

    }

    public BlockPos findPortalCore(IWorld worldIn, BlockPos pos)
    {
        BlockPos curPos = pos;
        Block biq = worldIn.getBlockState(curPos).getBlock();
        while(biq == this) {
            curPos = curPos.below();
            biq = worldIn.getBlockState(curPos).getBlock();
        }

        if(biq != Content.PORTAL_CORE_BLOCK.get())
        {
            if(biq == Content.PORTAL_FRAME_BLOCK.get())
                curPos = ((PortalFrame)biq).getPortalCore(curPos, worldIn);
            else
                return null;
        }

        if(curPos == null) return null;

        PortalCoreTE te = (PortalCoreTE)worldIn.getBlockEntity(curPos);
        if(te != null)
            return curPos;
        else
            return null;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void destroy(IWorld worldIn, BlockPos pos, BlockState blockState) {
        BlockPos portalCorePos = findPortalCore(worldIn, pos);
        if(portalCorePos != null) {
            PortalCoreTE portalCoreTE = (PortalCoreTE) worldIn.getBlockEntity(portalCorePos);
            if (portalCoreTE != null)
                portalCoreTE.deactivatePortal(false);
        }
        super.destroy(worldIn, pos, blockState);
    }
}
