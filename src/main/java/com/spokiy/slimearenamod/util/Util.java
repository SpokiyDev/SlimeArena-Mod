package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.data.*;
import com.spokiy.slimearenamod.config.Config;
import com.spokiy.slimearenamod.datagen.SATags;
import com.spokiy.slimearenamod.util.shop.ShopUtil;
import com.spokiy.slimearenamod.world.effect.SAStatusEffects;
import com.spokiy.slimearenamod.world.item.SAItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;

import java.util.*;

import static com.spokiy.slimearenamod.util.NameTagManager.updatePlayerScoreboardTeam;

public class Util {
    public static final PlayerClass[] SLIME_CLASSES = {
            PlayerClass.SLIME, PlayerClass.SPRINTER, PlayerClass.MAGE, PlayerClass.HUNTER, PlayerClass.TRAPPER, PlayerClass.SUPPORT
    };

    public static float randomRange(Random random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
    // Source - https://stackoverflow.com/a/1086134
    // Posted by dfa, modified by community. See post 'Timeline' for change history
    // Retrieved 2026-04-02, License - CC BY-SA 4.0
    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }



    public static void changePlayerClass(ServerPlayerEntity player, PlayerData playerData, PlayerClass playerClass) {
        playerData.setPlayerClass(playerClass);
        SAComponents.PLAYER_DATA.sync(player);

        player.clearStatusEffects();
        giveClassItems(player, playerClass);
        if (playerClass.equals(PlayerClass.HUMAN)) {
            player.giveItemStack(new ItemStack(Items.EMERALD, Config.DATA.emeraldsToGive));
        }
        else if (playerData.getPlayerTeam().equals(PlayerTeam.SLIME)) {
            player.addStatusEffect(new StatusEffectInstance(SAStatusEffects.SLIME_TEAM, -1, 0, false, false, false));
        }

        updatePlayerScoreboardTeam(player, playerClass);
    }
    public static void changePlayerClass(ServerPlayerEntity player, PlayerClass playerClass) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        changePlayerClass(player, playerData, playerClass);
    }
    public static void giveClassItems(ServerPlayerEntity player, PlayerClass playerClass) {
        clearArenaItems(player);

        List<ItemStack> items = new ArrayList<>();
        switch(playerClass) {
            case TRAPPER -> {
                for (int i = 1; i <= Config.DATA.trapperTrapCount; i++) {
                    ItemStack stack = new ItemStack(SAItems.SLIME_TRAP);
                    Text name = Text.empty().append(stack.getName()).append(Text.of(" №" + i));
                    stack.set(DataComponentTypes.ITEM_NAME, name);
                    items.add(stack);
                }
            }
        }

        for(ItemStack item : items) player.giveItemStack(item);

    }

    public static void curePlayer(ServerPlayerEntity player, PlayerData playerData) {
        changePlayerClass(player, playerData, PlayerClass.HUMAN);

        player.networkHandler.sendPacket(
                new TitleS2CPacket(Text.translatable("message.slimearenamod.cured_message").formatted(Formatting.YELLOW)));
    }
    public static void curePlayer(ServerPlayerEntity player) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        curePlayer(player, playerData);
    }

    public static void infectPlayer(ServerPlayerEntity player, PlayerData playerData, PlayerClass playerClass) {
        ServerWorld world = player.getServerWorld();
        changePlayerClass(player, playerData, playerClass);

        player.networkHandler.sendPacket(
                new TitleS2CPacket(Text.translatable("message.slimearenamod.infected_message").formatted(Formatting.GREEN)));

        // Count survivors
        List<ServerPlayerEntity> survivors = new ArrayList<>();
        for (ServerPlayerEntity p : world.getPlayers()) {
            PlayerData data = SAComponents.PLAYER_DATA.get(p);
            if (data.getPlayerTeam() == PlayerTeam.HUMAN) survivors.add(p);
        }

        // End the game if no survivors left
        if (survivors.isEmpty()) {
            WorldData worldData = SAComponents.WORLD_DATA.get(world);
            endGame(world, worldData, PlayerTeam.SLIME);
        }
        else if (survivors.size() == 1) {
            survivors.getFirst().giveItemStack(new ItemStack(Items.EMERALD, Config.DATA.emeraldsToGive));
        }

    }
    public static void infectPlayer(ServerPlayerEntity player, PlayerData playerData) {
        infectPlayer(player, playerData, PlayerClass.SLIME);
    }
    public static void infectPlayer(ServerPlayerEntity player, PlayerClass playerClass) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        infectPlayer(player, playerData, playerClass);
    }
    public static void endGame(ServerWorld world, WorldData worldData, PlayerTeam team) {

        SlimeArenaMod.bossBar.setVisible(false);
        SlimeArenaMod.bossBar.clearPlayers();

        worldData.setCurrentPhase(GamePhaseType.LOBBY);
        worldData.setGameTimer(-1);

        for (ServerPlayerEntity player : world.getPlayers()) {
            player.changeGameMode(Config.ARENA_GAMEMODE);
            Util.changePlayerClass(player, SAComponents.PLAYER_DATA.get(player), PlayerClass.HUMAN);
            player.teleport(world, 0, -60, 0, player.getYaw(), 0);

            if (!player.getCommandTags().isEmpty()) {
                for (String tag : new HashSet<>(player.getCommandTags())) player.removeCommandTag(tag);
            }

            if (team == PlayerTeam.HUMAN) {
                player.networkHandler.sendPacket(
                        new TitleS2CPacket(Text.translatable("message.slimearenamod.human_win").formatted(Formatting.YELLOW)));
            }
            else if (team == PlayerTeam.SLIME) {
                player.networkHandler.sendPacket(
                        new TitleS2CPacket(Text.translatable("message.slimearenamod.slime_win").formatted(Formatting.GREEN)));
            }
            else if (team == PlayerTeam.NONE) {
                player.networkHandler.sendPacket(
                        new TitleS2CPacket(Text.translatable("message.slimearenamod.none_win").formatted(Formatting.RED)));
            }

        }
    }


    public static void clearArenaItems(ServerPlayerEntity player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (!stack.isEmpty() && stack.isIn(SATags.Items.ARENA_ITEMS)) {
                inventory.setStack(i, ItemStack.EMPTY);
            }
        }

    }

    public static Identifier getTextureByClass(PlayerClass playerClass) {
        return switch (playerClass) {
            case SLIME -> SlimeArenaMod.prefix("textures/player/slime.png");
            case SPRINTER -> SlimeArenaMod.prefix("textures/player/sprinter.png");
            case MAGE -> SlimeArenaMod.prefix("textures/player/mage.png");
            case HUNTER -> SlimeArenaMod.prefix("textures/player/hunter.png");
            case TRAPPER -> SlimeArenaMod.prefix("textures/player/trapper.png");
            case SUPPORT -> SlimeArenaMod.prefix("textures/player/support.png");
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

    public static EntityHitResult getTargetedEntity(ServerPlayerEntity player, double range) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d direction = player.getRotationVec(1.0F);
        Vec3d end = start.add(direction.multiply(range));

        HitResult blockHit = player.getWorld().raycast(
                new RaycastContext(
                        start,
                        end,
                        RaycastContext.ShapeType.COLLIDER,
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
                entity -> entity instanceof LivingEntity && entity.isAlive(),
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
        stack.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.slimearenamod." + group + '.' + id));
        if (lore != null) {
            List<Text> newLore = new ArrayList<>();
            for (Text line : lore) newLore.add(line.copy().setStyle(ShopUtil.shopStyle(Formatting.DARK_PURPLE)));
            stack.set(DataComponentTypes.LORE, new LoreComponent(newLore));
        }
    }

    public static void playSlimeSound(ServerWorld world, Entity entity, float volume) {
        world.playSound(
                null,
                entity.getBlockPos(),
                SoundEvents.ENTITY_SLIME_JUMP,
                SoundCategory.NEUTRAL,
                volume,
                Util.randomRange(world.random, 0.85F, 1.15F)
        );
    }

    public static List<Text> quickLore(Item item, Formatting color) {
        String id = Registries.ITEM.getId(item).getPath();

        List<Text> lore = new ArrayList<>();
        String raw = Text.translatable("item.slimearenamod." + id + ".desc").getString();
        for (String line : raw.split("\n")) lore.add(parseHighlight(line, color).setStyle(ShopUtil.shopStyle()));


        return lore;
    }
    public static List<Text> quickLore(ItemStack stack, Formatting color) {
        return quickLore(stack.getItem(), color);
    }
    public static List<Text> quickLore(ItemStack stack) {
        return quickLore(stack.getItem(), Formatting.GRAY);
    }
    public static List<Text> quickLore(Item item, int price) {
        return quickLore(item, ShopUtil.getLoreColorByPrice(price));
    }

    private static MutableText parseHighlight(String line, Formatting color) {
        MutableText result = Text.empty();

        char activeSymbol = 0;
        StringBuilder current = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '_' || c == '#' || c == '~') {

                if (!current.isEmpty()) {
                    result.append(style(current.toString(), activeSymbol, color));
                    current.setLength(0);
                }

                if (activeSymbol == c) activeSymbol = 0;
                else activeSymbol = c;

                continue;
            }

            current.append(c);
        }

        if (!current.isEmpty()) result.append(style(current.toString(), activeSymbol, color));

        return result;
    }

    private static MutableText style(String text, char symbol, Formatting color) {
        MutableText t = Text.literal(text);

        if (symbol == '_') return t.styled(s -> s.withColor(0xd4c27c));
        if (symbol == '#') return t.formatted(Formatting.GOLD);
        if (symbol == '~') return t.formatted(Formatting.AQUA);

        return t.formatted(color);
    }

    public static void customCooldown(PlayerEntity user, Item item, int seconds) {
        if (user.isCreative()) user.getItemCooldownManager().set(item, 20);
        else                   user.getItemCooldownManager().set(item, 20 * seconds);
    }


}
