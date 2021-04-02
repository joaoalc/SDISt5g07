package com.company.dataStructures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PeerStorage {
    public FileInfos infos;

    //Where you read/store files
    public final String PEER_FILES_DIR = "files/files/peer-";
    //Where you store chunks of a file
    public final String PEER_CHUNKS_DIR = "files/chunks/peer-";

    //Filenames that contain the info of every file/chunk
    public final String PEER_FILES_INFO_NAME = "fileInfo.txt";
    public final String PEER_CHUNKS_INFO_NAME = "chunkInfo.txt";

    public PeerStorage(int peerID){
        //TODO: Change later to add the info from the files file
        infos = new FileInfos();
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

    public void WriteInfoToFileData(){
        File outputFile = new File(PEER_FILES_INFO_NAME);
        if(outputFile.exists() && !outputFile.isDirectory()){
            System.out.println("No file data found for this peer, creating new file.");

        }
        String result = infos.fileInfos.size() + "\n";
        for(FileInfo fInfo: infos.fileInfos){
            result += fInfo.unencryptedFileID + " " + fInfo.fileID + " " + fInfo.usersBackingUp.size() + "\n";
            for(int i = 0; i < fInfo.usersBackingUp.size(); i++){
                result += i + " " + fInfo.usersBackingUp.size() + "\n";
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            outputStream.write(result.getBytes(StandardCharsets.US_ASCII));



        } catch (FileNotFoundException e) {
            System.out.println("Could not create file in selected directory.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WriteChunkToFileData(){
        File outputFile = new File(PEER_CHUNKS_INFO_NAME);
        if(outputFile.exists() && !outputFile.isDirectory()){
            System.out.println("No file data found for this peer, creating new file.");

        }
        String result = infos.fileInfos.size() + "\n";
        for(FileInfo fInfo: infos.fileInfos){
            result += fInfo.fileID + " " + fInfo.usersBackingUp.size() + "\n";
            for(int i = 0; i < fInfo.usersBackingUp.size(); i++){
                result += i + "\n";
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            outputStream.write(result.getBytes(StandardCharsets.US_ASCII));



        } catch (FileNotFoundException e) {
            System.out.println("Could not create file in selected directory.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args) {
        PeerStorage storage = new PeerStorage(35);

        storage.infos.addFile(new FileInfo())
    }*/

}
