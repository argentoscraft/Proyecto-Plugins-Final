package com.tuserverdor.simpleeconomy;

import com.tuserverdor.simpleeconomy.commands.BalanceCommand;
import com.tuserverdor.simpleeconomy.commands.EcoCommand;
import com.tuserverdor.simpleeconomy.commands.PayCommand;
import com.tuserverdor.simpleeconomy.database.DatabaseManager;
import com.tuserverdor.simpleeconomy.listeners.PlayerJoinListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SimpleEconomy extends JavaPlugin {

    private DatabaseManager databaseManager;
    private final Map<String, String> economyMappings = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadEconomyMappings();

        this.databaseManager = new DatabaseManager(this);

        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        // Agrega aquí el resto de tus comandos y listeners si los tienes...

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getLogger().info("SimpleEconomy ha sido habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.saveAllPlayerData();
        }
        getLogger().info("SimpleEconomy ha sido deshabilitado.");
    }

    private void loadEconomyMappings() {
        if (getConfig().isConfigurationSection("economy-map")) {
            for (String world : getConfig().getConfigurationSection("economy-map").getKeys(false)) {
                economyMappings.put(world.toLowerCase(), getConfig().getString("economy-map." + world));
            }
        }
    }

    public String getEconomyContext(Player player) {
        return economyMappings.getOrDefault(player.getWorld().getName().toLowerCase(), "default");
    }
    
    // --- API PÚBLICA ---
    public double getBalance(Player player, String worldName) {
        String economyContext = economyMappings.getOrDefault(worldName.toLowerCase(), "default");
        return databaseManager.getBalance(player.getUniqueId(), economyContext);
    }
    
    // --- GETTERS ---
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public Map<String, String> getEconomyMappings() {
        return economyMappings;
    }
}