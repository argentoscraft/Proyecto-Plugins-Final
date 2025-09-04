package com.tuservidor.serverguard.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Region {
    private final String name;
    private final World world;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private final Map<String, String> flags = new HashMap<>();

    public Region(String name, Location pos1, Location pos2) {
        this.name = name.toLowerCase();
        this.world = pos1.getWorld();
        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    // --- MÉTODOS NUEVOS PARA FLAGS ---
    public void setFlag(String flagName, String value) {
        flags.put(flagName.toLowerCase(), value.toLowerCase());
    }

    public String getFlag(String flagName) {
        return flags.get(flagName.toLowerCase());
    }
    
    public Map<String, String> getFlags() {
        return flags;
    }

    // --- MÉTODOS QUE YA TENÍAS ---
    public String getName() { return name; }
    public World getWorld() { return world; }
    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
    
    public boolean contains(Location location) {
        if (location.getWorld() == null || !location.getWorld().equals(this.world)) {
            return false;
        }
        return location.getBlockX() >= minX && location.getBlockX() <= maxX
            && location.getBlockY() >= minY && location.getBlockY() <= maxY
            && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }
    
    public boolean contains(Player player) {
        return contains(player.getLocation());
    }
}