package com.company.dataStructures;

import java.util.Comparator;

public class ChunkComparator implements Comparator<Chunk> {
        @Override
        public int compare(Chunk a, Chunk b){
            int deltaReplicationDegreeA = a.getPerceivedReplicationDegree() - a.getDesiredReplicationDegree();
            int deltaReplicationDegreeB = b.getPerceivedReplicationDegree() - b.getDesiredReplicationDegree();
            if(deltaReplicationDegreeA > deltaReplicationDegreeB)
                return -1;
            else if(deltaReplicationDegreeA < deltaReplicationDegreeB)
                return 1;
            else if(a.getSize() > b.getSize())
                return -1;
            else if(a.getSize() < b.getSize())
                return 1;
            return 0;
        }
}
