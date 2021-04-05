package com.company.dataStructures;

import java.util.HashMap;

public class ChunkFileInfos {

    //Key: File ID (encrypted); Value: Chunk information in that file that are stored here
    public HashMap<String, ChunkFileInfo> chunkInfos = new HashMap<>();


    public boolean addChunk(String fileID, int chunkNo){
        ChunkFileInfo a = chunkInfos.get(fileID);
        if(a == null){
            chunkInfos.put(fileID, new ChunkFileInfo());
            a = chunkInfos.get(fileID);
        }
        if(!a.chunkExists(chunkNo)){
            a.chunks.add(chunkNo);
            return true;
        }
        return false;
    }
    
    public boolean removeChunk(String fileID, int chunkNo) {
        ChunkFileInfo chunkFileInfo = chunkInfos.get(fileID);
        
        if (chunkFileInfo == null) {
            return false;
        }

        if (!chunkFileInfo.removeChunk(chunkNo)) {
            System.out.println("Failed to remove chunk");
        }
        return true;
    }
}
