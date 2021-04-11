package com.company.dataStructures;

import java.util.ArrayList;
import java.util.Comparator;

public class Chunk {
    private int chunkNo;
    private long size;
    private int desiredReplicationDegree;
    //private int perceivedReplicationDegree;
    private String fileID; //Already stored in ChunkFileInfos hashmap, but we need it here for faster acess
    private ArrayList<Integer> peersBackingUp = new ArrayList<>();

    public Chunk(int chunkNo, long size, int desiredReplicationDegree, ArrayList<Integer> peersBackingUp, String fileID) {
        this.chunkNo = chunkNo;
        this.size = size;
        this.desiredReplicationDegree = desiredReplicationDegree;
        //this.perceivedReplicationDegree = perceivedReplicationDegree;
        this.peersBackingUp = peersBackingUp;
        this.fileID = fileID;
    }


    public long getSize() {
        return size;
    }

    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }

    public int getPerceivedReplicationDegree(){
        return peersBackingUp.size();
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public boolean incrementPerceivedReplicationDegree(PeerStorage peerStorage, int userID) {
        for(int i = 0; i < this.peersBackingUp.size(); i++){
            if(this.peersBackingUp.get(i).compareTo(userID) == 0){
                return false;
            }
        }
        this.peersBackingUp.add(userID);
        peerStorage.WriteInfoToChunkData();
        return true;
    }

    public void decrementPerceivedReplicationDegree(PeerStorage peerStorage, int userID) {
        this.peersBackingUp.remove(userID);
        peerStorage.WriteInfoToChunkData();
    }

    public String getFileID(){ return fileID;}

    @Override
    public String toString() {
        String result = chunkNo + " " + size + " " + desiredReplicationDegree + " " + this.peersBackingUp.size();
        for(int i = 0; i < peersBackingUp.size(); i++){
            result += " " + peersBackingUp.get(i);
        }
        return result;
    }


}
