package com.company.dataStructures;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PeerStorage {
    public FileInfos infos;

    //Where you read/store files
    public final String PEER_FILES_DIR = "files/files/peer-";
    //Where you store chunks of a file
    public final String PEER_CHUNKS_DIR = "files/chunks/peer-";

    public PeerStorage(int peerID){
        try {
            createDirectory(peerID);
        }
        catch(Exception e){
            System.out.println("I/O machine broke at PeerStorage constructor");
            e.printStackTrace();
        }
    }

    public void createDirectory(int peerID) throws IOException {
        Path filesPath = Paths.get(PEER_FILES_DIR + peerID);
        if(!Files.exists(filesPath))
            Files.createDirectory(filesPath);

        Path chunksPath = Paths.get(PEER_CHUNKS_DIR + peerID);
        if(!Files.exists(chunksPath))
            Files.createDirectory(chunksPath);
    }

    public String getFilesDirectory(int peerID){
        return PEER_FILES_DIR + peerID;
    }

    public String getChunksDirectory(int peerID){
        return PEER_CHUNKS_DIR + peerID;
    }

}
