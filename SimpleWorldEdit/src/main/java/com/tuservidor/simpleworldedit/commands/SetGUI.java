package com.tuservidor.simpleworldedit.commands;

import com.tuservidor.simpleworldedit.SimpleWorldEdit;
import com.tuservidor.simpleworldedit.objects.BlockChange;
import com.tuservidor.simpleworldedit.objects.Selection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetGUI implements Listener {

    private final SimpleWorldEdit plugin;
    private final String GUI_TITLE = "Elige un bloque para rellenar";
    private final int BLOCK_SLOT = 13;

    public SetGUI(SimpleWorldEdit plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, Component.text(GUI_TITLE));
        ItemStack placeholder = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
        meta.displayName(Component.text(" ").color(NamedTextColor.BLACK));
        placeholder.setItemMeta(meta);

        for (int i = 0; i < gui.getSize(); i++) {
            if (i != BLOCK_SLOT) {
                gui.setItem(i, placeholder);
            }
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().title().equals(Component.text(GUI_TITLE))) return;

        Player player = (Player) event.getPlayer();
        ItemStack chosenItem = event.getInventory().getItem(BLOCK_SLOT);

        if (chosenItem == null || !chosenItem.getType().isBlock()) {
            if(chosenItem != null) player.sendMessage(Component.text("Operación cancelada.", NamedTextColor.YELLOW));
            return;
        }

        Material materialToSet = chosenItem.getType();
        Selection selection = plugin.getPlayerSelection(player);

        if (!selection.isComplete()) {
            player.sendMessage(Component.text("Debes seleccionar dos posiciones primero con la varita.", NamedTextColor.RED));
            return;
        }

        World world = selection.getPos1().getWorld();
        int minX = Math.min(selection.getPos1().getBlockX(), selection.getPos2().getBlockX());
        int minY = Math.min(selection.getPos1().getBlockY(), selection.getPos2().getBlockY());
        int minZ = Math.min(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ());
        int maxX = Math.max(selection.getPos1().getBlockX(), selection.getPos2().getBlockX());
        int maxY = Math.max(selection.getPos1().getBlockY(), selection.getPos2().getBlockY());
        int maxZ = Math.max(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ());

        // --- LÓGICA DE GUARDADO DE HISTORIAL ---
        List<BlockChange> changes = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    changes.add(new BlockChange(block.getLocation(), block.getBlockData()));
                }
            }
        }
        plugin.getHistoryManager().recordChange(player.getUniqueId(), changes);

        // --- LÓGICA DE RELLENADO ---
        for (BlockChange change : changes) {
            change.getLocation().getBlock().setType(materialToSet, false); // false para no causar actualizaciones de física
        }

        player.sendMessage(Component.text("¡Operación completada! Se han modificado " + changes.size() + " bloques. Usa /undo para deshacer.", NamedTextColor.GREEN));
    }
}