package com.tuserverdor.interfacecore.managers;

import com.tuserverdor.interfacecore.InterfaceCore;
import com.tuservidor.ranksystem.RankSystem;
import com.tuservidor.ranksystem.objects.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI; // Importamos PlaceholderAPI

public class TablistManager {

    private final InterfaceCore plugin;

    public TablistManager(InterfaceCore plugin) {
        this.plugin = plugin;
    }

    public void setTablist(Player player) {
        // Obtenemos las listas de texto desde la config.yml
        String headerText = String.join("\n", plugin.getConfig().getStringList("tablist.header"));
        String footerText = String.join("\n", plugin.getConfig().getStringList("tablist.footer"));
        
        // ¡Paso Clave! Usamos PlaceholderAPI para reemplazar TODOS los placeholders
        headerText = PlaceholderAPI.setPlaceholders(player, headerText);
        footerText = PlaceholderAPI.setPlaceholders(player, footerText);

        // Convertimos el texto con códigos '&' a Componentes de texto visibles en el juego
        Component header = LegacyComponentSerializer.legacyAmpersand().deserialize(headerText);
        Component footer = LegacyComponentSerializer.legacyAmpersand().deserialize(footerText);

        player.sendPlayerListHeaderAndFooter(header, footer);

        // --- Lógica para el nombre del jugador en la lista ---
        RankSystem rankSystemAPI = plugin.getRankSystem();
        if (rankSystemAPI == null) {
            player.playerListName(Component.text(player.getName()));
            return;
        }

        String rankName = rankSystemAPI.getPlayerDataManager().getPlayerRank(player.getUniqueId());
        Rank rank = rankSystemAPI.getRankManager().getRank(rankName);

        if (rank == null) {
            player.playerListName(Component.text(player.getName()));
            return;
        }

        // Creamos el formato final y lo convertimos a Component
        String playerListFormat = rank.getPrefix() + " " + player.getName();
        Component finalListName = LegacyComponentSerializer.legacyAmpersand().deserialize(playerListFormat);
        player.playerListName(finalListName);
    }
}