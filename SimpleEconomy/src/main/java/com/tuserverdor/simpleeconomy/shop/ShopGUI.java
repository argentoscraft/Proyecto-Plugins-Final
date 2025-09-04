package com.tuserverdor.simpleeconomy.shop;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopGUI {
    private final SimpleEconomy plugin;
    public static final String SHOP_TITLE_PREFIX = "§8Tienda de ";
    public static final String CONFIRM_TITLE = "§8Confirmar Transacción";

    public ShopGUI(SimpleEconomy plugin) { this.plugin = plugin; }

    public void openShop(Player player) {
        String context = plugin.getEconomyContext(player);
        Shop shop = plugin.getShopManager().getShop(context);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "La tienda para '" + context + "' no existe.");
            return;
        }

        String shopTitle = SHOP_TITLE_PREFIX + context;
        Inventory shopInventory = Bukkit.createInventory(new ShopHolder(), shop.getSize(), shopTitle);
        ItemStack fillerPane = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < shop.getSize(); i++) shopInventory.setItem(i, fillerPane);
        
        NamespacedKey key = new NamespacedKey(plugin, "simpleeconomy_item_id");
        shop.getItems().values().forEach(shopItem -> {
            ItemStack displayItem = shopItem.getItemStack().clone();
            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                if (shopItem.getBuyPrice() > 0) lore.add("§aClick Izquierdo para Comprar (§f$" + shopItem.getBuyPrice() + "§a)");
                if (shopItem.getSellPrice() > 0) lore.add("§cClick Derecho para Vender (§f$" + shopItem.getSellPrice() + "§c)");
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, shopItem.getId());
                displayItem.setItemMeta(meta);
            }
            shopInventory.setItem(shopItem.getSlot(), displayItem);
        });
        player.openInventory(shopInventory);
    }
    
    public void openConfirmationGUI(Player player, ShopItem item, boolean isBuyMode, int amount) {
        Inventory inv = Bukkit.createInventory(new ConfirmationHolder(), 27, CONFIRM_TITLE);
        updateConfirmationGUI(inv, item, isBuyMode, amount);
        player.openInventory(inv);
    }
    
    public void updateConfirmationGUI(Inventory inv, ShopItem item, boolean isBuyMode, int amount) {
        inv.clear();
        ItemStack fillerPane = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, fillerPane);
        double price = isBuyMode ? item.getBuyPrice() : item.getSellPrice();
        String modeText = isBuyMode ? "§a§lCOMPRAR" : "§c§lVENDER";
        
        inv.setItem(10, createGuiItem(Material.RED_STAINED_GLASS_PANE, "§c-64"));
        inv.setItem(11, createGuiItem(Material.RED_STAINED_GLASS_PANE, "§c-10"));
        inv.setItem(12, createGuiItem(Material.RED_STAINED_GLASS_PANE, "§c-1"));
        inv.setItem(14, createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "§a+1"));
        inv.setItem(15, createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "§a+10"));
        inv.setItem(16, createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "§a+64"));

        ItemStack displayItem = item.getItemStack().clone();
        displayItem.setAmount(amount);
        ItemMeta meta = displayItem.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add("§7Cantidad: §e" + amount);
            lore.add("§7Precio Total: §e$" + String.format("%,.2f", price * amount));
            meta.setLore(lore);
            displayItem.setItemMeta(meta);
        }
        inv.setItem(13, displayItem);
        ItemStack confirmButton = createGuiItem(isBuyMode ? Material.LIME_WOOL : Material.RED_WOOL, modeText, "§7Click para confirmar");
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        if (confirmMeta != null) {
            NamespacedKey key = new NamespacedKey(plugin, "simpleeconomy_item_id");
            confirmMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, item.getId());
            confirmButton.setItemMeta(confirmMeta);
        }
        inv.setItem(22, confirmButton);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            Collections.addAll(loreList, lore);
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }
        return item;
    }
    public static class ShopHolder implements InventoryHolder { @Override public Inventory getInventory() { return null; }}
    public static class ConfirmationHolder implements InventoryHolder { @Override public Inventory getInventory() { return null; }}
}