package com.tuservidor.stafftools.managers;

import com.tuservidor.stafftools.StaffTools;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PunishmentManager {

    private final StaffTools plugin;
    private File punishmentFile;
    private FileConfiguration punishmentConfig;

    public PunishmentManager(StaffTools plugin) {
        this.plugin = plugin;
        setupPunishmentFile();
    }

    private void setupPunishmentFile() {
        punishmentFile = new File(plugin.getDataFolder(), "punishments.yml");
        if (!punishmentFile.exists()) {
            try {
                punishmentFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear el archivo punishments.yml!");
            }
        }
        punishmentConfig = YamlConfiguration.loadConfiguration(punishmentFile);
    }

    public void save() {
        try {
            punishmentConfig.save(punishmentFile);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar la configuración en punishments.yml!");
        }
    }

    // --- Lógica de Ban (sin cambios) ---
    public boolean isBanned(UUID playerUUID) {
        String path = "bans." + playerUUID.toString();
        if (!punishmentConfig.contains(path)) return false;
        long endTime = punishmentConfig.getLong(path + ".ends_at");
        return endTime == -1 || System.currentTimeMillis() < endTime;
    }
    public String getBanReason(UUID playerUUID) { return punishmentConfig.getString("bans." + playerUUID.toString() + ".reason", "No especificada."); }
    public long getBanEndTime(UUID playerUUID) { return punishmentConfig.getLong("bans." + playerUUID.toString() + ".ends_at"); }
    public void banPlayer(UUID playerUUID, long durationMillis, String reason) {
        String path = "bans." + playerUUID.toString();
        punishmentConfig.set(path + ".reason", reason);
        punishmentConfig.set(path + ".banned_at", System.currentTimeMillis());
        long endTime = (durationMillis == -1) ? -1 : System.currentTimeMillis() + durationMillis;
        punishmentConfig.set(path + ".ends_at", endTime);
        save();
    }
    public boolean unbanPlayer(UUID playerUUID) {
        String path = "bans." + playerUUID.toString();
        if (punishmentConfig.contains(path)) {
            punishmentConfig.set(path, null);
            save();
            return true;
        }
        return false;
    }

    // --- ¡NUEVA LÓGICA DE MUTE! ---
    public boolean isMuted(UUID playerUUID) {
        String path = "mutes." + playerUUID.toString();
        if (!punishmentConfig.contains(path)) {
            return false;
        }
        long endTime = punishmentConfig.getLong(path + ".ends_at");
        return endTime == -1 || System.currentTimeMillis() < endTime;
    }
    public String getMuteReason(UUID playerUUID) { return punishmentConfig.getString("mutes." + playerUUID.toString() + ".reason", "No especificada."); }
    public long getMuteEndTime(UUID playerUUID) { return punishmentConfig.getLong("mutes." + playerUUID.toString() + ".ends_at"); }
    public void mutePlayer(UUID playerUUID, long durationMillis, String reason) {
        String path = "mutes." + playerUUID.toString();
        punishmentConfig.set(path + ".reason", reason);
        punishmentConfig.set(path + ".muted_at", System.currentTimeMillis());
        long endTime = (durationMillis == -1) ? -1 : System.currentTimeMillis() + durationMillis;
        punishmentConfig.set(path + ".ends_at", endTime);
        save();
    }
    public boolean unmutePlayer(UUID playerUUID) {
        String path = "mutes." + playerUUID.toString();
        if (punishmentConfig.contains(path)) {
            punishmentConfig.set(path, null);
            save();
            return true;
        }
        return false;
    }
}