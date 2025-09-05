package com.tuserverdor.simpleeconomy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final Map<String, Double> balances = new HashMap<>();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, Double> getBalances() {
        return balances;
    }
}