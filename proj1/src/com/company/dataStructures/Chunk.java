package com.company.dataStructures;

public class Chunk {
    private int chunkNo;
    private long size;
    private int desiredReplicationDegree;

    public Chunk(int chunkNo, long size, int desiredReplicationDegree) {
        this.size = size;
        this.desiredReplicationDegree = desiredReplicationDegree;
    }


    public long getSize() {
        return size;
    }

    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    @Override
    public String toString() {
        return chunkNo + " " + size + " " + desiredReplicationDegree;
    }
}
