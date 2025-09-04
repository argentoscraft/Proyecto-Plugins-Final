package com.tuservidor.serverguard;

import com.tuservidor.serverguard.commands.RegionCommand;
import com.tuservidor.serverguard.listeners.*;
import com.tuservidor.serverguard.managers.RegionManager;
import com.tuservidor.serverguard.tasks.TimeSyncTask;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerGuard extends JavaPlugin {

    private RegionManager regionManager;
    // El campo de AuthSystem ha sido eliminado

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        // La conexión a AuthSystem ha sido eliminada
        
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        this.regionManager = new RegionManager(this);

        getCommand("region").setExecutor(new RegionCommand(this));
        
        getServer().getPluginManager().registerEvents(new SelectionListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new PvpListener(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawningListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerUseItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRegionListener(this), this);
        getServer().getPluginManager().registerEvents(new ForceSpawnListener(this), this);

        if (getConfig().getBoolean("global-rules.real-world-time-sync.enabled", false)) {
            new TimeSyncTask(this).runTaskTimer(this, 0L, 1200L);
        }

        getLogger().info("ServerGuard ha sido habilitado.");
    }

    @Override
    public void onDisable() {
        if (this.regionManager != null) this.regionManager.saveRegions();
        getLogger().info("ServerGuard ha sido deshabilitado.");
    }

    public RegionManager getRegionManager() { return regionManager; }
    // El getter de AuthSystem ha sido eliminado
    
    @Override
    public java.util.logging.Logger getLogger() {
        return super.getLogger();
    }
}