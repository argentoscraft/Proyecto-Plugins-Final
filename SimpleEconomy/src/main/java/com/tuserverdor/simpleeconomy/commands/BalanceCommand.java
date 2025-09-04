package com.tuserverdor.simpleeconomy.commands;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    private final SimpleEconomy plugin;
    public BalanceCommand(SimpleEconomy plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Este comando solo puede ser ejecutado por un jugador."); return true; }
        Player player = (Player) sender;
        String economyContext = plugin.getEconomyContext(player);
        if (economyContext == null) { player.sendMessage(ChatColor.RED + "No te encuentras en un mundo con economía activa."); return true; }
        double balance = plugin.getDatabaseManager().getMoney(player);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTu saldo en &e" + economyContext + "&a es: &2$&f" + String.format("%,.2f", balance)));
        return true;
    }
}