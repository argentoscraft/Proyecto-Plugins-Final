package com.tuserverdor.interfacecore;

import com.tuserverdor.interfacecore.commands.RelojCommand;
import com.tuserverdor.interfacecore.commands.ScoreboardToggleCommand;
import com.tuserverdor.interfacecore.commands.TiendaCommand;
import com.tuserverdor.interfacecore.listeners.PlayerConnectionListener;
import com.tuserverdor.interfacecore.managers.PlayerDataManager;
import com.tuserverdor.interfacecore.managers.ScoreboardManager;
import com.tuserverdor.interfacecore.managers.TablistManager;
import com.tuserverdor.interfacecore.placeholders.PluginPlaceholders; // Importamos la nueva clase
import com.tuservidor.ranksystem.RankSystem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class InterfaceCore extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private ScoreboardManager scoreboardManager;
    private TablistManager tablistManager;
    private RankSystem rankSystem;

    private BukkitTask scoreboardTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("No se encontró PlaceholderAPI. El plugin se desactivará.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // --- LÍNEA AÑADIDA PARA REGISTRAR PLACEHOLDERS ---
        new PluginPlaceholders(this).register();

        Plugin rankSystemPlugin = Bukkit.getPluginManager().getPlugin("RankSystem");
        if (rankSystemPlugin instanceof RankSystem) {
            this.rankSystem = (RankSystem) rankSystemPlugin;
        } else {
            getLogger().warning("No se encontró el plugin RankSystem. Algunas funciones pueden no estar disponibles.");
            this.rankSystem = null;
        }

        this.playerDataManager = new PlayerDataManager();
        this.scoreboardManager = new ScoreboardManager(this);
        this.tablistManager = new TablistManager(this);

        getCommand("reloj").setExecutor(new RelojCommand(this));
        getCommand("tienda").setExecutor(new TiendaCommand(this));
        getCommand("sb").setExecutor(new ScoreboardToggleCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);

        startScoreboardTask();

        getLogger().info("InterfaceCore ha sido activado correctamente.");
    }

    @Override
    public void onDisable() {
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
        }
        getLogger().info("InterfaceCore ha sido desactivado.");
    }

    private void startScoreboardTask() {
        this.scoreboardTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(this.scoreboardManager::updatePlayerScoreboard);
        }, 0L, 20L);
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public TablistManager getTablistManager() {
        return tablistManager;
    }

    public RankSystem getRankSystem() {
        return rankSystem;
    }
}