package com.tuserverdor.simpleeconomy.shop;

import java.util.HashMap;
import java.util.Map;

public class Shop {
    private final String id;
    private final int size;
    private final Map<Integer, ShopItem> items = new HashMap<>();

    public Shop(String id, int size) {
        this.id = id;
        this.size = size;
    }
    public void addItem(ShopItem item) {
        items.put(item.getSlot(), item);
    }
    public String getId() { return id; }
    public int getSize() { return size; }
    public Map<Integer, ShopItem> getItems() { return items; }
}