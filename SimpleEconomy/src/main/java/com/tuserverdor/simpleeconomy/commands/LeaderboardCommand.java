package com.tuserverdor.simpleeconomy.commands;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import com.tuserverdor.simpleeconomy.utils.BalanceTopEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class LeaderboardCommand implements CommandExecutor {
    private final SimpleEconomy plugin;

    public LeaderboardCommand(SimpleEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }
        Player player = (Player) sender;
        String economyContext = plugin.getEconomyContext(player);
        if (economyContext == null) {
            player.sendMessage(ChatColor.RED + "No hay un leaderboard en este mundo.");
            return true;
        }

        List<BalanceTopEntry> topBalances = plugin.getDatabaseManager().getTopBalances(economyContext, 10);

        if (topBalances.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Aún no hay datos en el ranking de esta economía.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "--- Top 10 Ricos (" + economyContext + ") ---");
        int rank = 1;
        for (BalanceTopEntry entry : topBalances) {
            player.sendMessage(ChatColor.YELLOW + "" + rank + ". " + ChatColor.WHITE + entry.getPlayerName() + " - " + ChatColor.GREEN + "$" + String.format("%,.2f", entry.getBalance()));
            rank++;
        }
        player.sendMessage(ChatColor.GOLD + "--------------------------");
        
        return true;
    }
}