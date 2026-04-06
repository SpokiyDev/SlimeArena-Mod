package com.spokiy.slimearenamod.data;

import net.minecraft.entity.boss.BossBar;

public class GamePhase {
//    public int maxTimerValue;
    public String prefix;
    public BossBar.Color color;

    public static GamePhase empty() {
        return new GamePhase("", BossBar.Color.WHITE);
    };

    public GamePhase(String prefix, BossBar.Color color) {
//        this.maxTimerValue = maxTimerValue * 20;
        this.prefix = prefix;
        this.color = color;
    }
}
