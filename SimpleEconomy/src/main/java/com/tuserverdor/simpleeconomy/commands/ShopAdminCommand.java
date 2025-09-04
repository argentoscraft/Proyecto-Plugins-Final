package com.tuserverdor.simpleeconomy.commands;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopAdminCommand implements CommandExecutor {
    private final SimpleEconomy plugin;
    public ShopAdminCommand(SimpleEconomy plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simpleeconomy.shopadmin")) { sender.sendMessage(ChatColor.RED + "No tienes permiso."); return true; }
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) { plugin.getShopManager().reloadShops(); sender.sendMessage(ChatColor.GREEN + "¡Tiendas recargadas desde shop.yml!"); return true; }
        sender.sendMessage(ChatColor.YELLOW + "Uso: /shopadmin reload");
        return true;
    }
}