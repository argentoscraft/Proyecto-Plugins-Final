package com.tuservidor.authsystem.commands;

import com.tuservidor.authsystem.AuthSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegisterCommand implements CommandExecutor {
    private final AuthSystem plugin;

    public RegisterCommand(AuthSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Comando solo para jugadores.");
            return true;
        }
        Player player = (Player) sender;

        if (plugin.getAuthManager().isRegistered(player.getUniqueId())) {
            player.sendMessage(Component.text("Ya estás registrado. Usa /login <contraseña>", NamedTextColor.RED));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(Component.text("Uso: /register <contraseña> <contraseña>", NamedTextColor.RED));
            return true;
        }

        String pass1 = args[0];
        String pass2 = args[1];

        // --- ¡NUEVA COMPROBACIÓN! ---
        if (pass1.equalsIgnoreCase(player.getName())) {
            player.sendMessage(Component.text("La contraseña no puede ser igual a tu nombre de usuario.", NamedTextColor.RED));
            return true;
        }

        if (!pass1.equals(pass2)) {
            player.sendMessage(Component.text("Las contraseñas no coinciden.", NamedTextColor.RED));
            return true;
        }

        if (pass1.length() < 6) {
            player.sendMessage(Component.text("La contraseña debe tener al menos 6 caracteres.", NamedTextColor.RED));
            return true;
        }

        plugin.getAuthManager().registerPlayer(player.getUniqueId(), pass1);
        plugin.getAuthListener().setAuthenticated(player);
        player.sendMessage(Component.text("¡Te has registrado con éxito!", NamedTextColor.GREEN));

        return true;
    }
}