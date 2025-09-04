package com.tuserverdor.simpleeconomy.listeners;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import com.tuserverdor.simpleeconomy.shop.ShopGUI;
import com.tuserverdor.simpleeconomy.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ShopListener implements Listener {
    private final SimpleEconomy plugin;
    private final NamespacedKey shopItemIdKey;
    private final Map<UUID, Transaction> transactions = new HashMap<>();

    public ShopListener(SimpleEconomy plugin) {
        this.plugin = plugin;
        this.shopItemIdKey = new NamespacedKey(plugin, "simpleeconomy_item_id");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTopInventory().getHolder() instanceof ShopGUI.ShopHolder) {
            event.setCancelled(true);
            handleMainShopClick(player, event.getCurrentItem(), event.isLeftClick(), event.isRightClick());
        } else if (event.getView().getTopInventory().getHolder() instanceof ShopGUI.ConfirmationHolder) {
            event.setCancelled(true);
            handleConfirmationClick(player, event.getCurrentItem(), event.getSlot());
        }
    }

    private void handleMainShopClick(Player player, ItemStack clickedItem, boolean isLeftClick, boolean isRightClick) {
        if (clickedItem == null) return;
        String context = plugin.getEconomyContext(player);
        getShopItemFromNBT(context, clickedItem).ifPresent(shopItem -> {
            boolean isBuy = isLeftClick && shopItem.getBuyPrice() > 0;
            boolean isSell = isRightClick && shopItem.getSellPrice() > 0;

            if (isBuy || isSell) {
                transactions.put(player.getUniqueId(), new Transaction(shopItem, isBuy));
                new ShopGUI(plugin).openConfirmationGUI(player, shopItem, isBuy, 1);
            }
        });
    }

    private void handleConfirmationClick(Player player, ItemStack clickedItem, int slot) {
        if (clickedItem == null) return;
        Transaction transaction = transactions.get(player.getUniqueId());
        if (transaction == null) {
            player.closeInventory();
            return;
        }

        ShopItem item = transaction.getShopItem();
        int amount = transaction.getAmount();

        switch (slot) {
            case 10: transaction.setAmount(amount - 64); break;
            case 11: transaction.setAmount(amount - 10); break;
            case 12: transaction.setAmount(amount - 1); break;
            case 14: transaction.setAmount(amount + 1); break;
            case 15: transaction.setAmount(amount + 10); break;
            case 16: transaction.setAmount(amount + 64); break;
            case 22:
                executeTransaction(player, transaction);
                return; // Salimos porque la transacción cierra el inventario
            default:
                return; // Si no es un botón conocido, no hacemos nada
        }

        // --- ESTA ES LA MAGIA ---
        // En lugar de reabrir, actualizamos el inventario que el jugador YA TIENE ABIERTO.
        // Esto es estable y no causa que se cierre.
        new ShopGUI(plugin).updateConfirmationGUI(player.getOpenInventory().getTopInventory(), item, transaction.isBuyMode(), transaction.getAmount());
    }

    private void executeTransaction(Player player, Transaction transaction) {
        String context = plugin.getEconomyContext(player);
        ShopItem shopItem = transaction.getShopItem();
        int amount = transaction.getAmount();
        double totalPrice = (transaction.isBuyMode() ? shopItem.getBuyPrice() : shopItem.getSellPrice()) * amount;

        if (transaction.isBuyMode()) {
            if (plugin.getDatabaseManager().getMoney(player) < totalPrice) {
                player.sendMessage(ChatColor.RED + "No tienes suficiente dinero.");
                return;
            }
            plugin.getDatabaseManager().removeMoney(player.getUniqueId(), player.getName(), totalPrice, context);
            ItemStack toGive = shopItem.getItemStack().clone();
            toGive.setAmount(amount);
            player.getInventory().addItem(toGive);
            player.sendMessage(ChatColor.GREEN + "¡Compra realizada con éxito!");
        } else { // Modo Venta
            ItemStack toSell = shopItem.getItemStack().clone();
            toSell.setAmount(amount);
            if (!player.getInventory().containsAtLeast(toSell, amount)) {
                player.sendMessage(ChatColor.RED + "No tienes suficientes items para vender.");
                return;
            }
            player.getInventory().removeItem(toSell);
            plugin.getDatabaseManager().addMoney(player.getUniqueId(), player.getName(), totalPrice, context);
            player.sendMessage(ChatColor.GREEN + "¡Has vendido tus items!");
        }
        player.closeInventory();
    }

    private Optional<ShopItem> getShopItemFromNBT(String context, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Optional.empty();
        String id = item.getItemMeta().getPersistentDataContainer().get(shopItemIdKey, PersistentDataType.STRING);
        if (id == null) return Optional.empty();
        return plugin.getShopManager().getShop(context).getItems().values().stream().filter(si -> si.getId().equals(id)).findFirst();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof ShopGUI.ConfirmationHolder) {
            transactions.remove(event.getPlayer().getUniqueId());
        }
    }

    private static class Transaction {
        private final ShopItem shopItem;
        private final boolean isBuyMode;
        private int amount;
        public Transaction(ShopItem shopItem, boolean isBuyMode) { this.shopItem = shopItem; this.isBuyMode = isBuyMode; this.amount = 1; }
        public ShopItem getShopItem() { return shopItem; }
        public boolean isBuyMode() { return isBuyMode; }
        public int getAmount() { return amount; }
        public void setAmount(int newAmount) {
            this.amount = Math.max(1, Math.min(64, newAmount));
        }
    }
}


// YA FUNCIONA DOWNSITO