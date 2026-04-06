package com.spokiy.slimearenamod.config;

import com.spokiy.slimearenamod.data.GamePhase;
import com.spokiy.slimearenamod.data.GamePhaseType;
import com.spokiy.slimearenamod.data.PlayerClass;
import com.spokiy.slimearenamod.util.EffectConfig;
import com.spokiy.slimearenamod.world.effect.SAStatusEffects;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.Map;

public class Config {
    public static ServerConfig DATA;

    public static Vec3d LOBBY_POS = new Vec3d(0, -60, 0);
    public static Vec3d TRAINING_POS = new Vec3d(500, -60, 0);

    public static GameMode ARENA_GAMEMODE = GameMode.ADVENTURE;
    public static Map<GamePhaseType, GamePhase> GAME_PHASES = Map.of(
            GamePhaseType.LOBBY, GamePhase.empty(),
            GamePhaseType.SLIME, new GamePhase("", BossBar.Color.GREEN),
            GamePhaseType.PLAYING, new GamePhase("", BossBar.Color.WHITE)
    );
    public static final Map<PlayerClass, Formatting> CLASS_COLORS = Map.of(
            // Human
            PlayerClass.HUMAN, Formatting.YELLOW,
            // Slime
            PlayerClass.SLIME, Formatting.GREEN,
            PlayerClass.SPRINTER, Formatting.AQUA,
            PlayerClass.MAGE, Formatting.LIGHT_PURPLE,
            PlayerClass.HUNTER, Formatting.RED,
            PlayerClass.TRAPPER, Formatting.GOLD,
            PlayerClass.SUPPORT, Formatting.YELLOW
    );

    // Humans
    // Throwables
    public static final EffectConfig SLIME_BALL_EFFECT =
            new EffectConfig(StatusEffects.SLOWNESS, 5, 0, false, true, true);
    public static final EffectConfig PUFFERFISH_EFFECT =
            new EffectConfig(StatusEffects.NAUSEA, 20, 0, false, true, true);

    public static final EffectConfig HONEY_BLOCK_EFFECT =
            new EffectConfig(SAStatusEffects.STICKINESS, 10, 1, false, true, true);

    public static final int ECHO_SHARD_SONIC_BOOM_RANGE = 12;
    public static final double ECHO_SHARD_SONIC_BOOM_HITBOX_STEP = 0.8;
    public static final double ECHO_SHARD_SONIC_BOOM_HITBOX_WIDTH = 1.2;
    public static final double ECHO_SHARD_SONIC_BOOM_KNOCKBACK_XZ = 1.5;
    public static final double ECHO_SHARD_SONIC_BOOM_KNOCKBACK_Y = 0.5;

    public static final EffectConfig PUMPKIN_EFFECT =
            new EffectConfig(StatusEffects.BLINDNESS, 5, 0, false, true, true);

    public static final EffectConfig[] CAKE_EFFECTS = {
            new EffectConfig(StatusEffects.SPEED, 6, 3, false, true, true),
            new EffectConfig(StatusEffects.JUMP_BOOST, 6, 1, false, true, true),
    };

    // Consumables
    public static final int KNOCKBACK_HORN_COOLDOWN = 20;

    public static final double DRIVABLE_MINECART_STEP = 0.2;
    public static final double DRIVABLE_MINECART_JUMP_STRENGTH = 0.5;

    // Slimes
    public static final int SPRINTER_ABILITY_COOLDOWN = 15;
    public static final EffectConfig SPRINTER_SPEED_EFFECT =
            new EffectConfig(StatusEffects.SPEED, 5, 1, false, true, true);
    public static final double SPRINTER_DASH_STRENGTH = 1.1D;

    public static final int MAGE_ABILITY_COOLDOWN = 15;
    public static final int MAGE_TELEPORT_DISTANCE = 12;

    public static final int HUNTER_ABILITY_COOLDOWN = 30;
    public static final double HUNTER_ABILITY_RADIUS = 16D;
    public static final EffectConfig[] HUNTER_ABILITY_EFFECTS = {
            new EffectConfig(StatusEffects.GLOWING, 8, 0, false, false, true)
    };
    public static final EffectConfig[] HUNTER_ABILITY_BUFFS = {
            new EffectConfig(StatusEffects.INVISIBILITY, 5, 0, false, false, true),
            new EffectConfig(StatusEffects.SPEED, 5, 0, false, false, true)
    };

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
