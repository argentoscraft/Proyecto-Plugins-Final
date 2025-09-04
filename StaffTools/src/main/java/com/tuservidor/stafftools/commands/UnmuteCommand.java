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

public class UnmuteCommand implements CommandExecutor {
    private final StaffTools plugin;

    public UnmuteCommand(StaffTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Uso: /unmute <jugador>", NamedTextColor.RED));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
             sender.sendMessage(Component.text("El jugador '" + args[0] + "' nunca ha entrado al servidor.", NamedTextColor.RED));
            return true;
        }

        if (plugin.getPunishmentManager().unmutePlayer(target.getUniqueId())) {
            sender.sendMessage(Component.text("Se le ha quitado el silencio a " + target.getName() + ".", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text(target.getName() + " no estaba silenciado.", NamedTextColor.RED));
        }
        
        return true;
    }
}