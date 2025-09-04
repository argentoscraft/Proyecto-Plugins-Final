package com.tuservidor.stafftools.commands;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UnbanCommand implements CommandExecutor {

    private final StaffTools plugin;

    public UnbanCommand(StaffTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Uso: /unban <jugador>", NamedTextColor.RED));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
             sender.sendMessage(Component.text("El jugador '" + args[0] + "' nunca ha entrado al servidor.", NamedTextColor.RED));
            return true;
        }

        if (plugin.getPunishmentManager().unbanPlayer(target.getUniqueId())) {
            sender.sendMessage(Component.text("El jugador " + target.getName() + " ha sido desbaneado.", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("El jugador " + target.getName() + " no estaba baneado.", NamedTextColor.RED));
        }
        
        return true;
    }
}