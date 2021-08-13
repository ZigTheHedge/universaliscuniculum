package com.cwelth.universaliscuniculum.blocks;

import com.cwelth.universaliscuniculum.gui.server.PortalCoreContainer;
import com.cwelth.universaliscuniculum.tileentities.PortalCoreTE;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
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
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class PortalCore extends Block implements IPortalStructurePart {
    public PortalCore() {
        super(Properties.of(Material.METAL)
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE)
                .strength(4F, 6000000F)
        );
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_230322_1_, IBlockReader p_230322_2_, BlockPos p_230322_3_, ISelectionContext p_230322_4_) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
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

    @Override
    public BlockPos getPortalCore(BlockPos pos, IWorld worldIn) {
        return pos;
    }

    private static int countNeighbours(BlockPos pos, IWorld worldIn)
    {
        if(!(worldIn.getBlockState(pos).getBlock() instanceof IPortalStructurePart)) return 0;
        int neighboursFound = 0;
        for (Direction direction : Direction.values()) {
            if(direction == Direction.UP || direction == Direction.DOWN)continue;
            if (!(worldIn.getBlockState(pos.relative(direction)).getBlock() instanceof IPortalStructurePart)) continue;
            neighboursFound++;
        }
        return neighboursFound;
    }


    public static BlockPos getPortalCenter(BlockPos pos, IWorld worldIn) {

        BlockPos curPos = pos;
        //Find the lowest layer
        while(true) {
            if (!(worldIn.getBlockState(curPos.below()).getBlock() instanceof IPortalStructurePart))
            {
                if(!(worldIn.getBlockState(curPos.below(4)).getBlock() instanceof IPortalStructurePart)) break;
                else {
                    curPos = curPos.below(4);
                    continue;
                }
            }
            curPos = curPos.below();
        }
        int neighboursFound = 0;
        BlockPos tryPos = curPos;
        Direction lastDir = null;
        int maxNeighbours = 0;
        //Find the center
        while(true) {
            if(!(worldIn.getBlockState(tryPos).getBlock() instanceof IPortalStructurePart)) return null;
            for (Direction direction : Direction.values()) {
                if(direction == Direction.UP || direction == Direction.DOWN)continue;
                int subNeighboursFound = countNeighbours(tryPos.relative(direction), worldIn);
                if(subNeighboursFound > maxNeighbours)
                {
                    if(lastDir == null) lastDir = direction;
                    maxNeighbours = subNeighboursFound;
                }
                if (!(worldIn.getBlockState(tryPos.relative(direction)).getBlock() instanceof IPortalStructurePart)) continue;
                neighboursFound++;
            }
            if(neighboursFound == 0)return null;
            if(lastDir != null && neighboursFound < 4)
                tryPos = tryPos.relative(lastDir);

            if(neighboursFound == 4) {
                curPos = tryPos;
                break;
            }
            neighboursFound = 0;
            maxNeighbours = 0;
        }
        return curPos;
    }
}
