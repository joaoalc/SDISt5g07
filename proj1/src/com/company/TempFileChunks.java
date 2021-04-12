package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TempFileChunks {

    HashMap<Integer, byte[]> chunks = new HashMap<>();
    public String fileID;
    int numChunks;
    File file;
    public TempFileChunks(int numChunks, String fileID, File file){
        this.numChunks = numChunks;
        this.fileID = fileID;
        this.file = file;
    }

    public void addChunk(byte[] chunk, int chunkNo){
        chunks.put(chunkNo, chunk);
        if(chunks.size() == numChunks){
            createFile();
        }
    }

    void createFile(){
        System.out.println("Creating file");
        try {
            FileOutputStream output = new FileOutputStream(file);
            for(int i = 0; i < numChunks; i++){
                output.write(chunks.get(i), 0, chunks.get(i).length);
            }
            System.out.println("File created");
        } catch (FileNotFoundException e) {
            System.out.println("Could not create file because there already is a file there.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
