package com.tuservidor.simpleworldedit.listeners;

import com.tuservidor.simpleworldedit.SimpleWorldEdit;
import com.tuservidor.simpleworldedit.commands.WandCommand;
import com.tuservidor.simpleworldedit.objects.Selection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WandListener implements Listener {

    private final SimpleWorldEdit plugin;

    public WandListener(SimpleWorldEdit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() != Material.WOODEN_AXE || !itemInHand.hasItemMeta() || itemInHand.getItemMeta().displayName() == null) {
            return;
        }
        
        String itemName = PlainTextComponentSerializer.plainText().serialize(itemInHand.getItemMeta().displayName());
        if (!itemName.equals(WandCommand.WAND_NAME)) {
             return;
        }

        Action action = event.getAction();
        if (event.getClickedBlock() == null) return;
        
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            
            Location blockLocation = event.getClickedBlock().getLocation();
            Selection selection = plugin.getPlayerSelection(player);

            if (action == Action.LEFT_CLICK_BLOCK) {
                selection.setPos1(blockLocation);
                player.sendMessage(Component.text("Posición 1 establecida en: " + formatLocation(blockLocation), NamedTextColor.LIGHT_PURPLE));
            } else {
                selection.setPos2(blockLocation);
                player.sendMessage(Component.text("Posición 2 establecida en: " + formatLocation(blockLocation), NamedTextColor.LIGHT_PURPLE));
            }
        }
    }
    
    private String formatLocation(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }
}