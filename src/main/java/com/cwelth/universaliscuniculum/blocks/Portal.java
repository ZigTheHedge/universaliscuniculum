package com.cwelth.universaliscuniculum.blocks;

import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;

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
                RegistryKey<World> dimId = World.OVERWORLD; //PortalCoreTE.getDimensionIDFromStyle(te.linkedPortalStyle);
                ((ServerPlayerEntity) entityIn).teleportTo(mc.getLevel(dimId), spawn.getX() + .5D, spawn.getY(), spawn.getZ() + .5D, ((ServerPlayerEntity) entityIn).yHeadRot, ((ServerPlayerEntity) entityIn).yBodyRot);

            }
        }

    }

    public BlockPos findPortalCore(World worldIn, BlockPos pos)
    {
        ResourceLocation style = new ResourceLocation("universaliscuniculum:portal_block_deact");
        BlockPos curPos = pos;
        Block biq = worldIn.getBlockState(curPos.below()).getBlock();
        while(biq == this) {
            curPos = curPos.below();
            biq = worldIn.getBlockState(curPos.below()).getBlock();
        }
        if(worldIn.getBlockState(curPos.below()).getBlock() != Content.PORTAL_CORE_BLOCK.get()) {
            biq = worldIn.getBlockState(curPos.east()).getBlock();
            if(biq == Content.PORTAL_BLOCK.get())
            {
                curPos = curPos.east();
            } else {
                biq = worldIn.getBlockState(curPos.west()).getBlock();
                if (biq == Content.PORTAL_BLOCK.get())
                {
                    curPos = curPos.west();
                } else {
                    biq = worldIn.getBlockState(curPos.south()).getBlock();
                    if (biq == Content.PORTAL_BLOCK.get())
                    {
                        curPos = curPos.south();
                    } else {
                        biq = worldIn.getBlockState(curPos.north()).getBlock();
                        if (biq == Content.PORTAL_BLOCK.get()) curPos = curPos.north();
                    }
                }
            }
        }

        PortalCoreTE te = (PortalCoreTE)worldIn.getBlockEntity(curPos.below());
        if(te != null)
            return curPos.below();
        else
            return null;
    }
}
