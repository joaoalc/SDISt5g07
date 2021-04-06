package com.company.dataStructures;

import java.util.ArrayList;

public class ChunkFileInfo {

    // public ArrayList<Integer> chunks = new ArrayList<>();
    public ArrayList<Chunk> chunks = new ArrayList<>();

    public ChunkFileInfo(){
    }

    public boolean chunkExists(int chunkNo){
        /*for(Integer i: chunks){
            if (i == chunkNo)
                return true;
        }*/
        for (Chunk chunk : chunks) {
            if (chunk.getChunkNo() == chunkNo) {
                return true;
            }
        }
        return false;
    }
    /*public void addChunk(int chunkNo){
        for(Integer i: chunks){
            if (i == chunkNo)
                return;
        }
        chunks.add(chunkNo);
    }*/

    public void addChunk(Chunk chunk) {
        for(Chunk c: chunks){
            if (c.getChunkNo() == chunk.getChunkNo())
                return;
        }
        chunks.add(chunk);
    }

    public boolean removeChunk(int chunkNo) {
        if (!this.chunkExists(chunkNo)) {
            return false;
        }

        for (int i = 0; i < this.chunks.size(); i++) {
            if (this.chunks.get(i).getChunkNo() == chunkNo) {
                this.chunks.remove(i);
                return true;
            }
        }

        return false;
    }
}
