package com.tuserverdor.simpleeconomy.shop;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopManager {
    private final SimpleEconomy plugin;
    private final Map<String, Shop> shops = new HashMap<>();
    public ShopManager(SimpleEconomy plugin) {
        this.plugin = plugin;
        loadShops();
    }
    public void reloadShops() {
        shops.clear();
        loadShops();
    }
    private void loadShops() {
        File configFile = new File(plugin.getDataFolder(), "shop.yml");
        if (!configFile.exists()) {
            plugin.saveResource("shop.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        
        ConfigurationSection shopsSection = config.getConfigurationSection("shops");
        if (shopsSection == null) return;

        for (String shopKey : shopsSection.getKeys(false)) {
            ConfigurationSection currentShopSection = shopsSection.getConfigurationSection(shopKey);
            if (currentShopSection == null) continue;
            int size = currentShopSection.getInt("size", 54);
            Shop shop = new Shop(shopKey, size);
            
            ConfigurationSection itemsSection = currentShopSection.getConfigurationSection("items");
            if (itemsSection != null) {
                for (String itemKey : itemsSection.getKeys(false)) {
                    ConfigurationSection itemDetails = itemsSection.getConfigurationSection(itemKey);
                    if (itemDetails != null) {
                        try {
                            Material material = Material.valueOf(itemDetails.getString("material", "STONE").toUpperCase());
                            int slot = itemDetails.getInt("slot");
                            double buyPrice = itemDetails.getDouble("buy", -1);
                            double sellPrice = itemDetails.getDouble("sell", -1);
                            ItemStack itemStack = new ItemStack(material, 1);
                            ShopItem shopItem = new ShopItem(itemKey, itemStack, slot, buyPrice, sellPrice);
                            shop.addItem(shopItem);
                        } catch (Exception e) {
                            plugin.getLogger().warning("Error cargando el item '" + itemKey + "' en la tienda '" + shopKey + "'.");
                        }
                    }
                }
            }
            shops.put(shopKey, shop);
        }
        plugin.getLogger().info("Se cargaron " + shops.size() + " tiendas desde shop.yml.");
    }
    public Shop getShop(String economyContext) {
        return shops.get(economyContext);
    }
}