package com.tuservidor.ranksystem.listeners;

import com.tuservidor.ranksystem.RankSystem;
import com.tuservidor.ranksystem.objects.Rank;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ChatListener implements Listener {

    private final RankSystem plugin;

    public ChatListener(RankSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();

        String rankName = plugin.getPlayerDataManager().getPlayerRank(player.getUniqueId());
        Rank rank = plugin.getRankManager().getRank(rankName);

        if (rank == null) {
            return;
        }

        // Usamos el serializador de componentes para interpretar los códigos de color '&'
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

        Component prefix = serializer.deserialize(rank.getPrefix() + " ");
        Component playerName = Component.text(player.getName());
        Component separator = Component.text(": ");
        Component message = event.originalMessage(); // Obtenemos el mensaje original

        // Combinamos todo y le aplicamos el color del rango al separador y al mensaje
        Component finalFormat = prefix
                .append(playerName)
                .append(separator.color(serializer.deserialize(rank.getChatColor()).color()))
                .append(message.color(serializer.deserialize(rank.getChatColor()).color()));

        // Usamos el renderizador del evento, que es la forma moderna de cambiar el formato
        event.renderer((source, sourceDisplayName, msg, viewer) -> finalFormat);
    }
}