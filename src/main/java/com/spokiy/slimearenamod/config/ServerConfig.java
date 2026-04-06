package com.spokiy.slimearenamod.config;

import com.spokiy.slimearenamod.data.GamePhaseType;

import java.util.Map;

public class ServerConfig {
    public Map<GamePhaseType, Integer> phaseTimes = Map.of(
            GamePhaseType.LOBBY, 0,
            GamePhaseType.SLIME, 30,
            GamePhaseType.PLAYING, 300
    );

    public int emeraldsToGive = 15;
    public float slimeSwimSpeedMultiplier = 1.5f;

    public int trapperTrapCount = 3;

}
