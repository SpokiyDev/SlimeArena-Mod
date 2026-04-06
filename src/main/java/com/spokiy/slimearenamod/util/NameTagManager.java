package com.spokiy.slimearenamod.util;

import com.spokiy.slimearenamod.config.Config;
import com.spokiy.slimearenamod.data.PlayerClass;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;
import java.util.Map;

public class NameTagManager {

    public static void updatePlayerScoreboardTeam(ServerPlayerEntity player, PlayerClass playerClass) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        Scoreboard scoreboard = server.getScoreboard();

        String className = playerClass.name().toLowerCase();
        Team team = scoreboard.getTeam(className);

        if (team == null) {
            team = scoreboard.addTeam(className);

            String name = Text.translatable("class.slimearenamod." + className.toLowerCase()).getString()
                    .toUpperCase(Locale.ROOT);
            team.setPrefix(Text.literal("[" + name + "] "));

        }
        // Set color
        Formatting color = Config.CLASS_COLORS.get(playerClass);
        if (color == null) color = Formatting.WHITE;
        team.setColor(color);

        scoreboard.addScoreHolderToTeam(player.getName().getString(), team);
    }
}