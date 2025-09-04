package com.tuservidor.stafftools.listeners;

import com.tuservidor.stafftools.StaffTools;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent; // <-- IMPORT NUEVO
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FreezeListener implements Listener {

    private final StaffTools plugin;
    private final Component freezeMessage = Component.text("¡Estás congelado! No puedes hacer eso.", NamedTextColor.RED);

    public FreezeListener(StaffTools plugin) {
        this.plugin = plugin;
    }

    private boolean isFrozen(Player player) {
        return plugin.getFreezeManager().isFrozen(player);
    }

    // --- ¡NUEVO MÉTODO PARA BLOQUEAR DAÑO REALIZADO! ---
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageDealt(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            if (isFrozen(attacker)) {
                event.setCancelled(true);
                attacker.sendMessage(freezeMessage);
            }
        }
    }

    // --- EL RESTO DE MÉTODOS QUE YA TENÍAMOS ---

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (isFrozen(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(freezeMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isFrozen(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(freezeMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isFrozen(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(freezeMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick() || event.getAction().isRightClick()) {
            if (isFrozen(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (isFrozen(player)) {
                event.setCancelled(true);
                player.sendMessage(freezeMessage);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isFrozen(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player) && !event.getMessage().toLowerCase().startsWith("/msg") && !event.getMessage().toLowerCase().startsWith("/r")) {
            event.setCancelled(true);
            player.sendMessage(freezeMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player)) {
            event.setCancelled(true);
            Component staffChatMessage = Component.text("[FREEZE-CHAT] ", NamedTextColor.AQUA)
                .append(Component.text(player.getName() + ": ", NamedTextColor.GRAY))
                .append(event.message());
            
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("stafftools.freeze.chatsee")) {
                    onlinePlayer.sendMessage(staffChatMessage);
                }
            }
            plugin.getLogger().info("[FREEZE-CHAT] " + player.getName() + ": " + PlainTextComponentSerializer.plainText().serialize(event.message()));
        }
    }
}