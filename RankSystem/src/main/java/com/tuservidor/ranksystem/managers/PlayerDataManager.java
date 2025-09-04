package com.tuservidor.ranksystem.managers;

import com.tuservidor.ranksystem.RankSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDataManager {

    private final RankSystem plugin;
    private File playerFile;
    private FileConfiguration playerConfig;

    public PlayerDataManager(RankSystem plugin) {
        this.plugin = plugin;
        setupPlayerFile();
    }

    private void setupPlayerFile() {
        playerFile = new File(plugin.getDataFolder(), "players.yml");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear el archivo players.yml!");
            }
        }
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
    }

    public void setPlayerRank(UUID playerUUID, String rankName) {
        playerConfig.set(playerUUID.toString() + ".rank", rankName.toLowerCase());
        saveAsync();
    }

    public String getPlayerRank(UUID playerUUID) {
        return playerConfig.getString(playerUUID.toString() + ".rank", plugin.getRankManager().getDefaultRankName());
    }

    // --- ¡NUEVOS MÉTODOS PARA COOLDOWNS! ---

    /**
     * Guarda el momento actual como la última vez que el jugador reclamó un kit.
     */
    public void setKitCooldown(UUID playerUUID, String kitName) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        playerConfig.set(playerUUID.toString() + ".kit-cooldowns." + kitName.toLowerCase(), currentTimeSeconds);
        saveAsync();
    }

    /**
     * Obtiene la última vez (en segundos) que un jugador reclamó un kit.
     * @return El tiempo en segundos, o 0 si nunca lo ha reclamado.
     */
    public long getKitCooldown(UUID playerUUID, String kitName) {
        return playerConfig.getLong(playerUUID.toString() + ".kit-cooldowns." + kitName.toLowerCase(), 0);
    }

    private void saveAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo guardar la configuración en players.yml!");
            }
        });
    }
}