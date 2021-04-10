package com.company;

import com.company.dataStructures.Chunk;
import com.company.dataStructures.FileInfo;
import com.company.dataStructures.FileInfos;
import com.company.dataStructures.PeerStorage;
import com.company.utils.StringVerification;

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

    private String protocolVersion;
    private MulticastThread MC, MDB, MDR;
    public final String senderID;
    public PeerStorage peerStorage;

    public Peer(String protocolVersion, MulticastThread MC, MulticastThread MDB, MulticastThread MDR, String senderID, PeerStorage peerStorage) throws IOException {
        this.protocolVersion = protocolVersion;
        this.MC = MC;
        this.MDB = MDB;
        this.MDR = MDR;
        this.senderID = senderID;
        this.peerStorage = peerStorage;
        peerStorage.ReadInfoFromChunkData();
        peerStorage.ReadInfoFromFileData();
    }

    @Override
    public void backup(String path, int replication) throws IOException{

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
            String headerString = protocolVersion + " " + "PUTCHUNK" + " " + senderID + " " + fileID + " " + chunkNo + " " + replication;
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
    public void restore(String path) throws IOException {
        // TODO: implement this

        FileInfo fileInfo = peerStorage.infos.findByFilePath(path);
        String fileID = fileInfo.fileID;

        //TODO: Replace numChunks with the number of chunks in the file
        restoreFileChunks = new TempFileChunks(18, fileID, new File(path));

        for(int chunkNo = 0; chunkNo < fileInfo.usersBackingUp.size(); chunkNo++) {
            String headerString = protocolVersion + " " + "GETCHUNK" + " " + senderID + " " + fileID + " " + String.valueOf(chunkNo);

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
    public void delete(String path) throws IOException {
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
        String headerString = protocolVersion + " " + "DELETE" + " " + senderID + " " + fileInfo.fileID;
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

    public static void printUsage() {
        System.out.println("Usage: Peer <protocol_version> <peer_id> <acess_point> <mc_address> <mc_port> <mdb_address> <mdb_port> <mdr_address> <mdr_port>");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 9) {
            printUsage();
            System.exit(-1);
        }

        String protocolVersion = args[0];
        if (!StringVerification.verifyVersion(protocolVersion)) {
            System.out.println("Invalid version: " + args[0]);
            System.exit(-1);
        }

        String senderID = args[1];
        /*int peerID = StringVerification.verifyPositiveInt(args[1]);
        if (peerID == -1) {
            System.out.println("Invalid peer id: " + args[1]);
            System.exit(-1);
        }*/
        String accessPoint = args[2];

        String MCAddress = args[3];
        if (!StringVerification.verifyIpAddress(MCAddress)) {
            System.out.println("Invalid ipAddress must be in (224.0.0.0 - 239.255.255.255): " + args[3]);
            System.exit(-1);
        }
        int MCPort = StringVerification.verifyPositiveInt(args[4]);
        if (MCPort == -1) {
            System.out.println("Invalid MC port: " + args[4]);
            System.exit(-1);
        }

        String MDBAddress = args[5];
        if (!StringVerification.verifyIpAddress(MDBAddress)) {
            System.out.println("Invalid ipAddress must be in (224.0.0.0 - 239.255.255.255): " + args[5]);
            System.exit(-1);
        }
        int MDBPort = StringVerification.verifyPositiveInt(args[6]);
        if (MDBPort == -1) {
            System.out.println("Invalid MDB port: " + args[6]);
            System.exit(-1);
        }

        String MDRAddress = args[7];
        if (!StringVerification.verifyIpAddress(MDRAddress)) {
            System.out.println("Invalid ipAddress must be in (224.0.0.0 - 239.255.255.255): " + args[7]);
            System.exit(-1);
        }
        int MDRPort = StringVerification.verifyPositiveInt(args[8]);
        if (MDRPort == -1) {
            System.out.println("Invalid MDR port: " + args[8]);
            System.exit(-1);
        }

        // Create Channels
        MulticastThread MC = new MulticastThread(MCAddress, MCPort, senderID, "MC");
        MulticastThread MDB = new MulticastThread(MDBAddress, MDBPort, senderID, "MDB");
        MulticastThread MDR = new MulticastThread(MDRAddress, MDRPort, senderID, "MDR");

        PeerStorage peerStorage = new PeerStorage(Integer.parseInt(senderID));

        try {
            Peer peer = new Peer(protocolVersion, MC, MDB, MDR, senderID, peerStorage);
            IPeerRemote stub = (IPeerRemote) UnicastRemoteObject.exportObject(peer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(accessPoint, stub);

            MC.setChannelSockets(MC, MDB, MDR);
            MDB.setChannelSockets(MC, MDB, MDR);
            MDR.setChannelSockets(MC, MDB, MDR);

            MC.setPeer(peer);
            MDB.setPeer(peer);
            MDR.setPeer(peer);

            MC.start();
            MDB.start();
            MDR.start();

            System.out.println("Peer ready");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
