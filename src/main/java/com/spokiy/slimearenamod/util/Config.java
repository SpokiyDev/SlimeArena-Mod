package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.world.effect.SAStatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.ColorHelper;

import java.util.Map;

public class Config {
    public static final int EMERALDS_TO_GIVE = 15;
    public static final float SLIME_SWIM_SPEED_MULTIPLIER = 1.5f;

    // Humans
    // Throwables
    public static final EffectConfig SLIME_BALL_EFFECT =
            new EffectConfig(StatusEffects.SLOWNESS, 5, 0, false, true, true);
    public static final EffectConfig PUFFERFISH_EFFECT =
            new EffectConfig(StatusEffects.NAUSEA, 20, 0, false, true, true);

    public static final EffectConfig THROWN_HONEY_BLOCK_EFFECT =
            new EffectConfig(SAStatusEffects.STICKINESS, 10, 1, false, true, true);

    public static final int ECHO_SHARD_SONIC_BOOM_RANGE = 12;
    public static final double ECHO_SHARD_SONIC_BOOM_HITBOX_STEP = 0.8;
    public static final double ECHO_SHARD_SONIC_BOOM_HITBOX_WIDTH = 1.2;
    public static final double ECHO_SHARD_SONIC_BOOM_KNOCKBACK_XZ = 1.5;
    public static final double ECHO_SHARD_SONIC_BOOM_KNOCKBACK_Y = 0.5;

    public static final EffectConfig THROWN_PUMPKIN_EFFECT =
            new EffectConfig(StatusEffects.BLINDNESS, 5, 0, false, true, true);

    // Consumables
    public static final int KNOCKBACK_HORN_COOLDOWN = 20;

    public static final double DRIVABLE_MINECART_STEP = 0.2;
    public static final double DRIVABLE_MINECART_JUMP_STRENGTH = 0.5;

    // Slimes
    public static final int SPRINTER_ABILITY_COOLDOWN = 15;
    public static final int SPRINTER_SPEED_EFFECT_DURATION = 5;
    public static final int SPRINTER_SPEED_EFFECT_AMPLIFIER = 1;
    public static final double SPRINTER_DASH_STRENGTH = 1.1D;

    public static final int MAGE_ABILITY_COOLDOWN = 15;
    public static final int MAGE_TELEPORT_DISTANCE = 12;

    public static final int HUNTER_ABILITY_COOLDOWN = 30;
    public static final double HUNTER_ABILITY_RADIUS = 16D;
    public static final EffectConfig HUNTER_ABILITY_EFFECT =
            new EffectConfig(StatusEffects.GLOWING, 8, 0, true,false,true);
    public static final EffectConfig HUNTER_ABILITY_BUFF =
            new EffectConfig(StatusEffects.INVISIBILITY, 5, 0, true,false,true);

    public static final int TRAPPER_ABILITY_COOLDOWN = 5;
    public static final int TRAPPER_PLACE_TRAP_COOLDOWN = 2;
    public static final int TRAPPER_REMOVE_TRAP_COOLDOWN = 6;
    public static final int TRAPPER_ACTIVATE_TRAP_COOLDOWN = 4;
    public static final EffectConfig[] TRAPPER_TRAP_EFFECTS = {
            new EffectConfig(StatusEffects.GLOWING, 8, 0, false,false,true),
            new EffectConfig(StatusEffects.SLOWNESS, 6, 4, false,true,true),
    };
    public static final double TRAPPER_TRAP_PARTICLE_Y_OFFSET = 0.125;

    public static final int SUPPORT_EFFECT_DURATION = 8;
    public static final int SUPPORT_MAX_SPEED_AMPLIFIER = 1;
    public static final int SUPPORT_MAX_JUMP_BOOST_AMPLIFIER = 1;
    public static final int SUPPORT_ABILITY_COOLDOWN = 30;
    public static final double SUPPORT_ABILITY_RANGE = 32;
    public static final Map<String, EffectConfig> SUPPORT_ABILITY_SLOWNESS_EFFECTS = Map.of(
            "ally", new EffectConfig(StatusEffects.SPEED, 4, 3, false, true, true),
            "enemy", new EffectConfig(StatusEffects.SLOWNESS, 4, 3, false, true, true)
    );
    public static final Map<String, EntityEffectParticleEffect> SUPPORT_ABILITY_BEAM_PARTICLES = Map.of(
            "ally", EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, ColorHelper.Argb.fullAlpha(StatusEffects.SPEED.value().getColor())),
            "enemy", EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, ColorHelper.Argb.fullAlpha(StatusEffects.SLOWNESS.value().getColor()))
    );
}
