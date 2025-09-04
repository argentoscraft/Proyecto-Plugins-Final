package com.tuservidor.stafftools.commands;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.ItemMeta; // <-- ¡ESTE ES EL IMPORT QUE FALTABA!

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerNavigatorGUI implements Listener {

    private final StaffTools plugin;
    private final String GUI_TITLE_BASE = "Jugadores Conectados";
    private final int PLAYERS_PER_PAGE = 45;

    public PlayerNavigatorGUI(StaffTools plugin) {
        this.plugin = plugin;
    }

    public void open(Player staff, int page) {
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
                                            .filter(p -> !p.equals(staff))
                                            .collect(Collectors.toList());

        if (onlinePlayers.isEmpty()) {
            staff.sendMessage(Component.text("No hay otros jugadores conectados.", NamedTextColor.YELLOW));
            return;
        }

        int totalPages = (int) Math.ceil((double) onlinePlayers.size() / PLAYERS_PER_PAGE);
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        String guiTitle = GUI_TITLE_BASE + " (Página " + page + "/" + totalPages + ")";
        Inventory gui = Bukkit.createInventory(null, 54, Component.text(guiTitle));

        int startIndex = (page - 1) * PLAYERS_PER_PAGE;
        int endIndex = Math.min(startIndex + PLAYERS_PER_PAGE, onlinePlayers.size());

        for (int i = startIndex; i < endIndex; i++) {
            Player target = onlinePlayers.get(i);
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwningPlayer(target);
            meta.displayName(Component.text(target.getName()).color(NamedTextColor.GREEN));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Clic para teletransportarte").color(NamedTextColor.GRAY));
            meta.lore(lore);
            playerHead.setItemMeta(meta);
            gui.addItem(playerHead);
        }

        if (page > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.displayName(Component.text("<< Página Anterior").color(NamedTextColor.YELLOW));
            prevPage.setItemMeta(prevMeta);
            gui.setItem(48, prevPage);
        }

        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.displayName(Component.text("Página Siguiente >>").color(NamedTextColor.YELLOW));
            nextPage.setItemMeta(nextMeta);
            gui.setItem(50, nextPage);
        }

        staff.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.startsWith(GUI_TITLE_BASE)) return;

        event.setCancelled(true);

        Player staff = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String itemName = PlainTextComponentSerializer.plainText().serialize(clickedItem.getItemMeta().displayName());

        if (clickedItem.getType() == Material.ARROW) {
            int currentPage = Integer.parseInt(title.replaceAll(".*Página (\\d+)/.*", "$1"));
            if (itemName.contains("Siguiente")) {
                open(staff, currentPage + 1);
            } else if (itemName.contains("Anterior")) {
                open(staff, currentPage - 1);
            }
            return;
        }

        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            Player target = Bukkit.getPlayer(itemName);
            if (target != null && target.isOnline()) {
                staff.teleport(target.getLocation());
                staff.sendMessage(Component.text("Te has teletransportado a " + target.getName() + ".", NamedTextColor.GREEN));
                staff.closeInventory();
            } else {
                staff.sendMessage(Component.text("Ese jugador ya no está conectado.", NamedTextColor.RED));
                open(staff, 1);
            }
        }
    }
}