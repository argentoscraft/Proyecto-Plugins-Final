package com.tuserverdor.interfacecore.listeners;

import com.tuserverdor.interfacecore.InterfaceCore;
import com.tuserverdor.interfacecore.managers.PlayerDataManager;
import com.tuserverdor.interfacecore.managers.ScoreboardManager;
import com.tuserverdor.interfacecore.managers.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final InterfaceCore plugin;
    private final PlayerDataManager playerDataManager;
    private final TablistManager tablistManager;
    private final ScoreboardManager scoreboardManager;

    public PlayerConnectionListener(InterfaceCore plugin) {
        this.plugin = plugin;
        this.playerDataManager = plugin.getPlayerDataManager();
        this.tablistManager = plugin.getTablistManager();
        this.scoreboardManager = plugin.getScoreboardManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // --- MENSAJE ESPÍA 1 ---
        plugin.getLogger().info("¡Jugador " + player.getName() + " ha entrado! Aplicando interfaces...");

        playerDataManager.loadPlayerData(player);
        tablistManager.setTablist(player);
        scoreboardManager.updatePlayerScoreboard(player);

        // --- MENSAJE ESPÍA 2 ---
        plugin.getLogger().info("¡Interfaces aplicadas para " + player.getName() + "!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataManager.removePlayerData(player);
    }
}