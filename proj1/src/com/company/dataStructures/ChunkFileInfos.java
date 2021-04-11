package com.company.dataStructures;

import com.company.Peer;

import java.util.HashMap;
import java.util.Map;

public class ChunkFileInfos {

    //Key: File ID (encrypted); Value: Chunk information in that file that are stored here
    public HashMap<String, ChunkFileInfo> chunkInfos = new HashMap<>();


    /*public boolean addChunk(String fileID, int chunkNo){
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
    }*/

    public boolean addChunk(String fileID, Chunk chunk){
        ChunkFileInfo a = chunkInfos.get(fileID);
        if(a == null){
            chunkInfos.put(fileID, new ChunkFileInfo());
            a = chunkInfos.get(fileID);
        }

        if(!a.chunkExists(chunk.getChunkNo())){
            a.chunks.add(chunk);
            return true;
        }
        return false;
    }

    public void incrementChunkPerceivedReplicationDegree(String fileID, int chunkNo, PeerStorage peerStorage, int peerNum) {
        System.out.println("Incremented perceived replication degree of file with id and chunk number " + fileID + " " + chunkNo);
        for (String currentID : chunkInfos.keySet()) {
            System.out.println("Current ID: " + currentID);
        }

        ChunkFileInfo chunkFileInfo = chunkInfos.get(fileID);
        if (chunkFileInfo == null) {
            return;
        }

        if (chunkFileInfo.chunkExists(chunkNo)) {
            chunkFileInfo.getChunk(chunkNo).incrementPerceivedReplicationDegree(peerStorage, peerNum);
        }
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

    public String getValuesHumanReadable() {
        String a = "";
        a += "This peer contains " + chunkInfos.size() + " files\n\n";
        int fileNum = 0;
        for(Map.Entry<String, ChunkFileInfo> set: chunkInfos.entrySet()){
            a += "File number " + fileNum + ": \n";
            //(File path of when this version of the file was inserted, file could've been manually moved or overwritte, in which case it would not be there)
            a += "The file's ID is: " + set.getKey() + "\n";

            for(int i = 0; i < set.getValue().chunks.size(); i++){
                a += "Chunk number " + i + " is being backed up\n";
            }
        }

        return a;
    }

    public String getState() {
        String result = "";
        result += "This peer contains " + chunkInfos.size() + " files\n\n";
        int fileNum = 0;
        for(Map.Entry<String, ChunkFileInfo> set: chunkInfos.entrySet()){
            result += "File number " + fileNum + ": \n";
            //(File path of when this version of the file was inserted, file could've been manually moved or overwritte, in which case it would not be there)
            result += "The file's ID is: " + set.getKey() + "\n";

            result += "The file has " + set.getValue().chunks.size() + " chunks:";
            for (Chunk chunk : set.getValue().chunks){
                result += "\t- ID: " + chunk.getChunkNo();
                result += "\t- Size: " + chunk.getSize();
                result += "\t- Desired replication degree: " + chunk.getDesiredReplicationDegree();
                result += "\t- Perceived replication degree: " + chunk.getPerceivedReplicationDegree();
            }
        }

        return result;
    }

    public void printValuesHumanReadable() {
        System.out.println(getValuesHumanReadable());
    }

    public void removeEmptyFiles(){
        for (Map.Entry<String, ChunkFileInfo> set : chunkInfos.entrySet()) {
            if(set.getValue().chunks.size() == 0){
                chunkInfos.remove(set.getKey(), set.getValue());
            }
        }
    }
}
