package com.tuservidor.simpleworldedit.managers;

import com.tuservidor.simpleworldedit.objects.BlockChange;

import java.util.*;

public class HistoryManager {

    // Usamos un Deque (una pila) para que el último cambio sea el primero en salir (LIFO)
    private final Map<UUID, Deque<List<BlockChange>>> playerHistory = new HashMap<>();

    public void recordChange(UUID playerUUID, List<BlockChange> changes) {
        playerHistory.computeIfAbsent(playerUUID, k -> new ArrayDeque<>()).push(changes);
    }

    public List<BlockChange> undoLastChange(UUID playerUUID) {
        Deque<List<BlockChange>> history = playerHistory.get(playerUUID);
        if (history == null || history.isEmpty()) {
            return null; // No hay nada que deshacer
        }
        return history.pop(); // Saca y devuelve el último cambio
    }
}