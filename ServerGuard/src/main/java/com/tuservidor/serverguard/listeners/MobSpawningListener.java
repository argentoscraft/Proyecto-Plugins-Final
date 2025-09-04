package com.tuservidor.serverguard.listeners;

import com.tuservidor.serverguard.ServerGuard;
import com.tuservidor.serverguard.managers.RegionManager;
import com.tuservidor.serverguard.objects.Region;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawningListener implements Listener {

    private final RegionManager regionManager;

    public MobSpawningListener(ServerGuard plugin) {
        this.regionManager = plugin.getRegionManager();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Region region = regionManager.getRegionAt(event.getLocation());
        if (region == null) {
            return;
        }

        // --- LÓGICA SIMPLIFICADA Y A PRUEBA DE FALLOS ---

        // Si es un monstruo (esto incluye a los Phantoms)
        if (event.getEntity() instanceof Monster) {
            String mobSpawningFlag = region.getFlag("mob-spawning");
            if ("deny".equalsIgnoreCase(mobSpawningFlag)) {
                // Si la razón es natural (lo que incluye Phantoms por no dormir), lo cancelamos.
                if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                    event.setCancelled(true);
                }
            }
        }

        // Si es un animal
        if (event.getEntity() instanceof Animals) {
            String animalSpawningFlag = region.getFlag("animal-spawning");
            if ("deny".equalsIgnoreCase(animalSpawningFlag)) {
                if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                    event.setCancelled(true);
                }
            }
        }
    }
}