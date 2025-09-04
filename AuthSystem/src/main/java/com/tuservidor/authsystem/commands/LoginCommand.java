package com.tuservidor.authsystem.commands;

import com.tuservidor.authsystem.AuthSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoginCommand implements CommandExecutor {
    private final AuthSystem plugin;

    public LoginCommand(AuthSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Comando solo para jugadores.");
            return true;
        }
        Player player = (Player) sender;

        if (!plugin.getAuthManager().isRegistered(player.getUniqueId())) {
            player.sendMessage(Component.text("No estás registrado. Usa /register <contraseña> <contraseña>", NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(Component.text("Uso: /login <contraseña>", NamedTextColor.RED));
            return true;
        }

        String password = args[0];
        if (plugin.getAuthManager().checkPassword(player.getUniqueId(), password)) {
            plugin.getAuthListener().setAuthenticated(player);
            player.sendMessage(Component.text("¡Has iniciado sesión con éxito!", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Contraseña incorrecta.", NamedTextColor.RED));
        }

        return true;
    }
}