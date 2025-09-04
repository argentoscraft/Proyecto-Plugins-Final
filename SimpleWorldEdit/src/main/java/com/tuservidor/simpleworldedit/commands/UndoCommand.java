package com.tuservidor.simpleworldedit.commands;

import com.tuservidor.simpleworldedit.SimpleWorldEdit;
import com.tuservidor.simpleworldedit.objects.BlockChange;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UndoCommand implements CommandExecutor {

    private final SimpleWorldEdit plugin;

    public UndoCommand(SimpleWorldEdit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Este comando solo puede ser usado por un jugador."));
            return true;
        }
        Player player = (Player) sender;

        List<BlockChange> lastChanges = plugin.getHistoryManager().undoLastChange(player.getUniqueId());

        if (lastChanges == null) {
            player.sendMessage(Component.text("No hay nada que deshacer.", NamedTextColor.RED));
            return true;
        }

        // Restauramos los bloques a su estado anterior
        for (BlockChange change : lastChanges) {
            change.getLocation().getBlock().setBlockData(change.getPreviousState(), false);
        }

        player.sendMessage(Component.text("¡Última operación deshecha! Se han restaurado " + lastChanges.size() + " bloques.", NamedTextColor.GREEN));
        return true;
    }
}