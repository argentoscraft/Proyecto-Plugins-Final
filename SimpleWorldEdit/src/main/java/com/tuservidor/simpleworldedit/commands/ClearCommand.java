package com.tuservidor.simpleworldedit.commands;

import com.tuservidor.simpleworldedit.SimpleWorldEdit;
import com.tuservidor.simpleworldedit.objects.BlockChange;
import com.tuservidor.simpleworldedit.objects.Selection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClearCommand implements CommandExecutor {

    private final SimpleWorldEdit plugin;

    public ClearCommand(SimpleWorldEdit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Este comando solo puede ser usado por un jugador."));
            return true;
        }
        Player player = (Player) sender;
        Selection selection = plugin.getPlayerSelection(player);

        if (!selection.isComplete()) {
            player.sendMessage(Component.text("Debes seleccionar dos posiciones primero con la varita.", NamedTextColor.RED));
            return true;
        }

        World world = selection.getPos1().getWorld();
        int minX = Math.min(selection.getPos1().getBlockX(), selection.getPos2().getBlockX());
        int minY = Math.min(selection.getPos1().getBlockY(), selection.getPos2().getBlockY());
        int minZ = Math.min(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ());
        int maxX = Math.max(selection.getPos1().getBlockX(), selection.getPos2().getBlockX());
        int maxY = Math.max(selection.getPos1().getBlockY(), selection.getPos2().getBlockY());
        int maxZ = Math.max(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ());

        // Guardamos el historial para que sea "deshacible"
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

        // Rellenamos con aire
        for (BlockChange change : changes) {
            change.getLocation().getBlock().setType(Material.AIR, false);
        }

        player.sendMessage(Component.text("¡Selección limpiada! Se han modificado " + changes.size() + " bloques. Usa //undo para deshacer.", NamedTextColor.GREEN));
        return true;
    }
}