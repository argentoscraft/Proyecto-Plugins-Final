package com.tuserverdor.interfacecore.managers;

// Este archivo define qué es un objeto PlayerData.
public class PlayerData {
    private boolean scoreboardActive = true; // El scoreboard estará activo por defecto
    private boolean clockActive = true;

    public boolean isScoreboardActive() {
        return scoreboardActive;
    }

    public void setScoreboardActive(boolean scoreboardActive) {
        this.scoreboardActive = scoreboardActive;
    }

    public void toggleScoreboard() {
        this.scoreboardActive = !this.scoreboardActive;
    }
    
    // (Opcional, para el futuro reloj que querías hacer)
    public boolean isClockActive() {
        return clockActive;
    }

    public void toggleClock() {
        this.clockActive = !this.clockActive;
    }
}