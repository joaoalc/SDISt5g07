package com.company.dataStructures;

import java.util.ArrayList;

public class ChunkFileInfo {

    ArrayList<Integer> chunks = new ArrayList<>();

    public ChunkFileInfo(){
    }

    public boolean chunkExists(int chunkNo){
        for(Integer i: chunks){
            if (i == chunkNo)
                return true;
        }
        return false;
    }
    public void addChunk(int chunkNo){
        for(Integer i: chunks){
            if (i == chunkNo)
                return;
        }
        chunks.add(chunkNo);
    }

    public boolean removeChunk(int chunkNo) {
        if (!this.chunkExists(chunkNo)) {
            return false;
        }

        this.chunks.remove(chunkNo);
        return true;
    }
}
