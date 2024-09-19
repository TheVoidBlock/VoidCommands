package com.thevoidblock.voidcommands;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ChunkTracker {

    public static List<ChunkPos> loadedChunks = new ArrayList<>();

    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register(
                (world, chunk) -> loadedChunks.add(chunk.getPos())
        );
        ClientChunkEvents.CHUNK_UNLOAD.register(
                (world, chunk) -> loadedChunks.remove(chunk.getPos())
        );
    }
}
