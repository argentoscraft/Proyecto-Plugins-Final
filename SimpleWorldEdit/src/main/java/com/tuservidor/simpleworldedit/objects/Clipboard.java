package com.tuservidor.simpleworldedit.objects;

import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class Clipboard {
    private final List<BlockInfo> blocks;
    private final Vector playerOffset; // <-- CAMPO NUEVO

    public Clipboard(Vector playerOffset) {
        this.blocks = new ArrayList<>();
        this.playerOffset = playerOffset; // <-- CAMPO NUEVO
    }

    public List<BlockInfo> getBlocks() {
        return blocks;
    }

    public Vector getPlayerOffset() { // <-- MÉTODO NUEVO
        return playerOffset;
    }

    public void addBlock(BlockInfo blockInfo) {
        this.blocks.add(blockInfo);
    }
}