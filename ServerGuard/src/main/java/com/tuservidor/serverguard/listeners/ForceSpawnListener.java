package com.tuservidor.serverguard.listeners;

import com.tuservidor.serverguard.ServerGuard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ForceSpawnListener implements Listener {

    private final ServerGuard plugin;
    private Location spawnLocation;

    public ForceSpawnListener(ServerGuard plugin) {
        this.plugin = plugin;
        loadSpawnLocation();
    }

    // --- ¡LA CORRECCIÓN ESTÁ AQUÍ! ---
    // Usamos la prioridad LOWEST para que este sea el PRIMER evento en ejecutarse.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (spawnLocation != null) {
            // Teletransportamos al jugador al spawn INMEDIATAMENTE al entrar.
            event.getPlayer().teleport(spawnLocation);
        }
    }

    // Este método lee la ubicación desde el config.yml (sin cambios)
    private void loadSpawnLocation() {
        if (plugin.getConfig().getBoolean("force-spawn-on-join.enabled")) {
            String worldName = plugin.getConfig().getString("force-spawn-on-join.world", "world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().severe("¡El mundo del spawn '" + worldName + "' no existe! La función de ForceSpawn está desactivada.");
                this.spawnLocation = null;
                return;
            }
            double x = plugin.getConfig().getDouble("force-spawn-on-join.x", 0.5);
            double y = plugin.getConfig().getDouble("force-spawn-on-join.y", 78.0);
            double z = plugin.getConfig().getDouble("force-spawn-on-join.z", 0.5);
            float yaw = (float) plugin.getConfig().getDouble("force-spawn-on-join.yaw", 180.0);
            float pitch = (float) plugin.getConfig().getDouble("force-spawn-on-join.pitch", 0.0);
            this.spawnLocation = new Location(world, x, y, z, yaw, pitch);
        }
    }
}