package com.tuserverdor.interfacecore.placeholders;

import com.tuserverdor.interfacecore.InterfaceCore;
import com.tuservidor.ranksystem.RankSystem;
import com.tuserverdor.simpleeconomy.SimpleEconomy;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PluginPlaceholders extends PlaceholderExpansion {

    private final InterfaceCore plugin;

    public PluginPlaceholders(InterfaceCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "server";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Santtiurrax";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        // Placeholder: %server_rank%
        if (params.equalsIgnoreCase("rank")) {
            RankSystem rankSystemAPI = plugin.getRankSystem();
            if (rankSystemAPI != null) {
                String rankName = rankSystemAPI.getPlayerDataManager().getPlayerRank(player.getUniqueId());
                return rankName != null ? rankName : "Sin Rango";
            }
            return "N/A";
        }

        // Placeholder: %server_money%
        if (params.equalsIgnoreCase("money")) {
            SimpleEconomy economyAPI = (SimpleEconomy) plugin.getServer().getPluginManager().getPlugin("SimpleEconomy");
            if (economyAPI != null) {
                double balance = economyAPI.getBalance(player, player.getWorld().getName());
                return String.format("%.2f", balance);
            }
            return "0.00";
        }

        return null;
    }
}