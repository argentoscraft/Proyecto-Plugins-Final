package com.tuservidor.serverguard.managers;

import com.tuservidor.serverguard.ServerGuard;
import com.tuservidor.serverguard.objects.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RegionManager {
    private final ServerGuard plugin;
    private final Map<String, Region> regions = new HashMap<>();
    private final Map<UUID, Location> pos1Selections = new HashMap<>();
    private final Map<UUID, Location> pos2Selections = new HashMap<>();
    private File regionsFile;
    private FileConfiguration regionsConfig;

    public RegionManager(ServerGuard plugin) {
        this.plugin = plugin;
        setupRegionsFile();
        loadRegions();
    }

    private void setupRegionsFile() {
        regionsFile = new File(plugin.getDataFolder(), "regions.yml");
        if (!regionsFile.exists()) {
            try {
                regionsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear el archivo regions.yml!");
            }
        }
        regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);
    }

    public void loadRegions() {
        regions.clear();
        ConfigurationSection section = regionsConfig.getConfigurationSection("regions");
        if (section == null) return;

        for (String regionName : section.getKeys(false)) {
            World world = Bukkit.getWorld(section.getString(regionName + ".world"));
            if (world == null) continue;
            
            Location pos1 = new Location(world, section.getInt(regionName + ".pos1.x"), section.getInt(regionName + ".pos1.y"), section.getInt(regionName + ".pos1.z"));
            Location pos2 = new Location(world, section.getInt(regionName + ".pos2.x"), section.getInt(regionName + ".pos2.y"), section.getInt(regionName + ".pos2.z"));
            
            Region region = new Region(regionName, pos1, pos2);
            
            ConfigurationSection flagsSection = section.getConfigurationSection(regionName + ".flags");
            if (flagsSection != null) {
                for (String flagName : flagsSection.getKeys(false)) {
                    region.setFlag(flagName, flagsSection.getString(flagName));
                }
            }
            regions.put(regionName.toLowerCase(), region);
        }
        plugin.getLogger().info("Se han cargado " + regions.size() + " regiones.");
    }

    public void saveRegions() {
        regionsConfig.set("regions", null);
        for (Map.Entry<String, Region> entry : regions.entrySet()) {
            String name = entry.getKey();
            Region region = entry.getValue();
            String path = "regions." + name;
            regionsConfig.set(path + ".world", region.getWorld().getName());
            regionsConfig.set(path + ".pos1.x", region.getMinX());
            regionsConfig.set(path + ".pos1.y", region.getMinY());
            regionsConfig.set(path + ".pos1.z", region.getMinZ());
            regionsConfig.set(path + ".pos2.x", region.getMaxX());
            regionsConfig.set(path + ".pos2.y", region.getMaxY());
            regionsConfig.set(path + ".pos2.z", region.getMaxZ());
            for (Map.Entry<String, String> flagEntry : region.getFlags().entrySet()) {
                regionsConfig.set(path + ".flags." + flagEntry.getKey(), flagEntry.getValue());
            }
        }
        try {
            regionsConfig.save(regionsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar la configuración en regions.yml!");
        }
    }

    public boolean defineRegion(String name, UUID playerUUID) {
        String lowerCaseName = name.toLowerCase();
        if (regions.containsKey(lowerCaseName)) return false;
        
        Location pos1 = pos1Selections.get(playerUUID);
        Location pos2 = pos2Selections.get(playerUUID);
        if (pos1 == null || pos2 == null) return false;
        
        Region region = new Region(name, pos1, pos2);
        regions.put(lowerCaseName, region);

        CompletableFuture.runAsync(this::saveRegions);
        return true;
    }

    // --- ¡MÉTODOS AÑADIDOS! ---
    public boolean deleteRegion(String name) {
        String lowerCaseName = name.toLowerCase();
        if (regions.remove(lowerCaseName) != null) {
            CompletableFuture.runAsync(this::saveRegions);
            return true;
        }
        return false;
    }
    
    public Set<String> getRegionNames() {
        return regions.keySet();
    }
    
    // --- MÉTODOS QUE YA TENÍAS ---
    public Region getRegionAt(Location location) {
        for (Region region : regions.values()) {
            if (region.contains(location)) {
                return region;
            }
        }
        return null;
    }
    
    public Region getRegionByName(String name) {
        return regions.get(name.toLowerCase());
    }

    public void setPos1(UUID playerUUID, Location location) { pos1Selections.put(playerUUID, location); }
    public void setPos2(UUID playerUUID, Location location) { pos2Selections.put(playerUUID, location); }
    public Location getPos1(UUID playerUUID) { return pos1Selections.get(playerUUID); }
    public Location getPos2(UUID playerUUID) { return pos2Selections.get(playerUUID); }
}