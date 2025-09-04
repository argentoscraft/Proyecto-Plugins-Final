package com.tuservidor.stafftools.commands;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffCommand implements CommandExecutor {

    private final StaffTools plugin;

    public StaffCommand(StaffTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Comando solo para jugadores.");
            return true;
        }
        Player player = (Player) sender;

        if (plugin.getStaffModeManager().isInStaffMode(player)) {
            plugin.getStaffModeManager().disableStaffMode(player);
            player.sendMessage(Component.text("Modo Staff desactivado. Has vuelto a tu estado normal.", NamedTextColor.YELLOW));
        } else {
            plugin.getStaffModeManager().enableStaffMode(player);
            player.sendMessage(Component.text("Modo Staff activado.", NamedTextColor.AQUA));
        }
        return true;
    }
}