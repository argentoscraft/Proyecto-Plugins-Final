package com.tuserverdor.simpleeconomy.listeners;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final SimpleEconomy plugin;

    public PlayerJoinListener(SimpleEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getDatabaseManager().createPlayerAccount(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        plugin.getDatabaseManager().createPlayerAccount(event.getPlayer());
    }
}