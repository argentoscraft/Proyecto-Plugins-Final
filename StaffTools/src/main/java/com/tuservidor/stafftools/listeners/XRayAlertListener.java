package com.tuservidor.stafftools.listeners;

import com.tuservidor.stafftools.StaffTools;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class XRayAlertListener implements Listener {

    private final StaffTools plugin;

    public XRayAlertListener(StaffTools plugin) {
        this.plugin = plugin;
    }

    // Usamos ignoreCancelled=true para no contar bloques que al final no se rompieron
    // y una prioridad baja para que se ejecute antes que otros plugins de protección
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // Solo nos interesan los jugadores en modo supervivencia
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        // Le pasamos la información al manager para que él decida si es sospechoso
        plugin.getXRayAlertManager().checkPlayerOreBreak(player, event.getBlock().getType());
    }
}