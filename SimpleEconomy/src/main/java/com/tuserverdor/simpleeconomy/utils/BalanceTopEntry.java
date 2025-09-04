package com.tuserverdor.simpleeconomy.utils;

public class BalanceTopEntry {
    private final String playerName;
    private final double balance;

    public BalanceTopEntry(String playerName, double balance) {
        this.playerName = playerName;
        this.balance = balance;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getBalance() {
        return balance;
    }
}