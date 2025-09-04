package com.tuservidor.ranksystem.objects;

import java.util.ArrayList;
import java.util.List;

public class Rank {

    private final String name;
    private String prefix;
    private String chatColor;
    private String parent;
    private final List<String> permissions;
    private final List<String> kits; // <-- LÍNEA NUEVA

    public Rank(String name) {
        this.name = name.toLowerCase();
        this.prefix = "&7[" + name + "]";
        this.chatColor = "&7";
        this.parent = null;
        this.permissions = new ArrayList<>();
        this.kits = new ArrayList<>(); // <-- LÍNEA NUEVA
    }

    // Getters
    public String getName() { return name; }
    public String getPrefix() { return prefix; }
    public String getChatColor() { return chatColor; }
    public String getParent() { return parent; }
    public List<String> getPermissions() { return permissions; }
    public List<String> getKits() { return kits; } // <-- MÉTODO NUEVO

    // Setters
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public void setChatColor(String chatColor) { this.chatColor = chatColor; }
    public void setParent(String parent) { this.parent = parent; }
}