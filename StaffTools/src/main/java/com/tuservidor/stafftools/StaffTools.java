package com.tuservidor.stafftools;

import com.tuservidor.stafftools.commands.*;
import com.tuservidor.stafftools.listeners.FreezeListener;
import com.tuservidor.stafftools.listeners.PunishmentListener;
import com.tuservidor.stafftools.listeners.StaffToolsListener;
import com.tuservidor.stafftools.listeners.XRayAlertListener;
import com.tuservidor.stafftools.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffTools extends JavaPlugin {

    private StaffModeManager staffModeManager;
    private VanishManager vanishManager;
    private FreezeManager freezeManager;
    private PlayerNavigatorGUI playerNavigatorGUI;
    private XRayAlertManager xRayAlertManager;
    private PunishmentManager punishmentManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.staffModeManager = new StaffModeManager(this);
        this.vanishManager = new VanishManager(this);
        this.freezeManager = new FreezeManager(this);
        this.playerNavigatorGUI = new PlayerNavigatorGUI(this);
        this.xRayAlertManager = new XRayAlertManager(this);
        this.punishmentManager = new PunishmentManager(this);

        getCommand("staff").setExecutor(new StaffCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));     // <-- REGISTRO
        getCommand("unmute").setExecutor(new UnmuteCommand(this)); // <-- REGISTRO

        getServer().getPluginManager().registerEvents(new StaffToolsListener(this), this);
        getServer().getPluginManager().registerEvents(this.playerNavigatorGUI, this);
        getServer().getPluginManager().registerEvents(new XRayAlertListener(this), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);
        getServer().getPluginManager().registerEvents(new PunishmentListener(this), this);

        getLogger().info("StaffTools ha sido habilitado.");
    }

    @Override
    public void onDisable() {
        if (freezeManager != null) {
            freezeManager.saveFrozenPlayers();
        }
        if (punishmentManager != null) {
            punishmentManager.save();
        }
        getLogger().info("StaffTools ha sido deshabilitado.");
    }
    
    // --- GETTERS (Sin cambios) ---
    public StaffModeManager getStaffModeManager() { return staffModeManager; }
    public VanishManager getVanishManager() { return vanishManager; }
    public FreezeManager getFreezeManager() { return freezeManager; }
    public PlayerNavigatorGUI getPlayerNavigatorGUI() { return playerNavigatorGUI; }
    public XRayAlertManager getXRayAlertManager() { return xRayAlertManager; }
    public PunishmentManager getPunishmentManager() { return punishmentManager; }
}