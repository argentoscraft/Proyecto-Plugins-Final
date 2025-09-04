package com.tuservidor.simpleworldedit.commands;

import com.tuservidor.simpleworldedit.SimpleWorldEdit;
import com.tuservidor.simpleworldedit.objects.Selection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetCommand implements CommandExecutor {

    private final SimpleWorldEdit plugin;
    private final SetGUI setGUI;

    public SetCommand(SimpleWorldEdit plugin) {
        this.plugin = plugin;
        this.setGUI = new SetGUI(plugin);
        // Registramos los eventos del GUI aquí para mantenerlo todo junto
        plugin.getServer().getPluginManager().registerEvents(setGUI, plugin);
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

        // Abrimos el menú GUI para el jugador
        setGUI.open(player);

        return true;
    }
}