package com.tuserverdor.simpleeconomy;

import com.tuserverdor.simpleeconomy.commands.*;
import com.tuserverdor.simpleeconomy.database.DatabaseManager;
import com.tuserverdor.simpleeconomy.listeners.PlayerJoinListener;
import com.tuserverdor.simpleeconomy.listeners.ShopListener;
import com.tuserverdor.simpleeconomy.shop.ShopManager;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleEconomy extends JavaPlugin {
    private HikariDataSource hikari;
    private DatabaseManager databaseManager;
    private ShopManager shopManager;
    private final Map<String, String> worldToEconomyMap = new HashMap<>();
    private double initialBalance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.initialBalance = getConfig().getDouble("settings.initial-balance", 1000.0);

        if (!setupDatabaseConnection()) {
            getLogger().severe("Error al conectar con la base de datos. El plugin se desactivará.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupDatabaseTables();
        this.databaseManager = new DatabaseManager(this);
        this.shopManager = new ShopManager(this);
        setupWorldMappings();

        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("shopadmin").setExecutor(new ShopAdminCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);
        getLogger().info("SimpleEconomy ha sido habilitado correctamente.");
    }
    
    @Override
    public void onDisable() {
        if (hikari != null && !hikari.isClosed()) {
            hikari.close();
        }
        getLogger().info("SimpleEconomy ha sido deshabilitado.");
    }

    private boolean setupDatabaseConnection() {
        try {
            hikari = new HikariDataSource();
            hikari.setJdbcUrl("jdbc:mysql://" + getConfig().getString("database.host") + ":" + getConfig().getString("database.port") + "/" + getConfig().getString("database.database"));
            hikari.setUsername(getConfig().getString("database.user"));
            hikari.setPassword(getConfig().getString("database.password"));
            hikari.addDataSourceProperty("cachePrepStmts", "true");
            hikari.addDataSourceProperty("prepStmtCacheSize", "250");
            hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    private void setupDatabaseTables() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS player_balances (uuid VARCHAR(36) NOT NULL, username VARCHAR(16) NOT NULL, economy_context VARCHAR(50) NOT NULL, balance DOUBLE NOT NULL DEFAULT 0, PRIMARY KEY (uuid, economy_context));";
        try (Connection conn = getDatabaseConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) { getLogger().severe("No se pudo crear la tabla en la base de datos."); e.printStackTrace(); }
    }
    private void setupWorldMappings() {
        worldToEconomyMap.put("prision", "prison");
        worldToEconomyMap.put("survival", "survival");
        worldToEconomyMap.put("survivalop", "survivalop");
        worldToEconomyMap.put("prisionop", "prisonop");
        worldToEconomyMap.put("skyblock", "skyblock");
        worldToEconomyMap.put("oneblock", "oneblock");
        getLogger().info("Mapeo de economías cargado: " + worldToEconomyMap.size() + " reglas.");
    }
    public Connection getDatabaseConnection() throws SQLException { return hikari.getConnection(); }
    public DatabaseManager getDatabaseManager() { return this.databaseManager; }
    public ShopManager getShopManager() { return this.shopManager; }
    public double getInitialBalance() { return this.initialBalance; }
    public String getEconomyContext(Player player) { return worldToEconomyMap.get(player.getWorld().getName().toLowerCase()); }
    public Collection<String> getEconomyMappings() { return worldToEconomyMap.values(); }

// Pega este método DENTRO de la clase SimpleEconomy, al final
public double getBalance(Player player, String worldName) {
    // CORRECCIÓN: Tu variable se llama 'economyMappings', no 'economyMap'
    String economyName = economyMappings.getOrDefault(worldName.toLowerCase(), "default");

    // CORRECCIÓN: Tu mapa de datos se llama 'playerDataMap', no 'playerData'
    PlayerData data = playerDataMap.get(player.getUniqueId());

    if (data == null) {
        return 0.0;
    }

    return data.getBalances().getOrDefault(economyName, 0.0);
}
}