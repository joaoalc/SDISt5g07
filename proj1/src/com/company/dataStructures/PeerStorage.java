package com.company.dataStructures;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class PeerStorage {
    public FileInfos infos;
    public ChunkFileInfos chunkInfos;
    public long total_space;

    public final long DEFAULT_TOTAL_SPACE = 10000000;

    //Where you read/store files
    public final String PEER_FILES_DIR = "files/files/peer-"; //Inner paths are hard coded in a function; DO NOT CHANGE
    //Where you store chunks of a file
    public final String PEER_CHUNKS_DIR = "files/chunks/peer-"; //Inner paths are hard coded in a function; DO NOT CHANGE

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
        Path generalPath = Paths.get("files");
        if(!Files.exists(generalPath)){
            Files.createDirectory(generalPath);
        }
        Path filesPath = Paths.get("files/files");
        if(!Files.exists(filesPath)){
            Files.createDirectory(filesPath);
        }
        Path chunksPath = Paths.get("files/chunks");
        if(!Files.exists(chunksPath)) {
            Files.createDirectory(chunksPath);
        }
        Path peerFilesPath = Paths.get(PEER_FILES_DIR + peerID);
        if(!Files.exists(peerFilesPath))
            Files.createDirectory(peerFilesPath);

        Path peerChunksPath = Paths.get(PEER_CHUNKS_DIR + peerID);
        if(!Files.exists(peerChunksPath))
            Files.createDirectory(peerChunksPath);
    }



    public String getFilesDirectory(int peerID){
        return PEER_FILES_DIR + peerID;
    }

    public String getChunksDirectory(int peerID){
        return PEER_CHUNKS_DIR + peerID;
    }

    public void ReadInfoFromFileData() throws IOException {
        File file = new File(PEER_FILES_DIR + peerID + "/" + PEER_FILES_INFO_NAME);
        FileInputStream fileInfoInput;
        if(file.exists() && !file.isDirectory()) {
            // do something
            fileInfoInput = new FileInputStream(PEER_FILES_DIR + peerID + "/" + PEER_FILES_INFO_NAME);
        }
        else{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(("0 " + DEFAULT_TOTAL_SPACE).getBytes(StandardCharsets.US_ASCII));
            return;
        }

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
            String filePath = scanner.nextLine();
            String unencryptedFileID = scanner.nextLine();
            String fileID = scanner.nextLine();
            String numChunksReplicationDegreeLine = scanner.nextLine();
            String[] args = numChunksReplicationDegreeLine.split(" ");
            int numberOfChunks = Integer.parseInt(args[0]);//Integer.parseInt(scanner.nextLine());
            int desiredReplication = Integer.parseInt(args[1]);
            FileInfo info = new FileInfo(filePath, unencryptedFileID, numberOfChunks, desiredReplication);
            if(info.fileID.compareTo(fileID) != 0){
                System.out.println("File ID from file is incorrect, this is likely an error, so the id from the file will be ignored.");
            }
            for (int chunk = 0; chunk < numberOfChunks; chunk++){
                info.usersBackingUp.add(new ArrayList<>());
                if(!scanner.hasNextLine()){
                    break;
                }
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

            //infos.printValuesHumanReadable();
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
            result += fInfo.filePath + "\n" + fInfo.unencryptedFileID + "\n" + fInfo.fileID + "\n" + fInfo.numberOfChunks + " " + fInfo.desiredReplicationDegree + "\n";
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
        File file = new File(PEER_CHUNKS_DIR + peerID + "/" + PEER_CHUNKS_INFO_NAME);
        FileInputStream fileInfoInput;
        if(file.exists() && !file.isDirectory()) {
            // do something
            fileInfoInput = new FileInputStream(PEER_CHUNKS_DIR + peerID + "/" + PEER_CHUNKS_INFO_NAME);
        }
        else{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write("0 ".getBytes(StandardCharsets.US_ASCII));
            return;
        }

        String filedata = "";
        int character = 0;
        while(character != -1){
            character = fileInfoInput.read();
            if(character != -1)
                filedata += String.valueOf((char) character);
        }

        chunkInfos = new ChunkFileInfos();
        Scanner scanner = new Scanner(filedata);

        String firstLine = scanner.nextLine();
        String[] firstLineArgs = firstLine.split(" ");
        int num_files = Integer.parseInt(firstLineArgs[0]);
        total_space = Long.parseLong(firstLineArgs[1]);

        for(int i = 0; i < num_files; i++){
            String line = scanner.nextLine();
            String[] args = line.split(" ");
            ChunkFileInfo info = new ChunkFileInfo();
            chunkInfos.chunkInfos.put(args[0], info);
            for(int j = 0; j < Integer.parseInt(args[1]); j++){
                String line2 = scanner.nextLine();
                String[] args2 = line2.split(" ");
                info.chunks.add(new Chunk(Integer.parseInt(args2[0]), Integer.parseInt(args2[1]), Integer.parseInt(args2[2]), Integer.parseInt(args2[3]), args[0]));
            }
        }
        chunkInfos.printValuesHumanReadable();

    }

    public void WriteInfoToChunkData(){
        File outputFile = new File(PEER_CHUNKS_DIR + peerID + "/" + PEER_CHUNKS_INFO_NAME);
        if(outputFile.exists() && !outputFile.isDirectory()){
            System.out.println("No file data found for this peer, creating new file.");

        }
        String result = chunkInfos.chunkInfos.size() + " " + total_space + "\n";
        int fileNum = 0;
        for(Map.Entry<String, ChunkFileInfo> set: chunkInfos.chunkInfos.entrySet()){
            //(File path of when this version of the file was inserted, file could've been manually moved or overwritte, in which case it would not be there)
            result += set.getKey() + " " + set.getValue().chunks.size() + "\n";
            /*for(int i = 0; i < set.getValue().chunks.size(); i++) {
                result += i + "\n";
            }*/

            for (Chunk chunk : set.getValue().chunks) {
                result += chunk.toString() + "\n";
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

    public long GetOccupiedSpace() {
        long result = 0;
        for(Map.Entry<String, ChunkFileInfo> entry:  chunkInfos.chunkInfos.entrySet()){
            for(Chunk chunk: entry.getValue().chunks){
                result += chunk.getSize();
            }
        }
        return result;
    }

    /*public static void main(String[] args) {
        PeerStorage storage = new PeerStorage(35);

        storage.infos.addFile(new FileInfo())
    }*/

}
