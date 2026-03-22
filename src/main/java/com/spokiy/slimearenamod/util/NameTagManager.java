package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.enums.PlayerClass;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class NameTagManager {
    private static final Map<String, Formatting> COLORS = Map.of(
            // Human
            "human", Formatting.YELLOW,
            "boy", Formatting.YELLOW,
            // Slime
            "slime", Formatting.GREEN,
            "support", Formatting.AQUA
    );

    public static void updatePlayerScoreboardTeam(ServerPlayerEntity player, PlayerClass playerClass) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();

        String teamName = playerClass.name().toUpperCase();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.addTeam(teamName);

            team.setPrefix(Text.literal("[" + teamName + "] "));

            // Set color
            Formatting color = COLORS.get(playerClass.name().toLowerCase());
            if (color == null) color = Formatting.WHITE;
            team.setColor(color);
        }

        scoreboard.addScoreHolderToTeam(player.getName().getString(), team);
    }
}