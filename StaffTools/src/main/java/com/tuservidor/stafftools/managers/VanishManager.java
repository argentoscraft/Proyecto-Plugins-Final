package com.tuservidor.stafftools.managers;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class VanishManager {

    private final StaffTools plugin;
    private final HashSet<UUID> vanishedPlayers = new HashSet<>();

    public VanishManager(StaffTools plugin) {
        this.plugin = plugin;
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void enableVanish(Player player) {
        vanishedPlayers.add(player.getUniqueId());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("stafftools.vanish.see")) {
                onlinePlayer.hidePlayer(plugin, player);
            }
        }
        player.sendMessage(Component.text("Vanish activado. Solo el Staff puede verte.", NamedTextColor.GRAY));
    }

    public void disableVanish(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(plugin, player);
        }
        player.sendMessage(Component.text("Vanish desactivado. Ahora eres visible para todos.", NamedTextColor.GRAY));
    }

    public void handlePlayerJoin(Player player) {
        for (UUID vanishedUUID : vanishedPlayers) {
            Player vanishedPlayer = Bukkit.getPlayer(vanishedUUID);
            if (vanishedPlayer != null && !player.hasPermission("stafftools.vanish.see")) {
                player.hidePlayer(plugin, vanishedPlayer);
            }
        }
    }
}