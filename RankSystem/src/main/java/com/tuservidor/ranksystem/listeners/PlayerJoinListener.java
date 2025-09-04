package com.tuservidor.ranksystem.listeners;

import com.tuservidor.ranksystem.RankSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent; // <-- IMPORT NUEVO
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final RankSystem plugin;
    private final HashMap<UUID, PermissionAttachment> playerAttachments = new HashMap<>();

    public PlayerJoinListener(RankSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            applyPermissions(player);
            plugin.updatePlayerTablist(player);
        }, 10L);
    }

    // --- ¡NUEVO MÉTODO PARA LIMPIAR AL SALIR! ---
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        // Si tenemos un "adjunto" de permisos para este jugador, lo eliminamos
        if (playerAttachments.containsKey(playerUUID)) {
            // No es necesario usar removeAttachment aquí, ya que el jugador se va
            // Lo importante es eliminarlo de nuestro mapa para que no cause problemas la próxima vez que entre
            playerAttachments.remove(playerUUID);
        }
    }

    public void applyPermissions(Player player) {
        UUID playerUUID = player.getUniqueId();

        // Esta línea es la que causa el error. La vamos a quitar
        // porque la nueva lógica de onPlayerQuit la hace innecesaria y peligrosa.
        // if (playerAttachments.containsKey(playerUUID)) {
        //     player.removeAttachment(playerAttachments.get(playerUUID));
        // }

        // En su lugar, recalculamos todos los permisos del jugador, lo que limpia los viejos de forma segura
        player.recalculatePermissions();

        PermissionAttachment attachment = player.addAttachment(plugin);
        playerAttachments.put(playerUUID, attachment);

        String rankName = plugin.getPlayerDataManager().getPlayerRank(playerUUID);

        for (String permission : plugin.getRankManager().getAllPermissions(rankName)) {
            attachment.setPermission(permission, true);
        }
        
        // No es necesario llamar a recalculatePermissions() dos veces
    }
}