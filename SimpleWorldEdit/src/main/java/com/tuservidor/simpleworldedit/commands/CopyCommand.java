package com.tuservidor.simpleworldedit.commands;

import com.tuservidor.simpleworldedit.SimpleWorldEdit;
import com.tuservidor.simpleworldedit.objects.BlockInfo;
import com.tuservidor.simpleworldedit.objects.Clipboard;
import com.tuservidor.simpleworldedit.objects.Selection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CopyCommand implements CommandExecutor {

    private final SimpleWorldEdit plugin;

    public CopyCommand(SimpleWorldEdit plugin) {
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

        Location playerLocation = player.getLocation();
        // --- LÓGICA NUEVA: CALCULAR EL PUNTO BASE DE LA SELECCIÓN ---
        int baseX = Math.min(selection.getPos1().getBlockX(), selection.getPos2().getBlockX());
        int baseY = Math.min(selection.getPos1().getBlockY(), selection.getPos2().getBlockY());
        int baseZ = Math.min(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ());
        Location baseLocation = new Location(player.getWorld(), baseX, baseY, baseZ);
        
        // Guardamos el "offset" del jugador relativo a la esquina de la selección
        Vector playerOffset = playerLocation.toVector().subtract(baseLocation.toVector());
        Clipboard clipboard = new Clipboard(playerOffset);

        World world = selection.getPos1().getWorld();
        int minX = Math.min(selection.getPos1().getBlockX(), selection.getPos2().getBlockX());
        int minY = Math.min(selection.getPos1().getBlockY(), selection.getPos2().getBlockY());
        int minZ = Math.min(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ());
        int maxX = Math.max(selection.getPos1().getBlockX(), selection.getPos2().getBlockX());
        int maxY = Math.max(selection.getPos1().getBlockY(), selection.getPos2().getBlockY());
        int maxZ = Math.max(selection.getPos1().getBlockZ(), selection.getPos2().getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location blockLocation = new Location(world, x, y, z);
                    // Ahora guardamos la posición relativa A LA ESQUINA de la selección, no al jugador
                    Vector relativePos = blockLocation.toVector().subtract(baseLocation.toVector());
                    clipboard.addBlock(new BlockInfo(relativePos, blockLocation.getBlock().getBlockData()));
                }
            }
        }

        plugin.getPlayerClipboards().put(player.getUniqueId(), clipboard);
        player.sendMessage(Component.text("¡Área copiada! (" + clipboard.getBlocks().size() + " bloques). Usa /paste para pegar.", NamedTextColor.GREEN));

        return true;
    }
}