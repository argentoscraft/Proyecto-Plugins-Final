package com.tuservidor.serverguard.listeners;

import com.tuservidor.serverguard.ServerGuard;
import com.tuservidor.serverguard.managers.RegionManager;
import com.tuservidor.serverguard.objects.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final RegionManager regionManager;

    public BlockBreakListener(ServerGuard plugin) {
        this.regionManager = plugin.getRegionManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // Los OPs o jugadores con el permiso de bypass pueden ignorar las flags
        if (player.hasPermission("serverguard.bypass")) {
            return;
        }

        Region region = regionManager.getRegionAt(event.getBlock().getLocation());
        if (region == null) {
            return; // No está dentro de ninguna región, no hacemos nada
        }

        String blockBreakFlag = region.getFlag("block-break");
        if ("deny".equalsIgnoreCase(blockBreakFlag)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("¡No puedes romper bloques en esta zona!", NamedTextColor.RED));
        }
    }
}