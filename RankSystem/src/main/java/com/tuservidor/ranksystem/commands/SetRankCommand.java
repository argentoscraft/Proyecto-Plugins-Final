package com.tuservidor.ranksystem.commands;

import com.tuservidor.ranksystem.RankSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetRankCommand implements CommandExecutor {

    private final RankSystem plugin;

    public SetRankCommand(RankSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Component.text("Uso correcto: /setrank <jugador> <rango>", NamedTextColor.RED));
            return true;
        }

        String playerName = args[0];
        String rankName = args[1];

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(Component.text("El jugador '" + playerName + "' no está en línea.", NamedTextColor.RED));
            return true;
        }

        if (plugin.getRankManager().getRank(rankName) == null) {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no existe.", NamedTextColor.RED));
            return true;
        }

        plugin.getPlayerDataManager().setPlayerRank(target.getUniqueId(), rankName);
        plugin.getPlayerJoinListener().applyPermissions(target);

        plugin.updatePlayerTablist(target); // <-- LÍNEA NUEVA

        sender.sendMessage(Component.text("Has asignado el rango '" + rankName + "' a " + target.getName() + ".", NamedTextColor.GREEN));
        target.sendMessage(Component.text("¡Has recibido el rango '" + rankName + "'!", NamedTextColor.GOLD));

        return true;
    }
}