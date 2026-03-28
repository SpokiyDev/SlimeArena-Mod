package com.spokiy.slimearenamod.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

public class EffectConfig {
    private final RegistryEntry<StatusEffect> effect;
    private final int duration;
    private final int amplifier;
    private final boolean ambient;
    private final boolean showParticles;
    private final boolean showIcon;

    public EffectConfig(RegistryEntry<StatusEffect> effect, int duration, int amplifier,
                        boolean ambient, boolean showParticles, boolean showIcon) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.showParticles = showParticles;
        this.showIcon = showIcon;
    }

    public StatusEffectInstance create() {
        return new StatusEffectInstance(effect, duration, amplifier, ambient, showParticles, showIcon);
    }
}