package com.tuservidor.ranksystem.objects;

import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class Kit {

    private final String name;
    private long cooldown; // en segundos
    private final List<ItemStack> items;

    public Kit(String name, long cooldown) {
        this.name = name.toLowerCase();
        this.cooldown = cooldown;
        this.items = new ArrayList<>();
    }

    // Getters y Setters
    public String getName() { return name; }
    public long getCooldown() { return cooldown; }
    public List<ItemStack> getItems() { return items; }
    public void setCooldown(long cooldown) { this.cooldown = cooldown; }
}