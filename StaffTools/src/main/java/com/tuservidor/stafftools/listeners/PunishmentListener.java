package com.tuservidor.stafftools.listeners;

import com.tuservidor.stafftools.StaffTools;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PunishmentListener implements Listener {

    private final StaffTools plugin;

    public PunishmentListener(StaffTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();
        if (plugin.getPunishmentManager().isBanned(playerUUID)) {
            String reason = plugin.getPunishmentManager().getBanReason(playerUUID);
            long endTime = plugin.getPunishmentManager().getBanEndTime(playerUUID);
            Component kickMessage;
            if (endTime == -1) {
                kickMessage = Component.text("Has sido baneado permanentemente del servidor.\n\n", NamedTextColor.RED)
                    .append(Component.text("Razón: ", NamedTextColor.GRAY))
                    .append(Component.text(reason, NamedTextColor.WHITE));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm:ss");
                String formattedDate = sdf.format(new Date(endTime));
                kickMessage = Component.text("Estás baneado temporalmente del servidor.\n\n", NamedTextColor.RED)
                    .append(Component.text("Razón: ", NamedTextColor.GRAY))
                    .append(Component.text(reason, NamedTextColor.WHITE))
                    .append(Component.text("\nTu sanción expira el: ", NamedTextColor.GRAY))
                    .append(Component.text(formattedDate, NamedTextColor.YELLOW));
            }
            event.kickMessage(kickMessage);
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
    }

    // --- ¡NUEVO EVENTO PARA BLOQUEAR EL CHAT! ---
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.getPunishmentManager().isMuted(player.getUniqueId())) {
            event.setCancelled(true);
            
            String reason = plugin.getPunishmentManager().getMuteReason(player.getUniqueId());
            long endTime = plugin.getPunishmentManager().getMuteEndTime(player.getUniqueId());

            if (endTime == -1) {
                player.sendMessage(Component.text("Estás silenciado permanentemente. Razón: " + reason, NamedTextColor.RED));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String formattedDate = sdf.format(new Date(endTime));
                player.sendMessage(Component.text("Estás silenciado hasta el " + formattedDate + ". Razón: " + reason, NamedTextColor.RED));
            }
        }
    }
}