package com.tuserverdor.simpleeconomy.shop;

import org.bukkit.inventory.ItemStack;
import java.util.Objects;

public class ShopItem {
    private final String id;
    private final ItemStack itemStack;
    private final int slot;
    private final double buyPrice;
    private final double sellPrice;

    public ShopItem(String id, ItemStack itemStack, int slot, double buyPrice, double sellPrice) {
        this.id = id;
        this.itemStack = itemStack;
        this.slot = slot;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
    public String getId() { return id; }
    public ItemStack getItemStack() { return itemStack; }
    public int getSlot() { return slot; }
    public double getBuyPrice() { return buyPrice; }
    public double getSellPrice() { return sellPrice; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopItem shopItem = (ShopItem) o;
        return id.equals(shopItem.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}