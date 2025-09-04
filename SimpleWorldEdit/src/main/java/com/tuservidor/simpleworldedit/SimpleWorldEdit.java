package com.tuservidor.simpleworldedit;

import com.tuservidor.simpleworldedit.commands.*;
import com.tuservidor.simpleworldedit.listeners.WandListener;
import com.tuservidor.simpleworldedit.managers.HistoryManager;
import com.tuservidor.simpleworldedit.objects.Clipboard;
import com.tuservidor.simpleworldedit.objects.Selection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class SimpleWorldEdit extends JavaPlugin {

    private final HashMap<UUID, Selection> playerSelections = new HashMap<>();
    private final HashMap<UUID, Clipboard> playerClipboards = new HashMap<>();
    private HistoryManager historyManager;

    @Override
    public void onEnable() {
        this.historyManager = new HistoryManager();

        getCommand("wand").setExecutor(new WandCommand());
        getCommand("wset").setExecutor(new SetCommand(this));     // CAMBIO AQUÍ
        getCommand("wundo").setExecutor(new UndoCommand(this));   // CAMBIO AQUÍ
        getCommand("wcopy").setExecutor(new CopyCommand(this));   // CAMBIO AQUÍ
        getCommand("wpaste").setExecutor(new PasteCommand(this)); // CAMBIO AQUÍ
        getCommand("wclear").setExecutor(new ClearCommand(this)); // CAMBIO AQUÍ

        getServer().getPluginManager().registerEvents(new WandListener(this), this);

        getLogger().info("SimpleWorldEdit ha sido habilitado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SimpleWorldEdit ha sido deshabilitado.");
    }

    public Selection getPlayerSelection(Player player) {
return playerSelections.computeIfAbsent(player.getUniqueId(), k -> new Selection());
    }
    
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
    
    public HashMap<UUID, Clipboard> getPlayerClipboards() {
        return playerClipboards;
    }
}