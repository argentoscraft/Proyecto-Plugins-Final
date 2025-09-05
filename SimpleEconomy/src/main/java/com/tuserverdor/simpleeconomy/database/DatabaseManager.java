package com.tuserverdor.simpleeconomy.database;

import com.tuserverdor.simpleeconomy.PlayerData;
import com.tuserverdor.simpleeconomy.SimpleEconomy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    private final SimpleEconomy plugin;
    private final Map<UUID, PlayerData> playerDataCache = new HashMap<>();

    public DatabaseManager(SimpleEconomy plugin) {
        this.plugin = plugin;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.computeIfAbsent(uuid, PlayerData::new);
    }
    
    public void loadPlayerData(UUID uuid) {
        // En un futuro, aquí cargarías los datos desde un archivo o base de datos
        playerDataCache.put(uuid, new PlayerData(uuid));
    }
    
    public void saveAllPlayerData(){
        // Aquí guardarías los datos
    }

    public double getBalance(UUID uuid, String economyContext) {
        return getPlayerData(uuid).getBalances().getOrDefault(economyContext, 0.0);
    }

    public void setMoney(UUID uuid, String economyContext, double amount) {
        getPlayerData(uuid).getBalances().put(economyContext, amount);
    }

    public void addMoney(UUID uuid, String economyContext, double amount) {
        double currentBalance = getBalance(uuid, economyContext);
        setMoney(uuid, economyContext, currentBalance + amount);
    }

    public void removeMoney(UUID uuid, String economyContext, double amount) {
        double currentBalance = getBalance(uuid, economyContext);
        setMoney(uuid, economyContext, Math.max(0, currentBalance - amount));
    }
}