package com.tuserverdor.simpleeconomy;

import com.tuserverdor.simpleeconomy.commands.*;
import com.tuserverdor.simpleeconomy.database.DatabaseManager;
import com.tuserverdor.simpleeconomy.listeners.PlayerJoinListener;
import com.tuserverdor.simpleeconomy.listeners.ShopListener;
import com.tuserverdor.simpleeconomy.shop.ShopManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleEconomy extends JavaPlugin {

    private static SimpleEconomy instance;
    private DatabaseManager databaseManager;
    private ShopManager shopManager;
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final Map<String, String> economyMappings = new HashMap<>();
    private double initialBalance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadEconomyMappings();
        initialBalance = getConfig().getDouble("initial-balance", 1000.0);

        this.databaseManager = new DatabaseManager(this);
        databaseManager.loadPlayersData();

        this.shopManager = new ShopManager(this);
        shopManager.loadShops();

        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("shopadmin").setExecutor(new ShopAdminCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);

        getLogger().info("SimpleEconomy ha sido habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        databaseManager.savePlayersData();
        getLogger().info("SimpleEconomy ha sido deshabilitado.");
    }

    public static SimpleEconomy getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public void setPlayerData(UUID uuid, PlayerData data) {
        playerDataMap.put(uuid, data);
    }

    private void loadEconomyMappings() {
        if (getConfig().isConfigurationSection("economy-map")) {
            for (String world : getConfig().getConfigurationSection("economy-map").getKeys(false)) {
                economyMappings.put(world.toLowerCase(), getConfig().getString("economy-map." + world));
            }
        }
        getLogger().info("Mapeo de economías cargado: " + economyMappings.size() + " reglas.");
    }
    
    public String getEconomyForWorld(String worldName) {
        return economyMappings.getOrDefault(worldName.toLowerCase(), "default");
    }

    public ShopManager getShopManager() {
        return this.shopManager;
    }

    public double getInitialBalance() {
        return this.initialBalance;
    }
    
    // Este es el método que te faltaba para el EcoCommand
    public Map<String, String> getEconomyMapping() {
        return this.economyMappings;
    }

    // --- MÉTODO DE LA API (CORREGIDO) ---
    public double getBalance(Player player, String worldName) {
        String economyName = economyMappings.getOrDefault(worldName.toLowerCase(), "default");
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data == null) {
            return 0.0;
        }
        return data.getBalances().getOrDefault(economyName, 0.0);
    }
}