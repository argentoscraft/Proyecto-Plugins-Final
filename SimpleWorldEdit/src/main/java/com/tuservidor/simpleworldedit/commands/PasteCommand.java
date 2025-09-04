package com.tuservidor.simpleworldedit.commands;

import com.tuservidor.simpleworldedit.SimpleWorldEdit;
import com.tuservidor.simpleworldedit.objects.BlockChange;
import com.tuservidor.simpleworldedit.objects.BlockInfo;
import com.tuservidor.simpleworldedit.objects.Clipboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PasteCommand implements CommandExecutor {

    private final SimpleWorldEdit plugin;

    public PasteCommand(SimpleWorldEdit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Este comando solo puede ser usado por un jugador."));
            return true;
        }
        Player player = (Player) sender;
        Clipboard clipboard = plugin.getPlayerClipboards().get(player.getUniqueId());

        if (clipboard == null) {
            player.sendMessage(Component.text("No tienes nada en tu portapapeles. Usa /copy primero.", NamedTextColor.RED));
            return true;
        }

        Location playerLocation = player.getLocation();
        World world = player.getWorld();
        
        // --- LÓGICA NUEVA: CALCULAR EL PUNTO DE ANCLAJE ---
        Location anchorPoint = playerLocation.clone().subtract(clipboard.getPlayerOffset());

        List<BlockChange> changes = new ArrayList<>();

        for (BlockInfo blockInfo : clipboard.getBlocks()) {
            Location newBlockLocation = anchorPoint.clone().add(blockInfo.getRelativePosition());
            Block blockToChange = world.getBlockAt(newBlockLocation);
            changes.add(new BlockChange(newBlockLocation, blockToChange.getBlockData()));
        }
        plugin.getHistoryManager().recordChange(player.getUniqueId(), changes);

        for (BlockInfo blockInfo : clipboard.getBlocks()) {
            Location newBlockLocation = anchorPoint.clone().add(blockInfo.getRelativePosition());
            newBlockLocation.getBlock().setBlockData(blockInfo.getBlockData(), false);
        }

        player.sendMessage(Component.text("¡Área pegada! (" + clipboard.getBlocks().size() + " bloques). Usa /undo para deshacer.", NamedTextColor.GREEN));
        return true;
    }
}