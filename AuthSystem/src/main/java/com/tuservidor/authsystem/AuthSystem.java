package com.tuservidor.authsystem;

import com.tuservidor.authsystem.commands.LoginCommand;
import com.tuservidor.authsystem.commands.RegisterCommand;
import com.tuservidor.authsystem.listeners.AuthListener;
import com.tuservidor.authsystem.managers.AuthManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AuthSystem extends JavaPlugin {

    private AuthManager authManager;
    private AuthListener authListener;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.authManager = new AuthManager(this);
        this.authListener = new AuthListener(this);

        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));

        getServer().getPluginManager().registerEvents(authListener, this);

        getLogger().info("AuthSystem ha sido habilitado.");
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public AuthListener getAuthListener() {
        return authListener;
    }
}