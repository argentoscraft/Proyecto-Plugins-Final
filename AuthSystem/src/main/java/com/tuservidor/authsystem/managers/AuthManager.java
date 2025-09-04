package com.tuservidor.authsystem.managers;

import com.tuservidor.authsystem.AuthSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mindrot.jbcrypt.BCrypt; // Importamos la librería de encriptación

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AuthManager {

    private final AuthSystem plugin;
    private File authFile;
    private FileConfiguration authConfig;

    public AuthManager(AuthSystem plugin) {
        this.plugin = plugin;
        setupAuthFile();
    }

    private void setupAuthFile() {
        authFile = new File(plugin.getDataFolder(), "auth-data.yml");
        if (!authFile.exists()) {
            try {
                authFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear el archivo auth-data.yml!");
            }
        }
        authConfig = YamlConfiguration.loadConfiguration(authFile);
    }

    public boolean isRegistered(UUID playerUUID) {
        return authConfig.contains(playerUUID.toString());
    }

    public void registerPlayer(UUID playerUUID, String password) {
        // Encriptamos la contraseña antes de guardarla
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        authConfig.set(playerUUID.toString() + ".password", hashedPassword);
        save();
    }

    public boolean checkPassword(UUID playerUUID, String password) {
        String hashedPassword = authConfig.getString(playerUUID.toString() + ".password");
        if (hashedPassword == null) {
            return false;
        }
        // Comparamos la contraseña introducida con la versión encriptada que tenemos guardada
        return BCrypt.checkpw(password, hashedPassword);
    }

    public void save() {
        try {
            authConfig.save(authFile);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar la configuración en auth-data.yml!");
        }
    }
}