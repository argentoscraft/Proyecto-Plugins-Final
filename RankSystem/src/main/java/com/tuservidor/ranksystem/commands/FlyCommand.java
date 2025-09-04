package com.tuservidor.ranksystem.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Comando solo para jugadores.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("ranksystem.command.fly")) {
            player.sendMessage(Component.text("No tienes permiso para usar este comando.", NamedTextColor.RED));
            return true;
        }

        // Alternamos el estado de vuelo
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.sendMessage(Component.text("Modo de vuelo desactivado.", NamedTextColor.YELLOW));
        } else {
            player.setAllowFlight(true);
            player.sendMessage(Component.text("Modo de vuelo activado.", NamedTextColor.GREEN));
        }

        return true;
    }
}