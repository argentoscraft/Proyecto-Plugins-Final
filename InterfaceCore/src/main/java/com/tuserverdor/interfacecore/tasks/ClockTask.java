package com.tuserverdor.interfacecore.tasks;

import com.tuserverdor.interfacecore.InterfaceCore;
import com.tuserverdor.interfacecore.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ClockTask extends BukkitRunnable {

    private final InterfaceCore plugin;
    private final PlayerDataManager playerDataManager;

    public ClockTask(InterfaceCore plugin) {
        this.plugin = plugin;
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @Override
    public void run() {
        // Obtenemos la lista de jugadores que tienen el reloj activado
        List<Player> playersToUpdate = playerDataManager.getPlayersWithClockEnabled();

        // Aquí iría la lógica para actualizarles algo (ej. un item en la mano, un mensaje, etc.)
        // Por ahora, solo lo dejamos preparado.
        for (Player player : playersToUpdate) {
            // Ejemplo: player.sendMessage("Tick-tock!");
        }
    }
}