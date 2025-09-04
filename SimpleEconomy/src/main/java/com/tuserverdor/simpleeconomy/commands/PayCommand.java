package com.tuserverdor.simpleeconomy.commands;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    private final SimpleEconomy plugin;
    public PayCommand(SimpleEconomy plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage(ChatColor.RED + "Este comando solo puede ser usado por un jugador."); return true; }
        Player player = (Player) sender;
        String economyContext = plugin.getEconomyContext(player);
        if (economyContext == null) { player.sendMessage(ChatColor.RED + "Los comandos de economía están desactivados en este mundo."); return true; }
        if (args.length != 2) { player.sendMessage(ChatColor.RED + "Uso incorrecto. Usa /pay <jugador> <cantidad>"); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { player.sendMessage(ChatColor.RED + "El jugador " + args[0] + " no está conectado."); return true; }
        if (target.equals(player)) { player.sendMessage(ChatColor.RED + "No puedes pagarte a ti mismo."); return true; }
        String targetEconomyContext = plugin.getEconomyContext(target);
        if (!economyContext.equals(targetEconomyContext)) { player.sendMessage(ChatColor.RED + "No puedes pagar a jugadores que están en una economía diferente."); return true; }
        double amount;
        try { amount = Double.parseDouble(args[1]); } catch (NumberFormatException e) { player.sendMessage(ChatColor.RED + "La cantidad debe ser un número."); return true; }
        if (amount <= 0) { player.sendMessage(ChatColor.RED + "La cantidad debe ser mayor que cero."); return true; }
        double playerBalance = plugin.getDatabaseManager().getMoney(player);
        if (playerBalance < amount) { player.sendMessage(ChatColor.RED + "No tienes suficiente dinero."); return true; }
        plugin.getDatabaseManager().removeMoney(player.getUniqueId(), player.getName(), amount, economyContext);
        plugin.getDatabaseManager().addMoney(target.getUniqueId(), target.getName(), amount, economyContext);
        player.sendMessage(ChatColor.GREEN + "Has pagado $" + amount + " a " + target.getName() + ".");
        target.sendMessage(ChatColor.GREEN + "Has recibido $" + amount + " de " + player.getName() + ".");
        return true;
    }
}