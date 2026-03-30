package com.spokiy.slimearenamod.mixin;

import com.spokiy.slimearenamod.util.Config;
import com.spokiy.slimearenamod.util.EffectConfig;
import com.spokiy.slimearenamod.util.Util;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

import static com.spokiy.slimearenamod.util.UseItemUtil.ACTIONS;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        Item item = (Item)(Object)this;
        ItemStack stack = user.getStackInHand(hand);

        if (ACTIONS.containsKey(item)) {
            ACTIONS.get(item).accept(world, user, stack);

            user.incrementStat(Stats.USED.getOrCreateStat(item));
            stack.decrementUnlessCreative(1, user);

            cir.setReturnValue(TypedActionResult.success(stack, world.isClient()));
        }

    }

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void customTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        Item item = stack.getItem();

        if (item == Items.SLIME_BALL) quickEffectLore(Config.SLIME_BALL_EFFECT, tooltip, context);
        else if (item == Items.PUFFERFISH) quickEffectLore(Config.PUFFERFISH_EFFECT, tooltip, context);
        else if (item == Items.HONEY_BLOCK) quickEffectLore(Config.HONEY_BLOCK_EFFECT, tooltip, context);
        else if (item == Items.PUMPKIN) {
            tooltip.addAll(Util.quickLore(item, 3));
            quickEffectLore(Config.PUMPKIN_EFFECT, tooltip, context);
        }
        else if (item == Items.CAKE) {
            tooltip.addAll(Util.quickLore(item, 3));
            quickEffectLore(Config.CAKE_EFFECTS, tooltip, context);
        }

    }

    @Unique
    private void quickEffectLore(EffectConfig[] effects, List<Text> tooltip, Item.TooltipContext context) {
        PotionContentsComponent.buildTooltip(Arrays.stream(effects).map(EffectConfig::create).toList(),
                tooltip::add, 1.0F, context.getUpdateTickRate());
    }
    @Unique
    private void quickEffectLore(EffectConfig effect, List<Text> tooltip, Item.TooltipContext context) {
        quickEffectLore(new EffectConfig[]{ effect }, tooltip, context);
    }


}
