package com.tuservidor.ranksystem.managers;

import com.tuservidor.ranksystem.RankSystem;
import com.tuservidor.ranksystem.objects.Kit;
import com.tuservidor.ranksystem.objects.Rank;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class KitManager {

    private final RankSystem plugin;
    private File kitFile;
    private FileConfiguration kitConfig;
    private final HashMap<String, Kit> kits = new HashMap<>();

    public KitManager(RankSystem plugin) {
        this.plugin = plugin;
        setupKitFile();
        loadKits();
    }

    private void setupKitFile() {
        kitFile = new File(plugin.getDataFolder(), "kits.yml");
        if (!kitFile.exists()) {
            try {
                kitFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear el archivo kits.yml!");
            }
        }
        kitConfig = YamlConfiguration.loadConfiguration(kitFile);
    }

    @SuppressWarnings("unchecked")
    public void loadKits() {
        kits.clear();
        ConfigurationSection kitsSection = kitConfig.getConfigurationSection("kits");
        if (kitsSection != null) {
            for (String kitName : kitsSection.getKeys(false)) {
                long cooldown = kitsSection.getLong(kitName + ".cooldown");
                List<ItemStack> items = (List<ItemStack>) kitsSection.getList(kitName + ".items");

                Kit kit = new Kit(kitName, cooldown);
                if (items != null) {
                    kit.getItems().addAll(items);
                }
                kits.put(kitName.toLowerCase(), kit);
            }
        }
        plugin.getLogger().info("¡Se han cargado " + kits.size() + " kits desde el archivo!");
    }

    public void saveKits() {
        kitConfig.set("kits", null);
        for (Kit kit : kits.values()) {
            String path = "kits." + kit.getName();
            kitConfig.set(path + ".cooldown", kit.getCooldown());
            kitConfig.set(path + ".items", kit.getItems());
        }
        try {
            kitConfig.save(kitFile);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar la configuración en kits.yml!");
        }
    }

    public boolean createKit(String kitName, long cooldown) {
        String lowerCaseName = kitName.toLowerCase();
        if (kits.containsKey(lowerCaseName)) {
            return false;
        }
        Kit newKit = new Kit(lowerCaseName, cooldown);
        kits.put(lowerCaseName, newKit);
        CompletableFuture.runAsync(this::saveKits);
        return true;
    }
    
    public boolean deleteKit(String kitName) {
        String lowerCaseName = kitName.toLowerCase();
        if (!kits.containsKey(lowerCaseName)) {
            return false;
        }
        kits.remove(lowerCaseName);
        // También lo eliminamos de cualquier rango que lo tuviera
        for (String rankName : plugin.getRankManager().getAllRankNames()) {
            Rank rank = plugin.getRankManager().getRank(rankName);
            if (rank != null) {
                rank.getKits().remove(lowerCaseName);
            }
        }
        // Guardamos tanto los kits como los rangos
        CompletableFuture.runAsync(this::saveKits);
        CompletableFuture.runAsync(() -> plugin.getRankManager().saveRanks());
        return true;
    }

    public Kit getKit(String kitName) {
        return kits.get(kitName.toLowerCase());
    }
}