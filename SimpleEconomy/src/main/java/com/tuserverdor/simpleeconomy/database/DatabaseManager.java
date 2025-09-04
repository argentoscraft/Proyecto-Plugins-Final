package com.tuserverdor.simpleeconomy.database;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import com.tuserverdor.simpleeconomy.utils.BalanceTopEntry;
import org.bukkit.entity.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    private final SimpleEconomy plugin;

    public DatabaseManager(SimpleEconomy plugin) {
        this.plugin = plugin;
    }

    public List<BalanceTopEntry> getTopBalances(String economyContext, int limit) {
        List<BalanceTopEntry> topBalances = new ArrayList<>();
        String sql = "SELECT username, balance FROM player_balances WHERE economy_context = ? ORDER BY balance DESC LIMIT ?";
        try (Connection conn = plugin.getDatabaseConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, economyContext);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                double balance = rs.getDouble("balance");
                topBalances.add(new BalanceTopEntry(username, balance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topBalances;
    }

    public double getMoney(Player player) {
        String economyContext = plugin.getEconomyContext(player);
        if (economyContext == null) return 0.0;
        return getMoney(player.getUniqueId(), economyContext);
    }

    public double getMoney(UUID playerUUID, String economyContext) {
        String sql = "SELECT balance FROM player_balances WHERE uuid = ? AND economy_context = ?";
        try (Connection conn = plugin.getDatabaseConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, economyContext);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public void setMoney(UUID playerUUID, String playerName, double amount, String economyContext) {
        String sql = "INSERT INTO player_balances (uuid, username, economy_context, balance) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE balance = ?, username = ?";
        try (Connection conn = plugin.getDatabaseConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, playerName);
            pstmt.setString(3, economyContext);
            pstmt.setDouble(4, amount);
            pstmt.setDouble(5, amount);
            pstmt.setString(6, playerName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMoney(UUID playerUUID, String playerName, double amount, String economyContext) {
        double currentBalance = getMoney(playerUUID, economyContext);
        setMoney(playerUUID, playerName, currentBalance + amount, economyContext);
    }

    public void removeMoney(UUID playerUUID, String playerName, double amount, String economyContext) {
        double currentBalance = getMoney(playerUUID, economyContext);
        setMoney(playerUUID, playerName, currentBalance - amount, economyContext);
    }

    public void createPlayerAccount(Player player) {
        String economyContext = plugin.getEconomyContext(player);
        if (economyContext == null) return;
        String sql = "INSERT IGNORE INTO player_balances (uuid, username, economy_context, balance) VALUES (?, ?, ?, ?)";
        try (Connection conn = plugin.getDatabaseConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player.getUniqueId().toString());
            pstmt.setString(2, player.getName());
            pstmt.setString(3, economyContext);
            pstmt.setDouble(4, plugin.getInitialBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}