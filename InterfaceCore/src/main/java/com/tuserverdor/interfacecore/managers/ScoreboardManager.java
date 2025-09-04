package com.tuserverdor.interfacecore.managers;

import com.tuserverdor.interfacecore.InterfaceCore;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.List;

public class ScoreboardManager {

    private final InterfaceCore plugin;

    public ScoreboardManager(InterfaceCore plugin) {
        this.plugin = plugin;
    }

    public void setScoreboard(Player player) {
        updatePlayerScoreboard(player);
    }

    public void updatePlayerScoreboard(Player player) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        PlayerData playerData = playerDataManager.getPlayerData(player);

        if (playerData == null || !playerData.isScoreboardActive()) {
            // --- MENSAJE ESPÍA 3 ---
            plugin.getLogger().info("Scoreboard para " + player.getName() + " está desactivado o no tiene datos. Limpiando su scoreboard.");
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            return;
        }

        // --- MENSAJE ESPÍA 4 ---
        plugin.getLogger().info("Actualizando scoreboard para " + player.getName() + "...");

        Scoreboard board = player.getScoreboard();
        if (board == null || board.getObjectives().isEmpty()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        Objective objective = board.getObjective("InterfaceCore");
        if (objective == null) {
            Component title = Component.text(format(PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("scoreboard.title"))));
            objective = board.registerNewObjective("InterfaceCore", "dummy", title);
        }

        Component finalTitle = Component.text(format(PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("scoreboard.title"))));
        objective.displayName(finalTitle);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }

        List<String> lines = plugin.getConfig().getStringList("scoreboard.lines");
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            objective.getScore(format(PlaceholderAPI.setPlaceholders(player, line))).setScore(lines.size() - i);
        }

        player.setScoreboard(board);
    }

    private String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}