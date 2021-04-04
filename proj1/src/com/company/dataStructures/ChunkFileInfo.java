package com.company.dataStructures;

import java.util.ArrayList;

public class ChunkFileInfo {

    ArrayList<Integer> chunks = new ArrayList<>();

    public ChunkFileInfo(){
    }

    public boolean ChunkExists(int chunkNo){
        for(Integer i: chunks){
            if (i == chunkNo)
                return true;
        }
        return false;
    }
    public void AddChunk(int chunkNo){
        for(Integer i: chunks){
            if (i == chunkNo)
                return;
        }
        chunks.add(chunkNo);
    }
}
