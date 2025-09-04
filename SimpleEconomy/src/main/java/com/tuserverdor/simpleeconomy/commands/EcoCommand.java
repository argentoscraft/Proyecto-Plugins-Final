package com.tuserverdor.simpleeconomy.commands;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcoCommand implements CommandExecutor {
    private final SimpleEconomy plugin;
    public EcoCommand(SimpleEconomy plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simpleeconomy.eco")) { sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando."); return true; }
        if (args.length < 4) { sender.sendMessage(ChatColor.RED + "Uso: /eco <give|set|take> <jugador> <cantidad> <economia>"); return true; }
        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(ChatColor.RED + "El jugador " + args[1] + " no está en línea."); return true; }
        double amount;
        try { amount = Double.parseDouble(args[2]);
            if (amount < 0) { sender.sendMessage(ChatColor.RED + "La cantidad no puede ser negativa."); return true; }
        } catch (NumberFormatException e) { sender.sendMessage(ChatColor.RED + "La cantidad debe ser un número válido."); return true; }
        String economyContext = args[3].toLowerCase();
        if (!plugin.getEconomyMappings().contains(economyContext)) { sender.sendMessage(ChatColor.RED + "La economía '" + economyContext + "' no es válida."); return true; }
        switch (action) {
            case "give":
                plugin.getDatabaseManager().addMoney(target.getUniqueId(), target.getName(), amount, economyContext);
                sender.sendMessage(ChatColor.GREEN + "Has añadido $" + amount + " a " + target.getName() + " en '" + economyContext + "'.");
                target.sendMessage(ChatColor.GREEN + "Has recibido $" + amount + ".");
                break;
            case "set":
                plugin.getDatabaseManager().setMoney(target.getUniqueId(), target.getName(), amount, economyContext);
                sender.sendMessage(ChatColor.GREEN + "Has establecido el saldo de " + target.getName() + " a $" + amount + " en '" + economyContext + "'.");
                target.sendMessage(ChatColor.GREEN + "Tu saldo ha sido establecido a $" + amount + ".");
                break;
            case "take": case "remove":
                plugin.getDatabaseManager().removeMoney(target.getUniqueId(), target.getName(), amount, economyContext);
                sender.sendMessage(ChatColor.GREEN + "Has quitado $" + amount + " de " + target.getName() + " en '" + economyContext + "'.");
                target.sendMessage(ChatColor.GREEN + "Te han quitado $" + amount + ".");
                break;
            default: sender.sendMessage(ChatColor.RED + "Acción no válida. Usa give, set, o take."); break;
        }
        return true;
    }
}