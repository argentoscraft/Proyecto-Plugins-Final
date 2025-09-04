package com.tuservidor.stafftools.listeners;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class StaffToolsListener implements Listener {

    private final StaffTools plugin;

    public StaffToolsListener(StaffTools plugin) {
        this.plugin = plugin;
    }
    
    // El onPlayerQuit que estaba en FreezeListener lo movemos aquí para tener todo junto.
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Si el jugador que se desconecta estaba congelado, lo baneamos
        if (plugin.getFreezeManager().isFrozen(player)) {
            String playerName = player.getName();
            // Ejecutamos el comando de ban desde la consola del servidor de forma síncrona
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + playerName + " 7d [Freeze] Evasión de sanción.");
                plugin.getLogger().info("El jugador " + playerName + " se ha desconectado estando congelado y ha sido baneado por 7 días.");
            });
        }
    }

    // --- EL RESTO DE MÉTODOS QUE YA TENÍAS, COMPLETOS Y SIN CAMBIOS ---

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (plugin.getStaffModeManager().isInStaffMode(player) && event.getClickedInventory() == player.getInventory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getStaffModeManager().isInStaffMode(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getVanishManager().handlePlayerJoin(player);

        if (plugin.getFreezeManager().isFrozen(player)) {
            Component alertMessage = Component.text("[Alerta] ", NamedTextColor.DARK_AQUA)
                .append(Component.text(player.getName() + " se ha conectado y sigue congelado.", NamedTextColor.AQUA));
            
            Component tpButton = Component.text(" [TP HACIA ÉL]")
                .color(NamedTextColor.GREEN)
                .clickEvent(ClickEvent.runCommand("/tp " + player.getName()));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("stafftools.freeze.alert")) {
                    onlinePlayer.sendMessage(alertMessage.append(tpButton));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getFreezeManager().isFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getStaffModeManager().isInStaffMode(player)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        handleVanishClick(player, event);
        handleGamemodeSwitch(player, event);
        handleNavigatorClick(player, event);
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
        Player staff = (Player) event.getDamager();
        Player target = (Player) event.getEntity();
        if (!plugin.getStaffModeManager().isInStaffMode(staff)) return;
        ItemStack itemInHand = staff.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.ICE && itemInHand.hasItemMeta()) {
             String itemName = PlainTextComponentSerializer.plainText().serialize(itemInHand.getItemMeta().displayName());
             if (itemName.equals("Vara Congeladora")) {
                 event.setCancelled(true);
                 plugin.getFreezeManager().toggleFreeze(target, staff);
             }
        }
    }

    private void handleVanishClick(Player player, PlayerInteractEvent event) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!itemInHand.hasItemMeta() || itemInHand.getItemMeta().displayName() == null) return;
        String itemName = PlainTextComponentSerializer.plainText().serialize(itemInHand.getItemMeta().displayName());
        if (itemName.startsWith("Vanish")) {
            event.setCancelled(true);
            if (plugin.getVanishManager().isVanished(player)) {
                plugin.getVanishManager().disableVanish(player);
                player.getInventory().setItemInMainHand(plugin.getStaffModeManager().createVanishItem(false));
            } else {
                plugin.getVanishManager().enableVanish(player);
                player.getInventory().setItemInMainHand(plugin.getStaffModeManager().createVanishItem(true));
            }
        }
    }

    private void handleGamemodeSwitch(Player player, PlayerInteractEvent event) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if ((itemInHand.getType() != Material.GRASS_BLOCK && itemInHand.getType() != Material.BEDROCK) || !itemInHand.hasItemMeta() || itemInHand.getItemMeta().displayName() == null) return;
        String itemName = PlainTextComponentSerializer.plainText().serialize(itemInHand.getItemMeta().displayName());
        if (itemName.startsWith("Modo:")) {
            event.setCancelled(true);
            if (player.getGameMode() == GameMode.CREATIVE) {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().setItemInMainHand(plugin.getStaffModeManager().createGamemodeSwitcher(GameMode.SURVIVAL));
            } else {
                player.setGameMode(GameMode.CREATIVE);
                player.getInventory().setItemInMainHand(plugin.getStaffModeManager().createGamemodeSwitcher(GameMode.CREATIVE));
            }
        }
    }

    private void handleNavigatorClick(Player player, PlayerInteractEvent event) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != Material.ENDER_EYE || !itemInHand.hasItemMeta() || itemInHand.getItemMeta().displayName() == null) return;
        String itemName = PlainTextComponentSerializer.plainText().serialize(itemInHand.getItemMeta().displayName());
        if (itemName.equals("Navegador de Jugadores")) {
            event.setCancelled(true);
            plugin.getPlayerNavigatorGUI().open(player, 1);
        }
    }
}