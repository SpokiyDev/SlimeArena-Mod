package com.spokiy.slimearenamod.component;

import com.spokiy.slimearenamod.enums.PlayerClass;
import com.spokiy.slimearenamod.enums.PlayerTeam;
import com.spokiy.slimearenamod.util.SAAbilities;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class PlayerData implements Component, AutoSyncedComponent {
    long abilityCooldown = 0;
    private PlayerTeam playerTeam = PlayerTeam.NONE;
    private PlayerClass playerClass = PlayerClass.NONE;

    public PlayerTeam getPlayerTeam() { return playerTeam; }
    public void setPlayerTeam(PlayerTeam value) { this.playerTeam = value; }

    public PlayerClass getPlayerClass() { return playerClass; }
    public void setPlayerClass(PlayerClass value) { this.playerClass = value; }

    public int getCooldown(long currentTime) { return (int)Math.max(0, abilityCooldown - currentTime); }
    public void setCooldown(long currentTime, int duration) { this.abilityCooldown = currentTime + duration; }
    public boolean isOnCooldown(long currentTime) { return abilityCooldown > currentTime; }
    public boolean hasCooldown() { return SAAbilities.COOLDOWNS.containsKey(this.playerClass); }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        abilityCooldown = tag.getLong("ability_cooldown");
        playerTeam = PlayerTeam.valueOf(tag.getString("player_team"));
        playerClass = PlayerClass.valueOf(tag.getString("player_class"));
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        tag.putLong("ability_cooldown", abilityCooldown);
        tag.putString("player_team", playerTeam.name());
        tag.putString("player_class", playerClass.name());
    }

}