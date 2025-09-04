package com.tuservidor.serverguard.listeners;

import com.tuservidor.serverguard.ServerGuard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material; 
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SelectionListener implements Listener {
    private final ServerGuard plugin;

    public SelectionListener(ServerGuard plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Solo reaccionamos si el jugador tiene permiso y usa un hacha de madera
        if (!player.hasPermission("serverguard.admin") || itemInHand.getType() != Material.WOODEN_AXE) {
            return;
        }
        
        Action action = event.getAction();
        if (event.getClickedBlock() == null) return;
        
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true); // Evitamos que rompa el bloque
            
            Location blockLocation = event.getClickedBlock().getLocation();

            if (action == Action.LEFT_CLICK_BLOCK) {
                plugin.getRegionManager().setPos1(player.getUniqueId(), blockLocation);
                player.sendMessage(Component.text("Posición 1 establecida en: " + formatLocation(blockLocation), NamedTextColor.LIGHT_PURPLE));
            } else { // Clic Derecho
                plugin.getRegionManager().setPos2(player.getUniqueId(), blockLocation);
                player.sendMessage(Component.text("Posición 2 establecida en: " + formatLocation(blockLocation), NamedTextColor.LIGHT_PURPLE));
            }
        }
    }
    
    private String formatLocation(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }
}