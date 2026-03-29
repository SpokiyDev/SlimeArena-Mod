package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.components.PlayerClass;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class NameTagManager {
    private static final Map<PlayerClass, Formatting> COLORS = Map.of(
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

    public static void updatePlayerScoreboardTeam(ServerPlayerEntity player, PlayerClass playerClass) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();

        String teamName = playerClass.name().toLowerCase();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.addTeam(teamName);

            team.setPrefix(Text.translatable("class.slimearenamod." + teamName));

        }
        // Set color
        Formatting color = COLORS.get(playerClass);
        if (color == null) color = Formatting.WHITE;
        team.setColor(color);

        scoreboard.addScoreHolderToTeam(player.getName().getString(), team);
    }
}