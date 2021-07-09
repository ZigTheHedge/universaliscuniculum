package com.cwelth.universaliscuniculum.tileentities;

import com.cwelth.universaliscuniculum.inits.Content;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

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
