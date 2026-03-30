package com.spokiy.slimearenamod.data;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.Map;

public class WorldData implements Component, AutoSyncedComponent {
    int gameTimer;
    String gameTag;

    GamePhaseType currentPhase = GamePhaseType.LOBBY;
    public static Map<GamePhaseType, GamePhase> GAME_PHASES = Map.of(
        GamePhaseType.LOBBY, GamePhase.empty(),
        GamePhaseType.SLIME, new GamePhase(5, "", BossBar.Color.GREEN),
        GamePhaseType.PLAYING, new GamePhase(10, "", BossBar.Color.WHITE)
    );


    public GamePhaseType getPhase() { return currentPhase; }
    public void setPhase(GamePhaseType phase) { this.currentPhase = phase; }

    public int getGameTimer() { return gameTimer; }
    public void initGameTimer(int value) {
        setGameTimer(value);
    }
    public void setGameTimer(int value) {
        gameTimer = value;

        if (value > 0) {
            int totalSeconds = value / 20;
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            String time = String.format("%02d:%02d", minutes, seconds);
            if (hours > 0) time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            GamePhase timer = GAME_PHASES.get(currentPhase);
            SlimeArenaMod.bossBar.setName(Text.of(time));
            SlimeArenaMod.bossBar.setColor(timer.color);
            SlimeArenaMod.bossBar.setPercent((float) value / timer.maxTimerValue);
        }

    }

    public String getGameTag() { return gameTag; }
    public void setGameTag(String value) { gameTag = value; }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("game_phase")) currentPhase = (GamePhaseType.valueOf(tag.getString("game_phase")));
        gameTimer = tag.getInt("game_timer");
        gameTag = tag.getString("game_tag");
        if (GAME_PHASES.get(currentPhase).equals(GamePhase.empty())) SlimeArenaMod.bossBar.setVisible(false);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("game_phase", currentPhase.name());
        tag.putInt("game_timer", getGameTimer());
        tag.putString("game_tag", gameTag);
    }
}
