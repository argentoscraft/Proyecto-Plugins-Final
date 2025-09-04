package com.tuservidor.ranksystem;

import com.tuservidor.ranksystem.commands.*;
import com.tuservidor.ranksystem.listeners.ChatListener;
import com.tuservidor.ranksystem.listeners.PlayerJoinListener;
import com.tuservidor.ranksystem.managers.KitManager;
import com.tuservidor.ranksystem.managers.PlayerDataManager;
import com.tuservidor.ranksystem.managers.RankManager;
import com.tuservidor.ranksystem.objects.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RankSystem extends JavaPlugin {

    private RankManager rankManager;
    private PlayerDataManager playerDataManager;
    private KitManager kitManager;
    private PlayerJoinListener playerJoinListener;
    private KitGUI kitGUI;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.rankManager = new RankManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.kitManager = new KitManager(this);
        this.playerJoinListener = new PlayerJoinListener(this);
        this.kitGUI = new KitGUI(this);

        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("setrank").setExecutor(new SetRankCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("fly").setExecutor(new FlyCommand()); // <-- REGISTRO DEL NUEVO COMANDO

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(this.playerJoinListener, this);
        getServer().getPluginManager().registerEvents(this.kitGUI, this);

        getLogger().info("RankSystem (Dinamico) ha sido habilitado!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RankSystem ha sido deshabilitado.");
    }

    public void updatePlayerTablist(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        String rankName = getPlayerDataManager().getPlayerRank(player.getUniqueId());
        Rank rank = getRankManager().getRank(rankName);

        if (rank == null) {
            player.playerListName(Component.text(player.getName()));
            return;
        }

        Component prefix = LegacyComponentSerializer.legacyAmpersand().deserialize(rank.getPrefix() + " ");
        Component playerName = Component.text(player.getName());
        Component finalListName = prefix.append(playerName);

        player.playerListName(finalListName);
    }

    // Getters
    public RankManager getRankManager() { return rankManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public KitManager getKitManager() { return kitManager; }
    public PlayerJoinListener getPlayerJoinListener() { return playerJoinListener; }
    public KitGUI getKitGUI() { return kitGUI; }
}