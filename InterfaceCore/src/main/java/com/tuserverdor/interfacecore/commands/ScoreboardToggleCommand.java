package com.tuserverdor.interfacecore.commands;

import com.tuserverdor.interfacecore.InterfaceCore;
import com.tuserverdor.interfacecore.managers.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ScoreboardToggleCommand implements CommandExecutor {

    private final PlayerDataManager playerDataManager;

    public ScoreboardToggleCommand(InterfaceCore plugin) {
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        
        // --- LÓGICA CORREGIDA ---
        // Primero cambiamos el estado
        playerDataManager.toggleScoreboard(player);

        // Luego, consultamos el nuevo estado para enviar el mensaje correcto
        if (playerDataManager.getPlayerData(player).isScoreboardActive()) {
            player.sendMessage("§aScoreboard activado.");
        } else {
            player.sendMessage("§cScoreboard desactivado.");
        }

        return true;
    }
}