package com.tuservidor.serverguard.tasks;

import com.tuservidor.serverguard.ServerGuard;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.time.ZoneId;

public class TimeSyncTask extends BukkitRunnable {

    private final ServerGuard plugin;
    private final int offsetHours;
    private boolean firstRun = true;

    public TimeSyncTask(ServerGuard plugin) {
        this.plugin = plugin;
        // Leemos el nuevo ajuste manual desde el config.yml
        this.offsetHours = plugin.getConfig().getInt("global-rules.real-world-time-sync.timezone-offset", 0);
    }

    @Override
    public void run() {
        // Obtenemos la hora universal (UTC), que es una referencia estable
        LocalTime utcTime = LocalTime.now(ZoneId.of("UTC"));

        // Aplicamos nuestro ajuste manual para obtener la hora local
        LocalTime localTime = utcTime.plusHours(this.offsetHours);

        int hour = localTime.getHour();
        int minute = localTime.getMinute();

        // Usamos la fórmula que ya sabemos que es correcta
        long totalTicks = 18000;
        totalTicks += hour * 1000;
        totalTicks += (minute / 60.0) * 1000;
        totalTicks %= 24000;
        
        // Aplicamos la hora y la gamerule a todos los mundos
        for (World world : Bukkit.getWorlds()) {
            if (firstRun || !world.getGameRuleValue(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE).equals(false)) {
                world.setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
            }
            world.setTime(totalTicks);
        }
        firstRun = false;
    }
}