package com.spokiy.slimearenamod.data;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.config.Config;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.ladysnake.cca.api.v3.component.Component;

public class WorldData implements Component {
    private int gameTimer = 0;
    private String gameTag = "";
    private GamePhaseType currentPhase = GamePhaseType.LOBBY;

    // Game Phase
    public GamePhaseType getCurrentPhase() { return currentPhase; }
    public void setCurrentPhase(GamePhaseType phase) { this.currentPhase = phase; }

    // Game Timer
    public int getGameTimer() { return gameTimer; }
    public void initGameTimer(GamePhaseType phase) {
        setGameTimer(Config.DATA.phaseTimes.get(phase) * 20);
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

            GamePhase phase = Config.GAME_PHASES.get(currentPhase);
            int maxTimerValue = Config.DATA.phaseTimes.get(currentPhase) * 20;
            SlimeArenaMod.bossBar.setName(Text.of(time));
            SlimeArenaMod.bossBar.setColor(phase.color);
            SlimeArenaMod.bossBar.setPercent((float) value / maxTimerValue);
        }

    }

    public String getGameTag() { return gameTag; }
    public void setGameTag(String value) { gameTag = value; }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("game_phase"))
            currentPhase = (GamePhaseType.valueOf(tag.getString("game_phase")));
        gameTimer = tag.getInt("game_timer");
        gameTag = tag.getString("game_tag");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("game_phase", currentPhase.name());
        tag.putInt("game_timer", getGameTimer());
        tag.putString("game_tag", gameTag);
    }
}
