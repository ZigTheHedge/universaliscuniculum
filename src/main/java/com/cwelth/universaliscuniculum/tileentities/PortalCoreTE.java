package com.cwelth.universaliscuniculum.tileentities;

import com.cwelth.universaliscuniculum.UniversalisCuniculum;
import com.cwelth.universaliscuniculum.blocks.IPortalStructurePart;
import com.cwelth.universaliscuniculum.blocks.PortalCore;
import com.cwelth.universaliscuniculum.blocks.PortalFrame;
import com.cwelth.universaliscuniculum.config.Config;
import com.cwelth.universaliscuniculum.inits.Content;
import com.cwelth.universaliscuniculum.items.PortalActivator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PortalCoreTE extends TileEntity {

    private ItemStackHandler handler = initHandler();
    private LazyOptional<IItemHandler> itemStackHandler = LazyOptional.of(() -> handler);

    public boolean isPortalActive = false;
    public boolean alwaysActive = false;
    public BlockPos linkedPortalPosition = new BlockPos(0,0,0);
    public ResourceLocation linkedPortalDimension = new ResourceLocation("minecraft:nowhere");
    private ResourceLocation looksLike = new ResourceLocation("universaliscuniculum:portal_block_deact");
    public boolean isPowered = false;

    public PortalCoreTE() {
        super(Content.PORTAL_CORE_TE.get());
    }

    public ItemStack getItemStack(int slot)
    {
        return handler.getStackInSlot(slot);
    }


    private ItemStackHandler initHandler() {
        return new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                setChanged();
                if(getStackInSlot(0).isEmpty())redstoneSwitch(false);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                Item itiq = stack.getItem();
                for(RegistryObject<PortalActivator> activator : Content.PORTAL_ACTIVATORS)
                    if(itiq == activator.get())return true;
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }

        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemStackHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public ResourceLocation getLooksLike() {
        return looksLike;
    }

    public void setLooksLike(ResourceLocation looksLike) {
        this.looksLike = looksLike;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        setChanged();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        load(null, pkt.getTag());
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.put("inv", handler.serializeNBT());
        nbt.putBoolean("isPortalActive", isPortalActive);
        nbt.putBoolean("alwaysActive", alwaysActive);
        nbt.putBoolean("isPowered", isPowered);
        nbt.put("linkedPortalPosition", NBTUtil.writeBlockPos(linkedPortalPosition));
        nbt.putString("linkedPortalStyle", linkedPortalDimension.toString());
        nbt.putString("looksLike", looksLike.toString());
        return super.save(nbt);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        if(nbt.contains("inv")) handler.deserializeNBT(nbt.getCompound("inv"));
        if(nbt.contains("isPortalActive")) isPortalActive = nbt.getBoolean("isPortalActive");
        if(nbt.contains("alwaysActive")) alwaysActive = nbt.getBoolean("alwaysActive");
        if(nbt.contains("isPowered")) isPowered = nbt.getBoolean("isPowered");
        if(nbt.contains("linkedPortalPosition")) linkedPortalPosition = NBTUtil.readBlockPos(nbt.getCompound("linkedPortalPosition"));
        if(nbt.contains("linkedPortalStyle")) linkedPortalDimension = new ResourceLocation(nbt.getString("linkedPortalStyle"));
        String looksLikeStr = nbt.getString("looksLike");
        if(looksLikeStr == "") looksLikeStr = "universaliscuniculum:portal_block_deact";
        looksLike = new ResourceLocation(looksLikeStr);
        super.load(blockState, nbt);
    }

    public boolean structureComplete()
    {
        World world = this.getLevel();
        BlockPos corePos = PortalCore.getPortalCenter(this.getBlockPos(), world);
        if(corePos == null) return false;
        if(!(world.getBlockState(corePos.east(1)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.east(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(1).east(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(2).east(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(3).east(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(4).east(1)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.west(1)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.west(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(1).west(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(2).west(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(3).west(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(4).west(1)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.north(1)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.north(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(1).north(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(2).north(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(3).north(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(4).north(1)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.south(1)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.south(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(1).south(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(2).south(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(3).south(2)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(4).south(1)).getBlock() instanceof IPortalStructurePart)) return false;
        if(!(world.getBlockState(corePos.above(4)).getBlock() instanceof IPortalStructurePart)) return false;
        if(PortalFrame.getPortalCoreStatic(corePos, world) == null)return false;
        return true;
    }

    private void setNewLook(World world, BlockPos pos, ResourceLocation dimension)
    {
        TileEntity te = world.getBlockEntity(pos);
        if(te != null)
        {
            if(te instanceof PortalFrameTE)
                ((PortalFrameTE)te).setLooksLike(Config.getDecorator(dimension));
            else if(te instanceof PortalCoreTE)
                ((PortalCoreTE)te).setLooksLike(Config.getDecorator(dimension));
        }
    }

    public void rebuildPortalStructure(ResourceLocation dimension)
    {
        World world = this.getLevel();
        BlockPos corePos = PortalCore.getPortalCenter(getBlockPos(), world);
        if(corePos == null) return;
        if(world.getBlockState(corePos.east(1)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.east(1), dimension);
        if(world.getBlockState(corePos.east(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.east(2), dimension);
        if(world.getBlockState(corePos.above(1).east(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(1).east(2), dimension);
        if(world.getBlockState(corePos.above(2).east(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(2).east(2), dimension);
        if(world.getBlockState(corePos.above(3).east(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(3).east(2), dimension);
        if(world.getBlockState(corePos.above(4).east(1)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(4).east(1), dimension);
        if(world.getBlockState(corePos.west(1)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.west(1), dimension);
        if(world.getBlockState(corePos.west(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.west(2), dimension);
        if(world.getBlockState(corePos.above(1).west(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(1).west(2), dimension);
        if(world.getBlockState(corePos.above(2).west(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(2).west(2), dimension);
        if(world.getBlockState(corePos.above(3).west(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(3).west(2), dimension);
        if(world.getBlockState(corePos.above(4).west(1)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(4).west(1), dimension);
        if(world.getBlockState(corePos.north(1)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.north(1), dimension);
        if(world.getBlockState(corePos.north(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.north(2), dimension);
        if(world.getBlockState(corePos.above(1).north(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(1).north(2), dimension);
        if(world.getBlockState(corePos.above(2).north(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(2).north(2), dimension);
        if(world.getBlockState(corePos.above(3).north(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(3).north(2), dimension);
        if(world.getBlockState(corePos.above(4).north(1)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(4).north(1), dimension);
        if(world.getBlockState(corePos.south(1)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.south(1), dimension);
        if(world.getBlockState(corePos.south(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.south(2), dimension);
        if(world.getBlockState(corePos.above(1).south(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(1).south(2), dimension);
        if(world.getBlockState(corePos.above(2).south(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(2).south(2), dimension);
        if(world.getBlockState(corePos.above(3).south(2)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(3).south(2), dimension);
        if(world.getBlockState(corePos.above(4).south(1)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(4).south(1), dimension);
        if(world.getBlockState(corePos.above(4)).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos.above(4), dimension);
        if(world.getBlockState(corePos).getBlock() instanceof IPortalStructurePart)setNewLook(world, corePos, dimension);
    }

    public void demolishPortalStructure()
    {
        World world = this.getLevel();
        BlockPos corePos = PortalCore.getPortalCenter(getBlockPos(), world);
        if(world.getBlockState(corePos.east(1)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.east(1), false);
        if(world.getBlockState(corePos.east(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.east(2), false);
        if(world.getBlockState(corePos.above(1).east(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(1).east(2), false);
        if(world.getBlockState(corePos.above(2).east(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(2).east(2), false);
        if(world.getBlockState(corePos.above(3).east(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(3).east(2), false);
        if(world.getBlockState(corePos.above(4).east(1)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(4).east(1), false);
        if(world.getBlockState(corePos.west(1)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.west(1), false);
        if(world.getBlockState(corePos.west(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.west(2), false);
        if(world.getBlockState(corePos.above(1).west(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(1).west(2), false);
        if(world.getBlockState(corePos.above(2).west(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(2).west(2), false);
        if(world.getBlockState(corePos.above(3).west(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(3).west(2), false);
        if(world.getBlockState(corePos.above(4).west(1)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(4).west(1), false);
        if(world.getBlockState(corePos.north(1)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.north(1), false);
        if(world.getBlockState(corePos.north(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.north(2), false);
        if(world.getBlockState(corePos.above(1).north(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(1).north(2), false);
        if(world.getBlockState(corePos.above(2).north(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(2).north(2), false);
        if(world.getBlockState(corePos.above(3).north(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(3).north(2), false);
        if(world.getBlockState(corePos.above(4).north(1)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(4).north(1), false);
        if(world.getBlockState(corePos.south(1)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.south(1), false);
        if(world.getBlockState(corePos.south(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.south(2), false);
        if(world.getBlockState(corePos.above(1).south(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(1).south(2), false);
        if(world.getBlockState(corePos.above(2).south(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(2).south(2), false);
        if(world.getBlockState(corePos.above(3).south(2)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(3).south(2), false);
        if(world.getBlockState(corePos.above(4).south(1)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(4).south(1), false);
        if(world.getBlockState(corePos.above(4)).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos.above(4), false);
        if(world.getBlockState(corePos).getBlock() instanceof IPortalStructurePart)world.removeBlock(corePos, false);
    }

    public static BlockPos checkExistingPortal(World world, BlockPos initialPos)
    {
        BlockPos corePos = initialPos;
        BlockPos retCoords = null;
        if(world.getBlockState(corePos.east(1)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.east(1)).getBlock()).getPortalCore(corePos.east(1), world):retCoords;
        if(world.getBlockState(corePos.east(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.east(2)).getBlock()).getPortalCore(corePos.east(2), world):retCoords;
        if(world.getBlockState(corePos.above(1).east(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(1).east(2)).getBlock()).getPortalCore(corePos.above(1).east(2), world):retCoords;
        if(world.getBlockState(corePos.above(2).east(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(2).east(2)).getBlock()).getPortalCore(corePos.above(2).east(2), world):retCoords;
        if(world.getBlockState(corePos.above(3).east(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(3).east(2)).getBlock()).getPortalCore(corePos.above(3).east(2), world):retCoords;
        if(world.getBlockState(corePos.above(4).east(1)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(4).east(1)).getBlock()).getPortalCore(corePos.above(4).east(1), world):retCoords;
        if(world.getBlockState(corePos.west(1)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.west(1)).getBlock()).getPortalCore(corePos.west(1), world):retCoords;
        if(world.getBlockState(corePos.west(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.west(2)).getBlock()).getPortalCore(corePos.west(2), world):retCoords;
        if(world.getBlockState(corePos.above(1).west(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(1).west(2)).getBlock()).getPortalCore(corePos.above(1).west(2), world):retCoords;
        if(world.getBlockState(corePos.above(2).west(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(2).west(2)).getBlock()).getPortalCore(corePos.above(2).west(2), world):retCoords;
        if(world.getBlockState(corePos.above(3).west(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(3).west(2)).getBlock()).getPortalCore(corePos.above(3).west(2), world):retCoords;
        if(world.getBlockState(corePos.above(4).west(1)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(4).west(1)).getBlock()).getPortalCore(corePos.above(4).west(1), world):retCoords;
        if(world.getBlockState(corePos.north(1)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.north(1)).getBlock()).getPortalCore(corePos.north(1), world):retCoords;
        if(world.getBlockState(corePos.north(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.north(2)).getBlock()).getPortalCore(corePos.north(2), world):retCoords;
        if(world.getBlockState(corePos.above(1).north(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(1).north(2)).getBlock()).getPortalCore(corePos.above(1).north(2), world):retCoords;
        if(world.getBlockState(corePos.above(2).north(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(2).north(2)).getBlock()).getPortalCore(corePos.above(2).north(2), world):retCoords;
        if(world.getBlockState(corePos.above(3).north(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(3).north(2)).getBlock()).getPortalCore(corePos.above(3).north(2), world):retCoords;
        if(world.getBlockState(corePos.above(4).north(1)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(4).north(1)).getBlock()).getPortalCore(corePos.above(4).north(1), world):retCoords;
        if(world.getBlockState(corePos.south(1)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.south(1)).getBlock()).getPortalCore(corePos.south(1), world):retCoords;
        if(world.getBlockState(corePos.south(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.south(2)).getBlock()).getPortalCore(corePos.south(1), world):retCoords;
        if(world.getBlockState(corePos.above(1).south(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(1).south(2)).getBlock()).getPortalCore(corePos.above(1).south(2), world):retCoords;
        if(world.getBlockState(corePos.above(2).south(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(2).south(2)).getBlock()).getPortalCore(corePos.above(2).south(2), world):retCoords;
        if(world.getBlockState(corePos.above(3).south(2)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(3).south(2)).getBlock()).getPortalCore(corePos.above(3).south(2), world):retCoords;
        if(world.getBlockState(corePos.above(4).south(1)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(4).south(1)).getBlock()).getPortalCore(corePos.above(4).south(1), world):retCoords;
        if(world.getBlockState(corePos.above(4)).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos.above(4)).getBlock()).getPortalCore(corePos.above(4), world):retCoords;
        if(world.getBlockState(corePos).getBlock() instanceof IPortalStructurePart) retCoords = (retCoords==null)? ((IPortalStructurePart)world.getBlockState(corePos).getBlock()).getPortalCore(corePos, world):retCoords;

        return retCoords;
    }

    public void activatePortal(ResourceLocation style)
    {
        World world = this.getLevel();
        BlockPos corePos = PortalCore.getPortalCenter(getBlockPos(), getLevel());
        if(!structureComplete())return;
        rebuildPortalStructure(style);
        if(world.getBlockState(corePos.above(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(2)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(2), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(3)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(3), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(1).east(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(1).east(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(2).east(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(2).east(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(3).east(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(3).east(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(1).west(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(1).west(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(2).west(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(2).west(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(3).west(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(3).west(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(1).north(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(1).north(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(2).north(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(2).north(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(3).north(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(3).north(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(1).south(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(1).south(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(2).south(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(2).south(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        if(world.getBlockState(corePos.above(3).south(1)).getBlock() != Content.PORTAL_BLOCK.get())world.setBlockAndUpdate(corePos.above(3).south(1), Content.PORTAL_BLOCK.get().defaultBlockState());
        isPortalActive = true;
        setChanged();
        world.sendBlockUpdated(corePos, world.getBlockState(corePos), world.getBlockState(corePos), 2);
    }

    public void deactivatePortal(boolean demolishPortalFrame)
    {
        World world = this.getLevel();
        BlockPos corePos = PortalCore.getPortalCenter(getBlockPos(), getLevel());
        if (corePos == null) return;
        rebuildPortalStructure(new ResourceLocation("minecraft:nowhere"));

        if(world.getBlockState(corePos.above(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(1), false);
        if(world.getBlockState(corePos.above(2)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(2), false);
        if(world.getBlockState(corePos.above(3)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(3), false);
        if(world.getBlockState(corePos.above(1).east(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(1).east(1), false);
        if(world.getBlockState(corePos.above(2).east(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(2).east(1), false);
        if(world.getBlockState(corePos.above(3).east(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(3).east(1), false);
        if(world.getBlockState(corePos.above(1).west(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(1).west(1), false);
        if(world.getBlockState(corePos.above(2).west(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(2).west(1), false);
        if(world.getBlockState(corePos.above(3).west(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(3).west(1), false);
        if(world.getBlockState(corePos.above(1).north(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(1).north(1), false);
        if(world.getBlockState(corePos.above(2).north(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(2).north(1), false);
        if(world.getBlockState(corePos.above(3).north(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(3).north(1), false);
        if(world.getBlockState(corePos.above(1).south(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(1).south(1), false);
        if(world.getBlockState(corePos.above(2).south(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(2).south(1), false);
        if(world.getBlockState(corePos.above(3).south(1)).getBlock() == Content.PORTAL_BLOCK.get())world.removeBlock(corePos.above(3).south(1), false);
        isPortalActive = false;
        if(demolishPortalFrame)demolishPortalStructure();
        linkedPortalPosition = new BlockPos(0, 0, 0);
        setChanged();
        world.sendBlockUpdated(corePos, world.getBlockState(corePos), world.getBlockState(corePos), 2);
    }

    public ResourceLocation getCoreStyle()
    {
        ItemStack activator = handler.getStackInSlot(0);
        if(activator.isEmpty()) return new ResourceLocation("minecraft:nowhere");
        String style = "minecraft:nowhere";
        if(activator.getItem() instanceof PortalActivator)
        {
            style = ((PortalActivator)activator.getItem()).getDimensionKey();
        }
        return new ResourceLocation(style);
    }

    private void setPortalFrame(World world, BlockPos pos, ResourceLocation look)
    {
        world.setBlockAndUpdate(pos, Content.PORTAL_FRAME_BLOCK.get().defaultBlockState());
        setNewLook(world, pos, look);
    }

    public void buildLinkedPortal(RegistryKey<World> dimension, BlockPos position) {
        MinecraftServer mc = this.getLevel().getServer();
        World targetWorld = mc.getLevel(dimension);

        if(targetWorld.getBlockState(position).getBlock() == Content.PORTAL_CORE_BLOCK.get())
        {
            PortalCoreTE targetCore = (PortalCoreTE) targetWorld.getBlockEntity(position);
            if(targetCore.isPortalActive) {
                linkedPortalDimension = getCoreStyle();
                linkedPortalPosition = position;
                setChanged();
                return;
            }
        }

        //BanishedCore.logger.warn("Generating linked portal at: " + position.toString() + ", @" + dimension);

        for(int px = 0; px < 5; px++)
            for(int py = 0; py < 5; py++)
                for(int pz = 0; pz < 5; pz++)
                {
                    targetWorld.removeBlock(position.above(pz).east(2).south(2).west(px).north(py), false);
                }

        targetWorld.setBlockAndUpdate(position, Content.PORTAL_CORE_BLOCK.get().defaultBlockState());
        PortalCoreTE targetCore = (PortalCoreTE) targetWorld.getBlockEntity(position);
        targetCore.linkedPortalPosition = this.getBlockPos();
        targetCore.linkedPortalDimension = this.getLevel().dimension().location();
        targetCore.alwaysActive = true;
        for (int i = 1; i <= 3; i++) {
            if(i < 3) {
                setPortalFrame(targetWorld, position.east(i), targetCore.linkedPortalDimension);
                setPortalFrame(targetWorld, position.west(i), targetCore.linkedPortalDimension);
                setPortalFrame(targetWorld, position.south(i), targetCore.linkedPortalDimension);
                setPortalFrame(targetWorld, position.north(i), targetCore.linkedPortalDimension);
            }

            setPortalFrame(targetWorld, position.above(i).east(2), targetCore.linkedPortalDimension);
            setPortalFrame(targetWorld, position.above(i).west(2), targetCore.linkedPortalDimension);
            setPortalFrame(targetWorld, position.above(i).south(2), targetCore.linkedPortalDimension);
            setPortalFrame(targetWorld, position.above(i).north(2), targetCore.linkedPortalDimension);
        }
        setPortalFrame(targetWorld, position.above(4).east(), targetCore.linkedPortalDimension);
        setPortalFrame(targetWorld, position.above(4).west(), targetCore.linkedPortalDimension);
        setPortalFrame(targetWorld, position.above(4).south(), targetCore.linkedPortalDimension);
        setPortalFrame(targetWorld, position.above(4).north(), targetCore.linkedPortalDimension);
        setPortalFrame(targetWorld, position.above(4), targetCore.linkedPortalDimension);

        if(Config.PLATFORMNEEDED.get()) {
            for (int px = 0; px < 5; px++)
                for (int py = 0; py < 5; py++) {
                    targetWorld.setBlockAndUpdate(position.below().east(2).south(2).west(px).north(py), ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Config.PLATFORM_BLOCK.get())).getBlock().defaultBlockState());
                }
        }
        targetCore.activatePortal(targetCore.linkedPortalDimension);
        targetCore.setChanged();

        linkedPortalDimension = getCoreStyle();
        linkedPortalPosition = position;
        setChanged();
    }

    public BlockPos getTargetPortalPosition(ResourceLocation dimension)
    {
        BlockPos possiblePortalCoords;
        MinecraftServer mc = this.getLevel().getServer();
        World world = mc.getLevel(Config.getTargetDimension(dimension.toString()));
        if(dimension.toString().equals("minecraft:the_nether"))
        {
            double yCoord = getBlockPos().getY();
            if(yCoord > 70) yCoord = 70;
            possiblePortalCoords = new BlockPos(Math.floor(getBlockPos().getX() / 8), yCoord, Math.floor(getBlockPos().getZ() / 8));
        } else if(dimension.toString().equals("minecraft:the_end"))
        {
            possiblePortalCoords = new BlockPos(100, 48, 0);
        } else
        {

            possiblePortalCoords = ((ServerWorld)world).getSharedSpawnPos();
            int lastY = possiblePortalCoords.getY();
            possiblePortalCoords = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, possiblePortalCoords);
            if(possiblePortalCoords.getY() == -1)
                possiblePortalCoords = new BlockPos(possiblePortalCoords.getX(), lastY, possiblePortalCoords.getZ());

        }
        BlockPos existingPortalCoords = checkExistingPortal(world, possiblePortalCoords);
        if(existingPortalCoords != null) {
            PortalCoreTE targetPortal = (PortalCoreTE)world.getBlockEntity(existingPortalCoords);
            if(targetPortal != null)
            {
                targetPortal.deactivatePortal(true);
            }
            return existingPortalCoords;
        } else
            return possiblePortalCoords;
    }

    public void redstoneSwitch(boolean isActive)
    {
        if(getLevel().isClientSide())return;
        if(!structureComplete())return;
        if(isActive)
        {
            if(isPowered) return;
            isPowered = true;
            if(isPortalActive) return;
            ResourceLocation style = getCoreStyle();
            if(style.toString().equals("minecraft:nowhere"))
            {
                UniversalisCuniculum.LOGGER.warn("Cannot find suitable dimension! Check your configs!");
                return;
            }
            if(this.getLevel().dimension().location().toString().equals(style.toString())) return;
            activatePortal(style);
            if(linkedPortalPosition.getX() == 0 && linkedPortalPosition.getY() == 0 && linkedPortalPosition.getZ() == 0)
                buildLinkedPortal(Config.getTargetDimension(style.toString()), getTargetPortalPosition(style));
            else
                buildLinkedPortal(Config.getTargetDimension(style.toString()), linkedPortalPosition);
            return;
        } else
        {
            if(!isPowered)return;
            isPowered = false;
            if(!isPortalActive) return;
            if(!alwaysActive) {
                if(linkedPortalPosition.getX() == 0 && linkedPortalPosition.getY() == 0 && linkedPortalPosition.getZ() == 0)
                {
                } else
                {
                    World targetWorld = this.getLevel().getServer().getLevel(Config.getTargetDimension(linkedPortalDimension.toString()));
                    PortalCoreTE targetPortalTE = (PortalCoreTE)targetWorld.getBlockEntity(linkedPortalPosition);
                    if(targetPortalTE != null) {
                        targetPortalTE.deactivatePortal(true);
                    }
                }
                deactivatePortal(false);
            }

        }
    }
}
