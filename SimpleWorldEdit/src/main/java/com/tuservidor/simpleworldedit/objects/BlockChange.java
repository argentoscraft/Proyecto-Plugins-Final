package com.tuservidor.simpleworldedit.objects;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class BlockChange {
    private final Location location;
    private final BlockData previousState;

    public BlockChange(Location location, BlockData previousState) {
        this.location = location;
        this.previousState = previousState;
    }

    public Location getLocation() {
        return location;
    }

    public BlockData getPreviousState() {
        return previousState;
    }
}