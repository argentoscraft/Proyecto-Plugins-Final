package com.tuservidor.stafftools.commands;

import com.tuservidor.stafftools.StaffTools;
import com.tuservidor.stafftools.utils.TimeUtils; // Crearemos este archivo ahora
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BanCommand implements CommandExecutor {

    private final StaffTools plugin;

    public BanCommand(StaffTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Component.text("Uso: /ban <jugador> [tiempo] [razón]", NamedTextColor.RED));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(Component.text("El jugador '" + args[0] + "' nunca ha entrado al servidor.", NamedTextColor.RED));
            return true;
        }

        long duration = -1; // Por defecto, permanente
        String reason = "No especificada.";
        
        if (args.length > 1) {
            duration = TimeUtils.parseTime(args[1]);
            if (duration > 0) { // Si el segundo argumento fue un tiempo válido
                reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            } else { // Si no fue un tiempo, todo es parte de la razón
                duration = -1; // Lo reseteamos a permanente
                reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }
        }

        plugin.getPunishmentManager().banPlayer(target.getUniqueId(), duration, reason);

        // Si el jugador está online, lo kickeamos
        if (target.isOnline()) {
            Player onlineTarget = (Player) target;
            onlineTarget.kick(Component.text("Has sido baneado. Razón: " + reason, NamedTextColor.RED));
        }

        sender.sendMessage(Component.text("El jugador " + target.getName() + " ha sido baneado.", NamedTextColor.GREEN));
        // Se podría añadir un anuncio global del baneo aquí
        
        return true;
    }
}