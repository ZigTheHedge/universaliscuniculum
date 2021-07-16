package com.cwelth.universaliscuniculum.tileentities;

import com.cwelth.universaliscuniculum.inits.Content;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class PortalFrameTE extends TileEntity {

    private ResourceLocation looksLike = new ResourceLocation("universaliscuniculum:portal_block_deact");

    public PortalFrameTE() {
        super(Content.PORTAL_FRAME_TE.get());
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
        nbt.putString("looksLike", looksLike.toString());
        return super.save(nbt);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        String looksLikeStr = nbt.getString("looksLike");
        if(looksLikeStr == "") looksLikeStr = "universaliscuniculum:portal_block_deact";
        looksLike = new ResourceLocation(looksLikeStr);
        super.load(blockState, nbt);
    }


}
