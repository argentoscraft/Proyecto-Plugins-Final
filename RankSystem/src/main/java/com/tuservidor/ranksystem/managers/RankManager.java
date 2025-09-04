package com.tuservidor.ranksystem.managers;

import com.tuservidor.ranksystem.RankSystem;
import com.tuservidor.ranksystem.objects.Rank;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RankManager {

    private final RankSystem plugin;
    private File rankFile;
    private FileConfiguration rankConfig;
    private String defaultRankName = "miembro";

    private final HashMap<String, Rank> ranks = new HashMap<>();

    public RankManager(RankSystem plugin) {
        this.plugin = plugin;
        setupRankFile();
        loadRanks();
    }

    private void setupRankFile() {
        rankFile = new File(plugin.getDataFolder(), "ranks.yml");
        if (!rankFile.exists()) {
            try {
                rankFile.createNewFile();
                rankConfig = YamlConfiguration.loadConfiguration(rankFile);
                rankConfig.set("default_rank", "miembro");
                rankConfig.set("ranks.miembro.prefix", "&7[Miembro]");
                rankConfig.set("ranks.miembro.chat_color", "&7");
                rankConfig.set("ranks.miembro.permissions", List.of("tpa.usar"));
                rankConfig.set("ranks.miembro.kits", List.of("diario"));
                rankConfig.save(rankFile);
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear el archivo ranks.yml!");
            }
        }
        rankConfig = YamlConfiguration.loadConfiguration(rankFile);
    }

    public void loadRanks() {
        ranks.clear();
        defaultRankName = rankConfig.getString("default_rank", "miembro");
        ConfigurationSection ranksSection = rankConfig.getConfigurationSection("ranks");
        if (ranksSection != null) {
            for (String rankName : ranksSection.getKeys(false)) {
                Rank rank = new Rank(rankName);
                rank.setPrefix(ranksSection.getString(rankName + ".prefix"));
                rank.setChatColor(ranksSection.getString(rankName + ".chat_color"));
                rank.setParent(ranksSection.getString(rankName + ".parent"));
                rank.getPermissions().addAll(ranksSection.getStringList(rankName + ".permissions"));
                rank.getKits().addAll(ranksSection.getStringList(rankName + ".kits"));
                ranks.put(rankName.toLowerCase(), rank);
            }
        }
        plugin.getLogger().info("¡Se han cargado " + ranks.size() + " rangos desde el archivo!");
    }

    public void saveRanks() {
        for (String key : rankConfig.getKeys(false)) {
            rankConfig.set(key, null);
        }
        rankConfig.set("default_rank", defaultRankName);
        for (Rank rank : ranks.values()) {
            String path = "ranks." + rank.getName();
            rankConfig.set(path + ".prefix", rank.getPrefix());
            rankConfig.set(path + ".chat_color", rank.getChatColor());
            rankConfig.set(path + ".parent", rank.getParent());
            rankConfig.set(path + ".permissions", rank.getPermissions());
            rankConfig.set(path + ".kits", rank.getKits());
        }
        try {
            rankConfig.save(rankFile);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar la configuración en ranks.yml!");
        }
    }

    public boolean createRank(String rankName) {
        String lowerCaseName = rankName.toLowerCase();
        if (ranks.containsKey(lowerCaseName)) {
            return false;
        }
        Rank newRank = new Rank(lowerCaseName);
        ranks.put(lowerCaseName, newRank);
        CompletableFuture.runAsync(this::saveRanks);
        return true;
    }

    public boolean deleteRank(String rankName) {
        String lowerCaseName = rankName.toLowerCase();
        if (!ranks.containsKey(lowerCaseName)) {
            return false;
        }
        if (lowerCaseName.equals(defaultRankName)) {
            return false;
        }
        // Seguridad: no dejar borrar un rango si otro hereda de él
        for (Rank rank : ranks.values()) {
            if (lowerCaseName.equals(rank.getParent())) {
                return false;
            }
        }
        ranks.remove(lowerCaseName);
        CompletableFuture.runAsync(this::saveRanks);
        return true;
    }

    public List<String> getAllPermissions(String rankName) {
        List<String> allPermissions = new ArrayList<>();
        Rank currentRank = getRank(rankName);
        while (currentRank != null) {
            allPermissions.addAll(currentRank.getPermissions());
            if (currentRank.getParent() != null) {
                currentRank = getRank(currentRank.getParent());
            } else {
                break;
            }
        }
        return allPermissions;
    }

    public List<String> getAllKits(String rankName) {
        List<String> allKits = new ArrayList<>();
        Rank currentRank = getRank(rankName);
        while (currentRank != null) {
            allKits.addAll(currentRank.getKits());
            if (currentRank.getParent() != null) {
                currentRank = getRank(currentRank.getParent());
            } else {
                break;
            }
        }
        return allKits;
    }

    public Rank getRank(String rankName) {
        return ranks.get(rankName.toLowerCase());
    }

    public String getDefaultRankName() {
        return defaultRankName;
    }
    
    public Set<String> getAllRankNames() {
        return ranks.keySet();
    }
}