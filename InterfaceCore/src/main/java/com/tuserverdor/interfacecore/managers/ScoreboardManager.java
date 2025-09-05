package com.tuserverdor.interfacecore.managers;

import com.tuserverdor.interfacecore.InterfaceCore;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class ScoreboardManager {

    private final InterfaceCore plugin;

    public ScoreboardManager(InterfaceCore plugin) {
        this.plugin = plugin;
    }

    public void updatePlayerScoreboard(Player player) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        if (playerDataManager == null) return;

        PlayerData playerData = playerDataManager.getPlayerData(player);
        if (playerData == null || !playerData.isScoreboardActive()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            return;
        }

        Scoreboard board = player.getScoreboard();
        if (board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        Objective objective = board.getObjective("info");
        if (objective == null) {
            Component title = parseText(plugin.getConfig().getString("scoreboard.title", "&e&lMI SERVIDOR"));
            objective = board.registerNewObjective("info", Criteria.DUMMY, title);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }

        Component finalTitle = parseText(PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString("scoreboard.title")));
        objective.displayName(finalTitle);

        List<String> lines = plugin.getConfig().getStringList("scoreboard.lines");
        for (int i = 0; i < lines.size(); i++) {
            // Usamos un serializador de legado para que los códigos de color funcionen bien en las líneas
            String parsedLine = PlaceholderAPI.setPlaceholders(player, lines.get(i));
            Component lineComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(parsedLine);
            // El scoreboard de Bukkit aún usa strings, así que lo volvemos a serializar
            String finalLine = LegacyComponentSerializer.legacySection().serialize(lineComponent);
            objective.getScore(finalLine).setScore(lines.size() - i);
        }
        player.setScoreboard(board);
    }
    
    private Component parseText(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}