package com.tuservidor.ranksystem.commands;

import com.tuservidor.ranksystem.RankSystem;
import com.tuservidor.ranksystem.objects.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class KitGUI implements Listener {

    private final RankSystem plugin;
    private final String GUI_TITLE = "Menú de Kits";

    public KitGUI(RankSystem plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        List<String> availableKits = plugin.getRankManager().getAllKits(plugin.getPlayerDataManager().getPlayerRank(player.getUniqueId()));
        Inventory gui = Bukkit.createInventory(null, 27, Component.text(GUI_TITLE));

        for (String kitName : availableKits) {
            Kit kit = plugin.getKitManager().getKit(kitName);
            if (kit == null) continue;

            long lastClaimed = plugin.getPlayerDataManager().getKitCooldown(player.getUniqueId(), kitName);
            long cooldown = kit.getCooldown();
            long timeRemaining = (lastClaimed + cooldown) - (System.currentTimeMillis() / 1000);

            ItemStack item;
            ItemMeta meta;

            if (timeRemaining > 0) {
                item = new ItemStack(Material.RED_WOOL);
                meta = item.getItemMeta();
                meta.displayName(Component.text(kitName).color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("No disponible").color(NamedTextColor.GRAY));
                lore.add(Component.text("Tiempo restante: " + formatTime(timeRemaining)).color(NamedTextColor.GRAY));
                meta.lore(lore);
            } else {
                item = new ItemStack(Material.GREEN_WOOL);
                meta = item.getItemMeta();
                meta.displayName(Component.text(kitName).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("¡Disponible para reclamar!").color(NamedTextColor.GRAY));
                meta.lore(lore);
            }
            item.setItemMeta(meta);
            gui.addItem(item);
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().title().equals(Component.text(GUI_TITLE))) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        Player player = (Player) event.getWhoClicked();
        String kitName = PlainTextComponentSerializer.plainText().serialize(clickedItem.getItemMeta().displayName());

        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) return;

        long lastClaimed = plugin.getPlayerDataManager().getKitCooldown(player.getUniqueId(), kitName);
        long cooldown = kit.getCooldown();
        long timeRemaining = (lastClaimed + cooldown) - (System.currentTimeMillis() / 1000);

        if (timeRemaining > 0) {
            player.sendMessage(Component.text("¡Aún no puedes reclamar este kit!").color(NamedTextColor.RED));
            player.closeInventory();
            return;
        }

        for (ItemStack item : kit.getItems()) {
            player.getInventory().addItem(item);
        }

        plugin.getPlayerDataManager().setKitCooldown(player.getUniqueId(), kitName);
        player.sendMessage(Component.text("¡Has reclamado el kit '" + kitName + "'!").color(NamedTextColor.GREEN));
        player.closeInventory();
    }

    private String formatTime(long seconds) {
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long secs = seconds % 60;
        if (days > 0) return String.format("%dd %dh", days, hours);
        if (hours > 0) return String.format("%dh %dm", hours, minutes);
        if (minutes > 0) return String.format("%dm %ds", minutes, secs);
        return String.format("%ds", secs);
    }
}