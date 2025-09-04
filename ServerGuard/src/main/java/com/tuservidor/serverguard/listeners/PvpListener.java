package com.tuservidor.serverguard.listeners; // <-- LÍNEA CORREGIDA

import com.tuservidor.serverguard.ServerGuard;
import com.tuservidor.serverguard.managers.RegionManager;
import com.tuservidor.serverguard.objects.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvpListener implements Listener {

    private final RegionManager regionManager;

    public PvpListener(ServerGuard plugin) {
        this.regionManager = plugin.getRegionManager();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        
        if (attacker.hasPermission("serverguard.bypass")) {
            return;
        }

        Region region = regionManager.getRegionAt(victim.getLocation());
        if (region == null) {
            return;
        }

        String pvpFlag = region.getFlag("pvp");
        if ("deny".equalsIgnoreCase(pvpFlag)) {
            event.setCancelled(true);
            attacker.sendMessage(Component.text("¡El PvP está desactivado en esta zona!", NamedTextColor.RED));
        }
    }
}