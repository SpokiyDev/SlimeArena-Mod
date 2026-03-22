package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.component.PlayerData;
import com.spokiy.slimearenamod.component.SAComponents;
import com.spokiy.slimearenamod.enums.PlayerClass;
import com.spokiy.slimearenamod.enums.PlayerTeam;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

import java.util.List;

import static com.spokiy.slimearenamod.util.NameTagManager.updatePlayerScoreboardTeam;

public class Util {
    public static final List<PlayerClass> HUMAN_CLASSES = List.of(
            PlayerClass.HUMAN, PlayerClass.BOY
    );
    public static final List<PlayerClass> SLIME_CLASSES = List.of(
            PlayerClass.SLIME, PlayerClass.SUPPORT
    );

    public static float randomRange(Random random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    public static void changePlayerTeam(ServerPlayerEntity player, PlayerData playerData, PlayerTeam playerTeam) {
        playerData.setPlayerTeam(playerTeam);
        SAComponents.PLAYER_DATA.sync(player);
    }
    public static void changePlayerTeam(ServerPlayerEntity player, PlayerTeam playerTeam) {
        PlayerData data = SAComponents.PLAYER_DATA.get(player);
        changePlayerTeam(player, data, playerTeam);
    }


    public static void changePlayerClass(ServerPlayerEntity player, PlayerData playerData, PlayerClass playerClass) {
        playerData.setPlayerClass(playerClass);
        SAComponents.PLAYER_DATA.sync(player);

        updatePlayerScoreboardTeam(player, playerClass);
    }
    public static void changePlayerClass(ServerPlayerEntity player, PlayerClass playerClass) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        changePlayerClass(player, playerData, playerClass);
    }


    public static void initPlayer(ServerPlayerEntity player,  PlayerData playerData) {
        changePlayerTeam(player, playerData, PlayerTeam.NONE);
        changePlayerClass(player, playerData, PlayerClass.NONE);
    }
    public static void initPlayer(ServerPlayerEntity player) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        initPlayer(player, playerData);
    }


    public static void playerBecomeHuman(ServerPlayerEntity player,  PlayerData playerData) {
        changePlayerTeam(player, playerData, PlayerTeam.HUMAN);
        changePlayerClass(player, playerData, PlayerClass.HUMAN);
    }
    public static void playerBecomeHuman(ServerPlayerEntity player) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        playerBecomeHuman(player, playerData);

        player.sendMessage(Text
                .translatable("message.slimearenamod.become_human")
                .formatted(Formatting.YELLOW));
    }

    public static void infectPlayer(ServerPlayerEntity player, PlayerData playerData) {
        changePlayerTeam(player, playerData, PlayerTeam.SLIME);
        changePlayerClass(player, playerData, PlayerClass.SLIME);

        player.sendMessage(Text
                .translatable("message.slimearenamod.caught_by_slimes")
                .formatted(Formatting.GREEN));
    }
    public static void infectPlayer(ServerPlayerEntity player) {
        PlayerData playerData = SAComponents.PLAYER_DATA.get(player);
        infectPlayer(player, playerData);
    }


    public static final float SLIME_SWIM_SPEED_MULTIPLIER = 1.5f;

}
