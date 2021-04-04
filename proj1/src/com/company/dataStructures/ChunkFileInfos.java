package com.company.dataStructures;

import java.io.File;
import java.util.HashMap;

public class ChunkFileInfos {

    //Key: File ID (encrypted); Value: Chunk information in that file that are stored here
    public HashMap<String, ChunkFileInfo> chunkInfos = new HashMap<>();


    public boolean AddChunk(String fileID, int chunkNo){
        ChunkFileInfo a = chunkInfos.get(fileID);
        if(a == null){
            chunkInfos.put(fileID, new ChunkFileInfo());
            a = chunkInfos.get(fileID);
        }
        if(!a.ChunkExists(chunkNo)){
            a.chunks.add(chunkNo);
            return true;
        }
        return false;
    }
}
