package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.components.PlayerData;
import com.spokiy.slimearenamod.components.SAComponents;
import com.spokiy.slimearenamod.components.PlayerClass;
import com.spokiy.slimearenamod.util.shop.ShopUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.spokiy.slimearenamod.util.NameTagManager.updatePlayerScoreboardTeam;

public class Util {
    public static final List<PlayerClass> HUMAN_CLASSES = List.of(
            PlayerClass.HUMAN
    );
    public static final List<PlayerClass> SLIME_CLASSES = List.of(
            PlayerClass.SLIME, PlayerClass.SPRINTER, PlayerClass.SUPPORT, PlayerClass.HUNTER
    );

    public static float randomRange(Random random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }


    public static void changePlayerClass(ServerPlayerEntity player, PlayerData playerData, PlayerClass playerClass) {
        playerData.setPlayerClass(playerClass);
        SAComponents.PLAYER_DATA.sync(player);

        giveClassItems(player, playerClass);

        updatePlayerScoreboardTeam(player, playerClass);
    }
    public static void changePlayerClass(ServerPlayerEntity player, PlayerClass playerClass) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        changePlayerClass(player, playerData, playerClass);
    }
    public static void giveClassItems(ServerPlayerEntity player, PlayerClass playerClass) {
        clearArenaItems(player);
    }


    public static void initPlayer(ServerPlayerEntity player,  PlayerData playerData) {
        changePlayerClass(player, playerData, PlayerClass.NONE);

        clearArenaItems(player);
    }
    public static void initPlayer(ServerPlayerEntity player) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        initPlayer(player, playerData);
    }


    public static void curePlayer(ServerPlayerEntity player, PlayerData playerData) {
        changePlayerClass(player, playerData, PlayerClass.HUMAN);

        clearArenaItems(player);
    }
    public static void curePlayer(ServerPlayerEntity player) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        curePlayer(player, playerData);

        player.sendMessage(Text
                .translatable("message.slimearenamod.cured_message")
                .formatted(Formatting.YELLOW));
    }

    public static void infectPlayer(ServerPlayerEntity player, PlayerData playerData) {
        changePlayerClass(player, playerData, PlayerClass.SLIME);

        player.sendMessage(Text
                .translatable("message.slimearenamod.infected_message")
                .formatted(Formatting.GREEN));
    }
    public static void infectPlayer(ServerPlayerEntity player) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        infectPlayer(player, playerData);
    }


    public static void clearArenaItems(ServerPlayerEntity player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (!stack.isEmpty() && stack.isIn(SATags.Items.ARENA_ITEMS)) {
                inventory.setStack(i, ItemStack.EMPTY);
            }
        }
        player.giveItemStack(new ItemStack(Items.EMERALD, EMERALDS_TO_GIVE));
    }

    public static Identifier getTextureByClass(PlayerClass playerClass) {
        return switch (playerClass) {
            case SLIME -> SlimeArenaMod.prefix("textures/player/slime.png");
            case SPRINTER -> SlimeArenaMod.prefix("textures/player/slime_sprinter.png");
            case HUNTER -> SlimeArenaMod.prefix("textures/player/slime_hunter.png");
            case SUPPORT -> SlimeArenaMod.prefix("textures/player/slime_support.png");
            default -> null;
        };
    }

    public static List<Text> getLore(ItemStack stack) {
        LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
        if (loreComponent != null) {
            return loreComponent.lines(); // List<Text>
        } else {
            return List.of(); // якщо немає lore
        }
    }

    public static EntityHitResult getTargetedPlayer(ServerPlayerEntity player, double range) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d direction = player.getRotationVec(1.0F);
        Vec3d end = start.add(direction.multiply(range));

        HitResult blockHit = player.getWorld().raycast(
                new RaycastContext(
                        start,
                        end,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        player
                )
        );

        double maxDistance = range;

        if (blockHit.getType() != HitResult.Type.MISS) {
            maxDistance = blockHit.getPos().distanceTo(start);
        }

        Vec3d newEnd = start.add(direction.multiply(maxDistance));

        Box box = player.getBoundingBox()
                .stretch(direction.multiply(maxDistance))
                .expand(1.0D, 1.0D, 1.0D);

        return ProjectileUtil.raycast(
                player,
                start,
                newEnd,
                box,
                entity -> entity instanceof ServerPlayerEntity
                        && entity.isAlive()
                        && entity != player,
                maxDistance * maxDistance
        );
    }


    public static void spawnParticleBeam(ServerWorld world, ParticleEffect particle, Vec3d start, Vec3d end, double step) {
        Vec3d direction = end.subtract(start);
        double length = direction.length();
        Vec3d normalized = direction.normalize();

        for (double i = 0; i < length; i += step) {
            Vec3d pos = start.add(normalized.multiply(i));
            world.spawnParticles(
                    particle,
                    pos.x, pos.y, pos.z,
                    1,
                    0.0, 0.0, 0.0,
                    0.0
            );
        }
    }

    public static ItemStack customPotion(Item potionType, RegistryEntry<StatusEffect> effect, int duration, int amplifier, boolean showParticles, List<Text> lore) {
        ItemStack potion = new ItemStack(potionType);
        String id = Objects.requireNonNull(Registries.STATUS_EFFECT.getId(effect.value())).getPath();
        potion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(
                Optional.empty(), Optional.of(effect.value().getColor()),
                List.of(new StatusEffectInstance(effect, duration, amplifier, false, showParticles, true))));

        nameItemStack(potion, id, "potion", lore);
        return potion;
    }
    public static ItemStack customPotion(Item potionType, RegistryEntry<StatusEffect> effect, int duration, int amplifier, List<Text> lore) {
        return customPotion(potionType, effect, duration, amplifier, true, lore);
    }
    public static ItemStack customPotion(Item potionType, RegistryEntry<StatusEffect> effect, int duration, int amplifier, boolean showParticles) {
        return customPotion(potionType, effect, duration, amplifier, showParticles, null);
    }
    public static ItemStack customPotion(Item potionType, RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
        return customPotion(potionType, effect, duration, amplifier, true, null);
    }

//    public static ItemStack enchantedItem(Item item, RegistryEntry<Enchantment> enchantment, int level, List<Text> lore) {
//        ItemStack stack = new ItemStack(Items.STICK);
//        String id = Registries.ITEM.getId(item).getPath();
//        nameItemStack(stack, id, "enchanted", lore);
//        return stack;
//    }

    private static void nameItemStack(ItemStack stack, String id, String group, List<Text> lore) {
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("item.slimearenamod." + group + '.' + id).setStyle(ShopUtil.shopStyle()));
        if (lore != null) {
            List<Text> newLore = new ArrayList<>(List.of(Text.empty()));
            for (Text line : lore) newLore.add(line.copy().setStyle(ShopUtil.shopStyle(Formatting.DARK_PURPLE)));
            stack.set(DataComponentTypes.LORE, new LoreComponent(newLore));
        }
    }

    public static final int EMERALDS_TO_GIVE = 10;
    public static final float SLIME_SWIM_SPEED_MULTIPLIER = 1.5f;

    // Humans
    public static final double SLIME_BALL_KNOCKBACK_STRENGTH = 1.2D;

    public static final int ECHO_SHARD_SONIC_BOOM_RANGE = 12;
    public static final double ECHO_SHARD_SONIC_BOOM_HITBOX_STEP = 0.8;
    public static final double ECHO_SHARD_SONIC_BOOM_HITBOX_WIDTH = 1.2;
    public static final double ECHO_SHARD_SONIC_BOOM_KNOCKBACK_XZ = 1.5;
    public static final double ECHO_SHARD_SONIC_BOOM_KNOCKBACK_Y = 0.5;

    public static final double DRIVABLE_MINECART_STEP = 0.2;
    public static final double DRIVABLE_MINECART_JUMP_STRENGTH = 0.5;

    // Slimes
    public static final int SPRINTER_ABILITY_COOLDOWN = 15;
    public static final int SPRINTER_SPEED_EFFECT_DURATION = 5;
    public static final int SPRINTER_SPEED_EFFECT_AMPLIFIER = 1;
    public static final double SPRINTER_DASH_STRENGTH = 1.1D;

    public static final int HUNTER_ABILITY_COOLDOWN = 30;
    public static final double HUNTER_ABILITY_RADIUS = 24D;
    public static final int HUNTER_ABILITY_DURATION = 8;

    public static final int SUPPORT_EFFECT_DURATION = 8;
    public static final int SUPPORT_MAX_SPEED_AMPLIFIER = 1;
    public static final int SUPPORT_MAX_JUMP_BOOST_AMPLIFIER = 1;
    public static final int SUPPORT_ABILITY_COOLDOWN = 20;
    public static final double SUPPORT_ABILITY_RANGE = 32;
    public static final int SUPPORT_ABILITY_SLOWNESS_EFFECT_DURATION = 4;
    public static final int SUPPORT_ABILITY_SLOWNESS_EFFECT_AMPLIFIER = 1;
    public static final int SUPPORT_ABILITY_BEAM_COLOR = 9154528;

}
