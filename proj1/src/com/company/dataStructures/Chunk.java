package com.company.dataStructures;

import java.util.Comparator;

public class Chunk {
    private int chunkNo;
    private long size;
    private int desiredReplicationDegree;
    private int perceivedReplicationDegree;
    private String fileID; //Already stored in ChunkFileInfos hashmap, but we need it here for faster acess

    public Chunk(int chunkNo, long size, int desiredReplicationDegree, int perceivedReplicationDegree, String fileID) {
        this.chunkNo = chunkNo;
        this.size = size;
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.perceivedReplicationDegree = perceivedReplicationDegree;
        this.fileID = fileID;
    }


    public long getSize() {
        return size;
    }

    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }

    public int getPerceivedReplicationDegree(){
        return perceivedReplicationDegree;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public void incrementPerceivedReplicationDegree() {
        this.perceivedReplicationDegree++;
    }

    public void decrementPerceivedReplicationDegree() {
        this.perceivedReplicationDegree--;
    }

    public String getFileID(){ return fileID;}

    @Override
    public String toString() {
        return chunkNo + " " + size + " " + desiredReplicationDegree + " " + perceivedReplicationDegree;
    }


}
