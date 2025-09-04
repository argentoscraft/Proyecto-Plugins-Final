package com.tuservidor.serverguard.listeners; // <-- LÍNEA CORREGIDA

import com.tuservidor.serverguard.ServerGuard;
import com.tuservidor.serverguard.managers.RegionManager;
import com.tuservidor.serverguard.objects.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final RegionManager regionManager;

    public BlockPlaceListener(ServerGuard plugin) {
        this.regionManager = plugin.getRegionManager();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("serverguard.bypass")) {
            return;
        }

        Region region = regionManager.getRegionAt(event.getBlock().getLocation());
        if (region == null) {
            return;
        }

        String blockPlaceFlag = region.getFlag("block-place");
        if ("deny".equalsIgnoreCase(blockPlaceFlag)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("¡No puedes colocar bloques en esta zona!", NamedTextColor.RED));
        }
    }
}