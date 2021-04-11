package com.company;

import com.company.dataStructures.*;
import com.company.utils.StringVerification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Peer implements IPeerRemote {

    public TempFileChunks restoreFileChunks;

    //ArrayList<FileInfo> fileInfos = new ArrayList<FileInfo>();

    // TODO: Fiz a parte de mudar o fileInfos pelo PeerStorage
    //FileInfos fileInfos = new FileInfos();

    private String protocolVersion;
    private MulticastThread MC, MDB, MDR;
    public final String senderID;
    public PeerStorage peerStorage;
    public ScheduledThreadPoolExecutor threadPool;

    public Peer(String protocolVersion, MulticastThread MC, MulticastThread MDB, MulticastThread MDR, String senderID, PeerStorage peerStorage, ScheduledThreadPoolExecutor threadPool) throws IOException {
        this.protocolVersion = protocolVersion;
        this.MC = MC;
        this.MDB = MDB;
        this.MDR = MDR;
        this.senderID = senderID;
        this.peerStorage = peerStorage;
        this.threadPool = threadPool;
        peerStorage.ReadInfoFromChunkData();
        peerStorage.ReadInfoFromFileData();
        System.setProperty("file.encoding", "US-ASCII");
    }

    @Override
    public void backup(String path, int replication) throws IOException {

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


        FileInfo currentFileInfo = new FileInfo(path, unencryptedFileID, numberOfChunks, replication);

        if(removeMultiples(currentFileInfo.fileID, path, replication)){
            System.out.println("There was an outdated version of this file, it has been deleted and replaced with the newest version.");
            return;
        }
        currentFileInfo = this.peerStorage.infos.addFile(currentFileInfo);

        FileInputStream objReader = new FileInputStream(file);

        int numBytes = 64000;
        int chunkNo = 0;
        String fileID = currentFileInfo.fileID;

        while(numBytes == 64000) {
            //PUTCHUNK operation
            String headerString = protocolVersion + " " + "PUTCHUNK" + " " + senderID + " " + fileID + " " + chunkNo + " " + replication;
            byte[] currentMessage = new byte[headerString.length() + 4 + 64000];

            System.arraycopy(headerString.getBytes(StandardCharsets.US_ASCII), 0, currentMessage, 0, headerString.length());



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
    }

    public void backupChunk(String fileID, int replication, int chunkNo, String version) throws IOException {
        if(version == "1.0"){
            File file = new File(peerStorage.getChunksDirectory(Integer.parseInt(senderID)) + "/" + fileID + "-" + chunkNo);
            if(!file.exists()){
                throw new FileNotFoundException("File was not found.");
            }
            if(!file.canRead()){
                throw new FileNotFoundException("File exists but could not be read.");
            }
            if(file.length() > (long) 64000 * 1000 * 1000){
                throw new FileNotFoundException("File is too large to be read (Max size: 64 billion bytes).");
            }

            FileInputStream objReader = new FileInputStream(file);
            String headerString = version + " " + "PUTCHUNK" + " " + senderID + " " + fileID + " " + chunkNo + " " + replication;
            System.out.println("Sending header string: " + headerString);
            byte[] currentMessage = new byte[headerString.length() + 4 + 64000];
            System.arraycopy(headerString.getBytes(StandardCharsets.US_ASCII), 0, currentMessage, 0, headerString.length());
            currentMessage[headerString.length()] = 0x0D;
            currentMessage[headerString.length() + 1] = 0x0A;
            currentMessage[headerString.length() + 2] = 0x0D;
            currentMessage[headerString.length() + 3] = 0x0A;

            //Fill the rest of the array containing the chunk with the file. Since the size of the array is fixed, we have to save the amount of bytes read to only send that through the multicast port
            int numBytes = objReader.read(currentMessage, headerString.length() + 4, 64000);
            if(numBytes == -1){
                numBytes = 0;
                return;
            }
            singleChunkBackupProtocol(currentMessage, chunkNo, numBytes + headerString.length() + 4, fileID);



        }

    }

    public void singleChunkBackupProtocol(byte[] message, int chunkNo, int bytesToSend, String fileID) throws IOException {
        for(int i = 0; i <= 4; i++) {
            if(this.peerStorage.chunkInfos.chunkInfos.get(fileID).chunks.get(chunkNo).getPerceivedReplicationDegree() >= this.peerStorage.chunkInfos.chunkInfos.get(fileID).chunks.get(chunkNo).getDesiredReplicationDegree()){
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

    public void chunkBackupProtocol(byte[] message, int chunkNo, int bytesToSend, int replicationDeg, String filePath) throws IOException {
        for(int i = 0; i <= 4; i++) {
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

        FileInfo fileInfo = peerStorage.infos.findByFilePath(path);
        if(fileInfo == null){
            System.out.println("File not found!");
            return;
        }
        String fileID = fileInfo.fileID;

        restoreFileChunks = new TempFileChunks(fileInfo.numberOfChunks, fileID, new File(path));

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


        File file = new File(path);
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(file.lastModified()).toString();

        String owner = Files.getFileAttributeView(Paths.get(path), FileOwnerAttributeView.class).getOwner().toString();
        String unencryptedFileID = file.getName() + date + owner;
        int numberOfChunks = (int) (file.length() / 64000) + 1;

        FileInfo fileInfoNew = new FileInfo(path, unencryptedFileID, numberOfChunks, fileInfo.desiredReplicationDegree);

        removeMultiples(fileInfoNew.fileID, path);

    }

    @Override
    public void delete(String path) throws IOException {
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

        FileInfo fileInfo = peerStorage.infos.findByFilePath(path);//new FileInfo(path, unencryptedFileID, numberOfChunks);
        if(fileInfo == null){
            System.out.println("File not found in metafile");
            return;
        }
        String headerString = protocolVersion + " " + "DELETE" + " " + senderID + " " + fileInfo.fileID;
/*=======
        FileInfo fileInfo = new FileInfo(path, unencryptedFileID, numberOfChunks);
        String headerString = protocolVersion + " " + "DELETE" + " " + senderID + " " + fileInfo.fileID;
>>>>>>> RMI*/
        byte[] message = new byte[headerString.length() + 4];
        System.arraycopy(headerString.getBytes(StandardCharsets.US_ASCII), 0, message, 0, headerString.length());

        message[headerString.length()] = 0x0D;
        message[headerString.length() + 1] = 0x0A;
        message[headerString.length() + 2] = 0x0D;
        message[headerString.length() + 3] = 0x0A;

        MC.sendMessage(message, message.length);

        peerStorage.infos.fileInfos.remove(peerStorage.infos.findByFilePath(path));
        peerStorage.infos.printValuesHumanReadable();
        peerStorage.WriteInfoToFileData();

    }

    @Override
    public void reclaim(long space) throws IOException {
        ArrayList<Chunk> chunks = new ArrayList<>();
        if (protocolVersion == "1.0"){
            peerStorage.total_space = space;
            long spaceOccupied = 0;
            for (Map.Entry<String, ChunkFileInfo> file : peerStorage.chunkInfos.chunkInfos.entrySet()) {
                for (Chunk chunk : file.getValue().chunks) {
                    spaceOccupied += chunk.getSize();
                    chunks.add(chunk);
                }
            }
            if (spaceOccupied <= space) {
                System.out.println("Size greater or equal to total file size, no need to remove a chunk.");
            } else {
                System.out.println("Chunks need to be removed.");
            }
            chunks.sort(new ChunkComparator());

            System.out.println("Number of chunks: " + chunks.size());
            if(space == 0){
                while(chunks.size() > 0){
                    System.out.println("Removing file");
                    Chunk removedChunk = chunks.remove(0);
                    String fileID = removedChunk.getFileID();
                    int chunkNo = removedChunk.getChunkNo();
                    spaceOccupied -= removedChunk.getSize();
                    peerStorage.chunkInfos.chunkInfos.get(fileID).removeChunk(chunkNo);
                    String headerString = protocolVersion + " " + "REMOVED" + " " + senderID + " " + fileID + " " + chunkNo;
                    byte[] message = new byte[headerString.length() + 4];
                    System.arraycopy(headerString.getBytes(StandardCharsets.US_ASCII), 0, message, 0, headerString.length());

                    message[headerString.length()] = 0x0D;
                    message[headerString.length() + 1] = 0x0A;
                    message[headerString.length() + 2] = 0x0D;
                    message[headerString.length() + 3] = 0x0A;
                    File file = new File(peerStorage.getChunksDirectory(Integer.parseInt(senderID)) + "/" + fileID + "-" + chunkNo);
                    Files.deleteIfExists(file.toPath());
                    peerStorage.WriteInfoToChunkData();
                    MC.sendMessage(message, message.length);
                    System.out.println("Number of chunks: " + chunks.size());
                }
            }

            while(spaceOccupied > space){
                System.out.println("Removing file");
                Chunk removedChunk = chunks.remove(0);
                String fileID = removedChunk.getFileID();
                int chunkNo = removedChunk.getChunkNo();
                spaceOccupied -= removedChunk.getSize();
                peerStorage.chunkInfos.chunkInfos.get(fileID).removeChunk(chunkNo);
                String headerString = protocolVersion + " " + "REMOVED" + " " + senderID + " " + fileID + " " + chunkNo;
                byte[] message = new byte[headerString.length() + 4];
                System.arraycopy(headerString.getBytes(StandardCharsets.US_ASCII), 0, message, 0, headerString.length());

                message[headerString.length()] = 0x0D;
                message[headerString.length() + 1] = 0x0A;
                message[headerString.length() + 2] = 0x0D;
                message[headerString.length() + 3] = 0x0A;
                File file = new File(peerStorage.getChunksDirectory(Integer.parseInt(senderID)) + "/" + fileID + "-" + chunkNo);
                Files.deleteIfExists(file.toPath());
                peerStorage.WriteInfoToChunkData();
                MC.sendMessage(message, message.length);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String state() throws RemoteException {
        return peerStorage.getState();
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

        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(16);

        try {
            Peer peer = new Peer(protocolVersion, MC, MDB, MDR, senderID, peerStorage, threadPool);
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

            /*MC.start();
            MDB.start();
            MDR.start();*/

            threadPool.execute(MC);
            threadPool.execute(MDB);
            threadPool.execute(MDR);

            System.out.println("Peer ready");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStoredPeer(String fileID, String userID, int chunkNo) {
        FileInfo a = this.peerStorage.infos.findByFileID(fileID);
        if(a == null){
            //System.out.println("No file found.");
            return;
        }
        this.peerStorage.infos.findByFileID(fileID).addUser(userID, chunkNo);
    }

    public boolean removeMultiples(String fileID, String filePath, int desiredReplicationDegree) throws IOException{
        FileInfo file = peerStorage.infos.findByFilePath(filePath);
        if(file == null){
            return false;
        }
        if(file.fileID != fileID){
            System.out.println("This file's backups are outdated! Deleting old backups and backing up again");
            delete(filePath);
            backup(filePath, desiredReplicationDegree);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean removeMultiples(String fileID, String filePath) throws IOException{
        FileInfo file = peerStorage.infos.findByFilePath(filePath);
        int desiredReplicationDegree = file.desiredReplicationDegree;
        if(file == null){
            return false;
        }
        if(file.fileID != fileID){
            System.out.println("This file's backups are outdated! Deleting old backups and backing up again");
            delete(filePath);
            backup(filePath, desiredReplicationDegree);
            return true;
        }
        else{
            return false;
        }
    }

}
