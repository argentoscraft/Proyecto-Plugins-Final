package com.tuserverdor.interfacecore.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public void loadPlayerData(Player player) {
        playerDataMap.put(player.getUniqueId(), new PlayerData());
    }

    public PlayerData getPlayerData(Player player) {
        if (!playerDataMap.containsKey(player.getUniqueId())) {
            loadPlayerData(player);
        }
        return playerDataMap.get(player.getUniqueId());
    }

    public void removePlayerData(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }

    // --- MÉTODOS CORREGIDOS Y AÑADIDOS ---

    public void toggleScoreboard(Player player) {
        PlayerData data = getPlayerData(player);
        if (data != null) {
            data.toggleScoreboard();
        }
    }

    public void toggleClock(Player player) {
        PlayerData data = getPlayerData(player);
        if (data != null) {
            data.toggleClock();
        }
    }
    
    public boolean hasClockEnabled(Player player) {
        PlayerData data = getPlayerData(player);
        return data != null && data.isClockActive();
    }

    public java.util.List<Player> getPlayersWithClockEnabled() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(this::hasClockEnabled)
                .collect(Collectors.toList());
    }
}