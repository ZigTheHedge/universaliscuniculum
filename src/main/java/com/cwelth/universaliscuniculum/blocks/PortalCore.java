package com.cwelth.universaliscuniculum.blocks;

import com.cwelth.universaliscuniculum.gui.server.PortalCoreContainer;
import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult ray) {
        if (!world.isClientSide()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof PortalCoreTE) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen.universaliscuniculum.portalcore");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new PortalCoreContainer(i, world, pos, playerInventory, playerEntity);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) entity, containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState oldState, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != oldState.getBlock()) {
            PortalCoreTE te = (PortalCoreTE)worldIn.getBlockEntity(pos);
            if(te != null) {
                te.deactivatePortal(false);
                InventoryHelper.dropItemStack((World) worldIn, pos.getX(), pos.getY(), pos.getZ(), te.getItemStack(0));
            }
            worldIn.removeBlockEntity(pos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState blockState, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean hz) {
        if (!worldIn.isClientSide())
        {
            PortalCoreTE te = (PortalCoreTE)worldIn.getBlockEntity(pos);
            if (worldIn.hasNeighborSignal(pos))
            {
                te.redstoneSwitch(true);
            }
            else
            {
                te.redstoneSwitch(false);
            }
        }
    }
}
