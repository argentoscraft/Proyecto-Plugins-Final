package com.tuservidor.serverguard.listeners;

import com.tuservidor.serverguard.ServerGuard;
import com.tuservidor.serverguard.managers.RegionManager;
import com.tuservidor.serverguard.objects.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

public class PlayerUseItemListener implements Listener {

    private final RegionManager regionManager;
    private final List<Material> blockedItems = Arrays.asList(
            Material.FLINT_AND_STEEL,
            Material.LAVA_BUCKET,
            Material.TNT_MINECART,
            Material.TNT
    );

    public PlayerUseItemListener(ServerGuard plugin) {
        this.regionManager = plugin.getRegionManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("serverguard.bypass")) {
            return;
        }
        if (event.getItem() == null || !blockedItems.contains(event.getItem().getType())) {
            return;
        }

        Region region = regionManager.getRegionAt(player.getLocation());
        if (region == null) {
            return;
        }
        
        String useFlag = region.getFlag("use");
        if ("deny".equalsIgnoreCase(useFlag)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("¡No puedes usar ese item en esta zona!", NamedTextColor.RED));
        }
    }
}