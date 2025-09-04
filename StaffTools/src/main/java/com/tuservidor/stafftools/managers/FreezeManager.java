package com.tuservidor.stafftools.managers;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FreezeManager {

    private final StaffTools plugin;
    private final HashSet<UUID> frozenPlayers = new HashSet<>();
    private File freezeFile;
    private FileConfiguration freezeConfig;

    public FreezeManager(StaffTools plugin) {
        this.plugin = plugin;
        setupFreezeFile();
        loadFrozenPlayers();
    }

    private void setupFreezeFile() {
        freezeFile = new File(plugin.getDataFolder(), "frozen-players.yml");
        if (!freezeFile.exists()) {
            try {
                freezeFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear el archivo frozen-players.yml!");
            }
        }
        freezeConfig = YamlConfiguration.loadConfiguration(freezeFile);
    }
    
    public void loadFrozenPlayers() {
        List<String> frozenUUIDs = freezeConfig.getStringList("frozen");
        for (String uuidString : frozenUUIDs) {
            frozenPlayers.add(UUID.fromString(uuidString));
        }
        plugin.getLogger().info("Se han cargado " + frozenPlayers.size() + " jugadores congelados.");
    }
    
    public void saveFrozenPlayers() {
        List<String> frozenUUIDs = frozenPlayers.stream().map(UUID::toString).collect(Collectors.toList());
        freezeConfig.set("frozen", frozenUUIDs);
        try {
            freezeConfig.save(freezeFile);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar la lista de jugadores congelados!");
        }
    }

    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }
    
    public void setFrozen(Player player, boolean frozen) {
        if (frozen) {
            frozenPlayers.add(player.getUniqueId());
        } else {
            frozenPlayers.remove(player.getUniqueId());
        }
        saveFrozenPlayers();
    }

    public void toggleFreeze(Player target, Player staff) {
        if (isFrozen(target)) {
            setFrozen(target, false);
            target.sendMessage(Component.text("Has sido descongelado por un miembro del Staff.", NamedTextColor.AQUA));
            staff.sendMessage(Component.text("Has descongelado a " + target.getName() + ".", NamedTextColor.YELLOW));
        } else {
            setFrozen(target, true);
            target.sendMessage(Component.text("=============================================").color(NamedTextColor.RED));
            target.sendMessage(Component.text("Has sido congelado por un miembro del Staff.").color(NamedTextColor.RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD));
            target.sendMessage(Component.text("No te desconectes o serás sancionado.").color(NamedTextColor.RED));
            target.sendMessage(Component.text("=============================================").color(NamedTextColor.RED));
            staff.sendMessage(Component.text("Has congelado a " + target.getName() + ".", NamedTextColor.YELLOW));
        }
    }
}