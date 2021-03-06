package com.company;

import com.company.dataStructures.Chunk;
import com.company.dataStructures.FileInfo;
import com.company.dataStructures.FileInfos;
import com.company.dataStructures.PeerStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Peer implements IPeerRemote {

    public TempFileChunks restoreFileChunks;

    //ArrayList<FileInfo> fileInfos = new ArrayList<FileInfo>();

    // TODO: Fiz a parte de mudar o fileInfos pelo PeerStorage
    //FileInfos fileInfos = new FileInfos();

    private MulticastThread MC, MDB, MDR;

    public final String senderID;
    public PeerStorage peerStorage;

    public Peer(MulticastThread MC, MulticastThread MDB, MulticastThread MDR, String senderID, PeerStorage peerStorage) throws IOException {
        this.MC = MC;
        this.MDB = MDB;
        this.MDR = MDR;
        this.senderID = senderID;
        this.peerStorage = peerStorage;
        peerStorage.ReadInfoFromChunkData();
        peerStorage.ReadInfoFromFileData();
    }

    @Override
    public void backup(String path, int replication, String version) throws IOException{

        System.setProperty("file.encoding", "US-ASCII");
        File file = new File(path);
        if(!file.exists()){
            throw new FileNotFoundException("File was not found.");
        }
        if(!file.canRead()){
            throw new FileNotFoundException("File exists but could not be read.");
        }
        if(file.length() > (long) 64000 * 1000 * 1000){
            throw new FileNotFoundException("File is too large to be read (Max size: 64 billion bytes).");
        }

        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(file.lastModified()).toString();

        String owner = Files.getFileAttributeView(Paths.get(path), FileOwnerAttributeView.class).getOwner().toString();
        String unencryptedFileID = file.getName() + date + owner;
        //Add the file to the list of your files that have been backed up

        //FileInfo currentFileInfo = fileInfos.addFile(new FileInfo(path, unencryptedFileID));

        int numberOfChunks = (int) (file.length() / 64000) + 1;
        FileInfo currentFileInfo = this.peerStorage.infos.addFile(new FileInfo(path, unencryptedFileID, numberOfChunks));





        FileInputStream objReader = new FileInputStream(file);


        int numBytes = 64000;
        int chunkNo = 0;
        String fileID = currentFileInfo.fileID;

        while(numBytes == 64000) {
            //PUTCHUNK operation
            String headerString = version + " " + "PUTCHUNK" + " " + senderID + " " + fileID + " " + chunkNo + " " + replication;
            byte[] currentMessage = new byte[headerString.length() + 4 + 64000];

            System.arraycopy(headerString.getBytes(StandardCharsets.UTF_8), 0, currentMessage, 0, headerString.length());



            currentMessage[headerString.length()] = 0x0D;
            currentMessage[headerString.length() + 1] = 0x0A;
            currentMessage[headerString.length() + 2] = 0x0D;
            currentMessage[headerString.length() + 3] = 0x0A;

            //Fill the rest of the array containing the chunk with the file. Since the size of the array is fixed, we have to save the amount of bytes read to only send that through the multicast port
            numBytes = objReader.read(currentMessage, headerString.length() + 4, 64000);
            if(numBytes == -1){
                numBytes = 0;
            }

            currentFileInfo.addChunkToArray(chunkNo);
            //currentFileInfo.usersBackingUp.add(new ArrayList<>());
            chunkBackupProtocol(currentMessage, chunkNo, numBytes + 4 + headerString.length(), replication, path);


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chunkNo++;
        }

        this.peerStorage.WriteInfoToFileData();
        this.peerStorage.infos.printValuesHumanReadable();
        //this.peerStorage.WriteInfoToChunkData();
    }

    public void chunkBackupProtocol(byte[] message, int chunkNo, int bytesToSend, int replicationDeg, String filePath) throws IOException {
        for(int i = 0; i <= 4; i++) {
            /*System.out.println("Current replication degree: " + fileInfos.findByFilePath(filePath).usersBackingUp.get(chunkNo).size());
            if(fileInfos.findByFilePath(filePath).usersBackingUp.get(chunkNo).size() >= replicationDeg){
                break;
            }*/
            System.out.println("Current replication degree: " + this.peerStorage.infos.findByFilePath(filePath).usersBackingUp.get(chunkNo).size());
            if(this.peerStorage.infos.findByFilePath(filePath).usersBackingUp.get(chunkNo).size() >= replicationDeg){
                break;
            }
            MDB.sendMessage(message, bytesToSend);
            if(i < 4) {
                try {
                    System.out.println("Sleeping now for " + Math.pow(2, i) + " seconds.");
                    Thread.sleep(1000 * (int) (Math.pow(2, i)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void restore(String path, String version) throws IOException {
        // TODO: implement this

        FileInfo fileInfo = peerStorage.infos.findByFilePath(path);
        String fileID = fileInfo.fileID;

        //TODO: Replace numChunks with the number of chunks in the file
        restoreFileChunks = new TempFileChunks(18, fileID, new File(path));

        for(int chunkNo = 0; chunkNo < fileInfo.usersBackingUp.size(); chunkNo++) {
            String headerString = "1.0" + " " + "GETCHUNK" + " " + senderID + " " + fileID + " " + String.valueOf(chunkNo);

            byte[] message = new byte[headerString.length() + 4];
            System.arraycopy(headerString.getBytes(StandardCharsets.US_ASCII), 0, message, 0, headerString.length());

            message[headerString.length()] = 0x0D;
            message[headerString.length() + 1] = 0x0A;
            message[headerString.length() + 2] = 0x0D;
            message[headerString.length() + 3] = 0x0A;
            MC.sendMessage(message, message.length);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void delete(String path, String version) throws IOException, NoSuchAlgorithmException {
        // TODO: implement this
        System.setProperty("file.encoding", "US-ASCII");
        File file = new File(path);
        if(!file.exists()){
            throw new FileNotFoundException("File was not found.");
        }
        if(!file.canRead()){
            throw new FileNotFoundException("File exists but could not be read.");
        }
        if(file.length() > (long) 64000 * 1000 * 1000){
            throw new FileNotFoundException("File is too large to be read (Max size: 64 billion bytes).");
        }

        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(file.lastModified());

        String owner = Files.getFileAttributeView(Paths.get(path), FileOwnerAttributeView.class).getOwner().toString();
        String unencryptedFileID = file.getName() + date + owner;

        int numberOfChunks = (int) (file.length() / 64000) + 1;
        FileInfo fileInfo = new FileInfo(path, unencryptedFileID, numberOfChunks);
        String headerString = version + " " + "DELETE" + " " + senderID + " " + fileInfo.fileID;
        byte[] message = new byte[headerString.length() + 4];
        System.arraycopy(headerString.getBytes(StandardCharsets.UTF_8), 0, message, 0, headerString.length());

        message[headerString.length()] = 0x0D;
        message[headerString.length() + 1] = 0x0A;
        message[headerString.length() + 2] = 0x0D;
        message[headerString.length() + 3] = 0x0A;

        MDB.sendMessage(message, message.length);


        peerStorage.infos.fileInfos.remove(peerStorage.infos.findByFilePath(path));
        peerStorage.infos.printValuesHumanReadable();
        peerStorage.WriteInfoToFileData();




    }

    @Override
    public void reclaim(int space) throws RemoteException {
        // TODO: implement this
    }

    public static void main(String[] args) {

        String headerString = "1.0" + " " + "PUTCHUNK" + " " + "1" + " " + "200" + " " + "3" + " " + "7";
        byte[] currentMessage = new byte[headerString.length() + 4 + 64000];

        /*for (int i = 0; i < headerString.length(); i++) {
            currentMessage[i] = (byte) headerString.charAt(i);
        }*/
        System.arraycopy(headerString.getBytes(StandardCharsets.UTF_8), 0, currentMessage, 0, headerString.length());

        for (int i = 0; i < headerString.length(); i++) {
            System.out.println(currentMessage[i]);
        }


/*
        if (args.length != 9) {
            System.out.println("Usage: Peer <protocol_version> <peer_id> <acess_point> <mc_address> <mc_port> <mdb_address> <mdb_port> <mdr_address> <mdr_port>");
            System.exit(-1);
        }

        String protocolVersion = args[0];
        int peerID = Integer.parseInt(args[1]);
        String acessPoint = args[2];

        String MCAddress = args[3];
        int MCPort = Integer.parseInt(args[4]);

        String MDBAddress = args[5];
        int MDBPort = Integer.parseInt(args[6]);

        String MDRAddress = args[7];
        int MDRPort = Integer.parseInt(args[8]);

        try {
            Peer peer = new Peer(MCAddress, MCPort, MDBAddress, MDBPort, MDRAddress, MDRPort);
            IPeerRemote stub = (IPeerRemote) UnicastRemoteObject.exportObject(peer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(acessPoint, stub);

            System.out.println("Peer ready");
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }*/
    }

    public void addStoredPeer(String fileID, String userID, int chunkNo) {

        /*FileInfo a = fileInfos.findByFileID(fileID);
        if(a == null){
            System.out.println("No file found.");
            return;
        }
        fileInfos.findByFileID(fileID).addUser(userID, chunkNo);*/

        FileInfo a = this.peerStorage.infos.findByFileID(fileID);
        if(a == null){
            System.out.println("No file found.");
            return;
        }
        this.peerStorage.infos.findByFileID(fileID).addUser(userID, chunkNo);
        //this.peerStorage.chunkInfos.addChunk(fileID, new Chunk())
    }
}
