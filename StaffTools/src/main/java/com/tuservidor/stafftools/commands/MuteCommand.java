package com.tuservidor.stafftools.commands;

import com.tuservidor.stafftools.StaffTools;
import com.tuservidor.stafftools.utils.TimeUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MuteCommand implements CommandExecutor {

    private final StaffTools plugin;

    public MuteCommand(StaffTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Component.text("Uso: /mute <jugador> [tiempo] [razón]", NamedTextColor.RED));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(Component.text("El jugador '" + args[0] + "' nunca ha entrado al servidor.", NamedTextColor.RED));
            return true;
        }

        long duration = -1;
        String reason = "No especificada.";
        
        if (args.length > 1) {
            duration = TimeUtils.parseTime(args[1]);
            if (duration > 0) {
                if (args.length > 2) {
                    reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                }
            } else {
                duration = -1;
                reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }
        }

        plugin.getPunishmentManager().mutePlayer(target.getUniqueId(), duration, reason);
        sender.sendMessage(Component.text("El jugador " + target.getName() + " ha sido silenciado.", NamedTextColor.GREEN));
        
        return true;
    }
}