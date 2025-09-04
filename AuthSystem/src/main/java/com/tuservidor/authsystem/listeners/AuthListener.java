package com.tuservidor.authsystem.listeners;

import com.tuservidor.authsystem.AuthSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthListener implements Listener {

    private final AuthSystem plugin;
    private final Set<UUID> authenticatedPlayers = new HashSet<>();
    private final HashMap<UUID, BukkitRunnable> freezeTasks = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> kickTasks = new HashMap<>();

    public AuthListener(AuthSystem plugin) {
        this.plugin = plugin;
    }

    // --- ¡LA CORRECCIÓN ESTÁ AQUÍ! ---
    // Usamos la prioridad HIGH para que este evento se ejecute DESPUÉS del de ForceSpawn.
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // La tarea de congelación ahora comenzará DESPUÉS de que el jugador haya sido teletransportado.
        startFreezeTask(player);
        startKickTask(player);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            if (plugin.getAuthManager().isRegistered(player.getUniqueId())) {
                player.sendMessage(Component.text("Por favor, inicia sesión con /login <contraseña>", NamedTextColor.YELLOW));
            } else {
                player.sendMessage(Component.text("Por favor, regístrate con /register <contraseña> <contraseña>", NamedTextColor.AQUA));
            }
        }, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        authenticatedPlayers.remove(playerUUID);
        stopFreezeTask(event.getPlayer());
        stopKickTask(event.getPlayer());
    }

    public void setAuthenticated(Player player) {
        authenticatedPlayers.add(player.getUniqueId());
        stopFreezeTask(player);
        stopKickTask(player);
    }

    private void startFreezeTask(Player player) {
        Location freezeLocation = player.getLocation();
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (authenticatedPlayers.contains(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                player.teleport(freezeLocation);
            }
        };
        task.runTaskTimer(plugin, 0L, 1L);
        freezeTasks.put(player.getUniqueId(), task);
    }
    
    // ... (El resto del archivo, que ya tienes y funciona bien, no cambia) ...

    private void stopFreezeTask(Player player) {
        if (freezeTasks.containsKey(player.getUniqueId())) {
            freezeTasks.get(player.getUniqueId()).cancel();
            freezeTasks.remove(player.getUniqueId());
        }
    }
    
    private void startKickTask(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!authenticatedPlayers.contains(player.getUniqueId()) && player.isOnline()) {
                    Component kickMessage = Component.text("Has sido expulsado por no iniciar sesión a tiempo.", NamedTextColor.RED);
                    player.kick(kickMessage);
                }
            }
        };
        task.runTaskLater(plugin, 1200L);
        kickTasks.put(player.getUniqueId(), task);
    }

    private void stopKickTask(Player player) {
        if (kickTasks.containsKey(player.getUniqueId())) {
            kickTasks.get(player.getUniqueId()).cancel();
            kickTasks.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) { if (!authenticatedPlayers.contains(event.getPlayer().getUniqueId())) { String command = event.getMessage().toLowerCase(); if (!command.startsWith("/login") && !command.startsWith("/register")) { event.setCancelled(true); event.getPlayer().sendMessage(Component.text("Debes iniciar sesión o registrarte para hacer eso.", NamedTextColor.RED)); } } }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) { if (!authenticatedPlayers.contains(event.getPlayer().getUniqueId())) { event.setCancelled(true); } }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) { if (!authenticatedPlayers.contains(event.getPlayer().getUniqueId())) { event.setCancelled(true); } }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) { if (!authenticatedPlayers.contains(event.getPlayer().getUniqueId())) { event.setCancelled(true); } }
}