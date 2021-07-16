package com.cwelth.universaliscuniculum.items;

import com.cwelth.universaliscuniculum.config.Config;
import com.cwelth.universaliscuniculum.inits.InitCommon;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class PortalActivator extends Item {
    public String dimension;

    public PortalActivator(String dimension) {
        super(new Item.Properties()
                .tab(InitCommon.creativeTab)
                .stacksTo(1)
        );
        this.dimension = dimension;
    }

    public String getDimensionKey()
    {
        for(List<String> dim : Config.ACTIVATORS_LIST.get())
        {
            String curDim = dim.get(0);
            if(curDim.equals(dimension))
            {
                return dim.get(1);
            }
        }
        return "minecraft:overworld";
    }

    public ResourceLocation getDecorator()
    {
        for(List<String> dim : Config.ACTIVATORS_LIST.get())
        {
            if(dim.get(0) == dimension)
            {
                return new ResourceLocation(dim.get(2));
            }
        }
        return new ResourceLocation("minecraft:stone");
    }

}
