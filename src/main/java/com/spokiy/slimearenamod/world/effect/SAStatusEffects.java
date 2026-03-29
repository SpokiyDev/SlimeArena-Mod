package com.spokiy.slimearenamod.world.effect;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class SAStatusEffects {

    public static final RegistryEntry<StatusEffect> STICKINESS = registerStatusEffect("stickiness",
            new StickinessEffect(StatusEffectCategory.HARMFUL, 0xf7a519)
                    .addAttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED, SlimeArenaMod.prefix("effect.stickiness.speed"),
                            -0.125F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                    .addAttributeModifier(
                            EntityAttributes.GENERIC_JUMP_STRENGTH, SlimeArenaMod.prefix("effect.stickiness.jump"),
                            -0.15F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ));

    public static final RegistryEntry<StatusEffect> GIGANTISM = registerStatusEffect("gigantism",
            new StickinessEffect(StatusEffectCategory.HARMFUL, 0xaade9b)
                    .addAttributeModifier(
                            EntityAttributes.GENERIC_STEP_HEIGHT, SlimeArenaMod.prefix("effect.gigantism.scale"),
                            0.25F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ));

    public static final RegistryEntry<StatusEffect> GRAVITY = registerStatusEffect("gravity",
            new StickinessEffect(StatusEffectCategory.HARMFUL, 0x737373)
                    .addAttributeModifier(
                            EntityAttributes.GENERIC_GRAVITY, SlimeArenaMod.prefix("effect.gravity"),
                            0.25F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ));

    public static RegistryEntry<StatusEffect> registerStatusEffect(String id, StatusEffect effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, SlimeArenaMod.prefix(id), effect);
    }

    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Status Effects for " + SlimeArenaMod.MOD_ID);

    }
}
