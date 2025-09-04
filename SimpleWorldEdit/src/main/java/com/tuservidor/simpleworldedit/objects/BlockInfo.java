package com.tuservidor.simpleworldedit.objects;

import org.bukkit.util.Vector;
import org.bukkit.block.data.BlockData;

public class BlockInfo {
    private final Vector relativePosition;
    private final BlockData blockData;

    public BlockInfo(Vector relativePosition, BlockData blockData) {
        this.relativePosition = relativePosition;
        this.blockData = blockData;
    }

    public Vector getRelativePosition() {
        return relativePosition;
    }

    public BlockData getBlockData() {
        return blockData;
    }
}