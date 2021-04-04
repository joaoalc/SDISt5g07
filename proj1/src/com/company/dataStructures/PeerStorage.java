package com.company.dataStructures;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class PeerStorage {
    public FileInfos infos;
    public ChunkFileInfos chunkInfos;

    //Where you read/store files
    public final String PEER_FILES_DIR = "files/files/peer-";
    //Where you store chunks of a file
    public final String PEER_CHUNKS_DIR = "files/chunks/peer-";

    //Filenames that contain the info of every file/chunk
    public final String PEER_FILES_INFO_NAME = "fileInfo.txt";
    public final String PEER_CHUNKS_INFO_NAME = "chunkInfo.txt";

    public int peerID;

    public PeerStorage(int peerID){
        this.peerID = peerID;
        //TODO: Change later to add the info from the files file
        infos = new FileInfos();
        chunkInfos = new ChunkFileInfos();
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

    public void ReadInfoFromFileData() throws IOException {
        FileInputStream fileInfoInput = new FileInputStream(PEER_FILES_DIR + peerID + "/" + PEER_FILES_INFO_NAME);

        //Read string from file
        String filedata = "";
        int character = 0;
        while(character != -1){
            character = fileInfoInput.read();
            if(character != -1)
                filedata += String.valueOf((char) character);
        }


        infos = new FileInfos();
        Scanner scanner = new Scanner(filedata);
        int num_files = Integer.parseInt(scanner.nextLine());

        //Read and insert info from file string
        for(int i = 0; i < num_files; i++){
            FileInfo info = new FileInfo(scanner.nextLine(), scanner.nextLine());
            String fileID = scanner.nextLine();
            if(info.fileID.compareTo(fileID) != 0){
                System.out.println("File ID from file is incorrect, this is likely an error, so the id from the file will be ignored.");
            }
            int numChunks = Integer.parseInt(scanner.nextLine());
            for (int chunk = 0; chunk < numChunks; chunk++){
                info.usersBackingUp.add(new ArrayList<>());
                String chunkStr = scanner.nextLine();
                String[] arr = chunkStr.split(" ");
                if(Integer.parseInt(arr[0]) != chunk){
                    System.out.println("Chunk number " + chunk + " of file " + info.unencryptedFileID + " is wrong, ignoring it.");
                }
                for(int userID = 0; userID < Integer.parseInt(arr[1]); userID++){
                    info.usersBackingUp.get(chunk).add(arr[userID + 2]);
                }

            }

            infos.fileInfos.add(info);

            infos.printValuesHumanReadable();
            //FileInfo info = new FileInfo();
        }


    }

    public void WriteInfoToFileData(){
        File outputFile = new File(PEER_FILES_DIR + peerID + "/" + PEER_FILES_INFO_NAME);
        if(outputFile.exists() && !outputFile.isDirectory()){
            System.out.println("No file data found for this peer, creating new file.");

        }
        //TODO: Convert to toString method override in PeerStorage
        String result = infos.fileInfos.size() + "\n";
        for(FileInfo fInfo: infos.fileInfos){
            result += fInfo.filePath + "\n" + fInfo.unencryptedFileID + "\n" + fInfo.fileID + "\n" + fInfo.usersBackingUp.size() + "\n";
            for(int i = 0; i < fInfo.usersBackingUp.size(); i++){
                result += i + " " + fInfo.usersBackingUp.get(i).size();
                for(int j = 0; j < fInfo.usersBackingUp.get(i).size(); j++){
                    result += " " + fInfo.usersBackingUp.get(i).get(j);
                }
                result += "\n";
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

    public void ReadInfoFromChunkData() throws IOException {
        FileInputStream fileInfoInput = new FileInputStream(PEER_FILES_DIR + peerID + "/" + PEER_FILES_INFO_NAME);

        String filedata = "";
        int character = 0;
        while(character != -1){
            character = fileInfoInput.read();
            if(character != -1)
                filedata += String.valueOf((char) character);
        }


    }

    public void WriteInfoToChunkData(){
        File outputFile = new File(PEER_CHUNKS_DIR + peerID + "/" + PEER_CHUNKS_INFO_NAME);
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
