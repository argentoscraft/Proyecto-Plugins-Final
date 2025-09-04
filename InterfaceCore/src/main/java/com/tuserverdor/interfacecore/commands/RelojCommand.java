package com.tuserverdor.interfacecore.commands;

import com.tuserverdor.interfacecore.InterfaceCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RelojCommand implements CommandExecutor {
    private final InterfaceCore plugin;
    public RelojCommand(InterfaceCore plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Comando solo para jugadores.");
            return true;
        }
        Player player = (Player) sender;
        plugin.getPlayerDataManager().toggleClock(player);
        boolean newState = plugin.getPlayerDataManager().hasClockEnabled(player);
        player.sendMessage(newState ? ChatColor.GREEN + "Reloj activado." : ChatColor.RED + "Reloj desactivado.");
        return true;
    }
}