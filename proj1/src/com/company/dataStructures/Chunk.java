package com.company.dataStructures;

import java.util.Comparator;

public class Chunk {
    private int chunkNo;
    private long size;
    private int desiredReplicationDegree;
    private int perceivedReplicationDegree;

    public Chunk(int chunkNo, long size, int desiredReplicationDegree, int perceivedReplicationDegree) {
        this.chunkNo = chunkNo;
        this.size = size;
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.perceivedReplicationDegree = perceivedReplicationDegree;
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

    @Override
    public String toString() {
        return chunkNo + " " + size + " " + desiredReplicationDegree + " " + perceivedReplicationDegree;
    }


}
