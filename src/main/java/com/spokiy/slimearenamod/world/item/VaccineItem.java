package com.spokiy.slimearenamod.world.item;

import com.spokiy.slimearenamod.util.Util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class VaccineItem extends Item {
    public VaccineItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.addAll(Util.quickLore(itemStack));
    }
}
