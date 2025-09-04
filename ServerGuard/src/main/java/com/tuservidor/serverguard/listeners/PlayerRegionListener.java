package com.tuservidor.serverguard.listeners;

import com.tuservidor.serverguard.ServerGuard;
import com.tuservidor.serverguard.objects.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title; // <-- IMPORT NUEVO
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.Duration; // <-- IMPORT NUEVO
import java.util.HashMap;
import java.util.UUID;

public class PlayerRegionListener implements Listener {

    private final ServerGuard plugin;
    private final HashMap<UUID, Region> lastPlayerRegion = new HashMap<>();

    public PlayerRegionListener(ServerGuard plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        handleRegionChange(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        handleRegionChange(event.getPlayer());
    }

    private void handleRegionChange(Player player) {
        Region currentRegion = plugin.getRegionManager().getRegionAt(player.getLocation());
        Region previousRegion = lastPlayerRegion.get(player.getUniqueId());

        if (currentRegion != previousRegion) {
            if (previousRegion != null) {
                handleRegionExit(player, previousRegion);
            }
            if (currentRegion != null) {
                handleRegionEntry(player, currentRegion);
            }
            lastPlayerRegion.put(player.getUniqueId(), currentRegion);
        }
    }

    private void handleRegionEntry(Player player, Region region) {
        String entryMessage = region.getFlag("entry-message");
        if (entryMessage != null) {
            // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
            Component mainTitle = LegacyComponentSerializer.legacyAmpersand().deserialize(entryMessage);
            Component subtitle = Component.text(""); // Subtítulo vacío

            // Creamos un objeto Title con los tiempos
            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500)); // 10, 40, 10 ticks
            Title title = Title.title(mainTitle, subtitle, times);
            
            // Enviamos el título al jugador
            player.showTitle(title);
        }

        String flyFlag = region.getFlag("fly");
        if ("allow".equalsIgnoreCase(flyFlag)) {
            player.setAllowFlight(true);
        }
    }

    private void handleRegionExit(Player player, Region region) {
        String flyFlag = region.getFlag("fly");
        if ("allow".equalsIgnoreCase(flyFlag) && !player.hasPermission("serverguard.fly.permanent")) {
            player.setAllowFlight(false);
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Region region = plugin.getRegionManager().getRegionAt(player.getLocation());

        if (region == null) return;

        String invincibleFlag = region.getFlag("invincible");
        if ("allow".equalsIgnoreCase(invincibleFlag)) {
            event.setCancelled(true);
        }
    }
}