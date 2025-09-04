package com.tuservidor.enchants;

import com.tuservidor.enchants.commands.EnchantCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantsPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("encantar").setExecutor(new EnchantCommand());
        getLogger().info("EnchantsPlugin ha sido habilitado.");
    }
}