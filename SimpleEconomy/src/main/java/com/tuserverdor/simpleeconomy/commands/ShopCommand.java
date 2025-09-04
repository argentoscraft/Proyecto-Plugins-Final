package com.tuserverdor.simpleeconomy.commands;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import com.tuserverdor.simpleeconomy.shop.ShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    private final SimpleEconomy plugin;
    public ShopCommand(SimpleEconomy plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Solo los jugadores pueden abrir la tienda."); return true; }
        Player player = (Player) sender;
        new ShopGUI(plugin).openShop(player);
        return true;
    }
}