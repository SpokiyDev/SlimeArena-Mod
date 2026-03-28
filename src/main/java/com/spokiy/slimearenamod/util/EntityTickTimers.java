package com.spokiy.slimearenamod.util;

public interface EntityTickTimers {
    void sa$setTimer(String id, int ticks);
    int sa$getTimer(String id);
    boolean sa$hasTimer(String id);
    void sa$removeTimer(String id);
}