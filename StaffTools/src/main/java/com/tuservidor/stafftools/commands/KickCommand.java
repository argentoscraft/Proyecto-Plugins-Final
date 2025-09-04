package com.tuservidor.stafftools.commands;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class KickCommand implements CommandExecutor {

    private final StaffTools plugin;

    public KickCommand(StaffTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Component.text("Uso: /kick <jugador> [razón]", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("El jugador '" + args[0] + "' no está en línea.", NamedTextColor.RED));
            return true;
        }

        String reason = "No especificada.";
        if (args.length > 1) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        // Creamos el mensaje de expulsión
        Component kickMessage = Component.text("Has sido expulsado del servidor.\n\n", NamedTextColor.RED)
            .append(Component.text("Razón: ", NamedTextColor.GRAY))
            .append(Component.text(reason, NamedTextColor.WHITE));

        // Expulsamos al jugador
        target.kick(kickMessage);

        // Anunciamos la expulsión a los jugadores con permiso (opcional)
        Component kickAnnouncement = Component.text(target.getName() + " ha sido expulsado por " + sender.getName() + ". Razón: " + reason, NamedTextColor.YELLOW);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("stafftools.kick.notify")) {
                onlinePlayer.sendMessage(kickAnnouncement);
            }
        }
        
        return true;
    }
}