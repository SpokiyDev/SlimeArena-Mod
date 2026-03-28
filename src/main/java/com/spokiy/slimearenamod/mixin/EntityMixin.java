package com.spokiy.slimearenamod.mixin;

import com.spokiy.slimearenamod.util.EntityTickTimers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(Entity.class)
public class EntityMixin implements EntityTickTimers {
    @Unique
    private Map<String, Integer> tickTimers = new HashMap<>();


    @Inject(method = "tick", at = @At("HEAD"))
    private void sa$tickTimers(CallbackInfo ci) {
        if (tickTimers.isEmpty()) return;

        Iterator<Map.Entry<String, Integer>> iterator = tickTimers.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            int value = entry.getValue() - 1;

            if (value <= 0) {
                iterator.remove();
                onTimerFinished(entry.getKey());
            } else {
                entry.setValue(value);
            }
        }
    }


    @Unique
    private void onTimerFinished(String id) {
        Entity self = (Entity)(Object)this;

        if (id.equals("blockOnHead")) {
            if (self instanceof LivingEntity entity) {
                entity.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
            }
        }

    }

    @Override
    public void sa$setTimer(String id, int ticks) {
        tickTimers.put(id, ticks);
    }

    @Override
    public int sa$getTimer(String id) {
        return tickTimers.getOrDefault(id, -1);
    }

    @Override
    public boolean sa$hasTimer(String id) {
        return tickTimers.containsKey(id);
    }

    @Override
    public void sa$removeTimer(String id) {
        tickTimers.remove(id);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void writeTimers(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound timersTag = new NbtCompound();

        for (var entry : tickTimers.entrySet()) {
            timersTag.putInt(entry.getKey(), entry.getValue());
        }

        nbt.put("slimearenamod:timers", timersTag);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readTimers(NbtCompound nbt, CallbackInfo ci) {
        tickTimers.clear();

        if (nbt.contains("slimearenamod:timers")) {
            NbtCompound timersTag = nbt.getCompound("slimearenamod:timers");

            for (String key : timersTag.getKeys()) {
                tickTimers.put(key, timersTag.getInt(key));
            }
        }
    }


//    @Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
//    private void modifyTeamColor(CallbackInfoReturnable<Integer> cir) {
//        Entity entity = (Entity) (Object) this;
//        if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(StatusEffects.UNLUCK)) {
//            cir.setReturnValue(0xFFFF0000);
//        }
//    }

}