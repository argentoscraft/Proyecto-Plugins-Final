package com.tuservidor.stafftools.managers;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class XRayAlertManager {

    private final StaffTools plugin;
    // Mapa que guarda los tiempos en los que un jugador encontró minerales valiosos
    private final HashMap<UUID, List<Long>> oreLog = new HashMap<>();

    public XRayAlertManager(StaffTools plugin) {
        this.plugin = plugin;
    }

    public void checkPlayerOreBreak(Player player, Material blockType) {
        // Obtenemos la lista de minerales a vigilar desde el config.yml
        List<String> trackedOres = plugin.getConfig().getStringList("xray-alerter.tracked-ores");
        if (!trackedOres.contains(blockType.name())) {
            return; // No es un mineral que nos interese
        }

        long currentTime = System.currentTimeMillis();
        long timeFrameMillis = plugin.getConfig().getLong("xray-alerter.time-frame", 5) * 60 * 1000;
        int alertThreshold = plugin.getConfig().getInt("xray-alerter.alert-threshold", 16);
        
        // Obtenemos el registro del jugador o creamos uno nuevo
        List<Long> playerLog = oreLog.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());

        // Añadimos el nuevo hallazgo
        playerLog.add(currentTime);

        // Limpiamos los registros que son más viejos que el tiempo definido (ej. 5 minutos)
        playerLog.removeIf(timestamp -> (currentTime - timestamp) > timeFrameMillis);

        // Si el jugador ha encontrado más minerales que el umbral, lanzamos una alerta
        if (playerLog.size() >= alertThreshold) {
            sendAlert(player, playerLog.size(), blockType.name());
            // Limpiamos su registro para no spamear alertas por el mismo grupo de minerales
            playerLog.clear();
        }
    }

    private void sendAlert(Player suspect, int amount, String ore) {
        Component alertMessage = Component.text("[AlertaXRay] ", NamedTextColor.RED)
            .append(Component.text(suspect.getName(), NamedTextColor.YELLOW))
            .append(Component.text(" ha encontrado " + amount + " de " + ore + " en poco tiempo.", NamedTextColor.GRAY));

        // Enviamos el mensaje a todos los jugadores con el permiso para ver las alertas
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("stafftools.alerts.xray")) {
                onlinePlayer.sendMessage(alertMessage);
            }
        }
        // También lo mostramos en la consola del servidor
        plugin.getLogger().info("[AlertaXRay] " + suspect.getName() + " ha encontrado " + amount + " de " + ore + " en poco tiempo.");
    }
}