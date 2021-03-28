package com.company;

import com.company.dataStructures.FileInfo;
import com.company.dataStructures.FileInfos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Peer implements IPeerRemote {


    //ArrayList<FileInfo> fileInfos = new ArrayList<FileInfo>();
    FileInfos fileInfos = new FileInfos();

    private MulticastThread MC, MDB, MDR;

    public String senderID;

    public Peer(MulticastThread MC, MulticastThread MDB, MulticastThread MDR, String senderID) {
        this.MC = MC;
        this.MDB = MDB;
        this.MDR = MDR;
        this.senderID = senderID;
    }

    @Override
    public void backup(String path, int replication, String version) throws IOException, NoSuchAlgorithmException {
        //Add the file to the list of your files that have been backed up
        FileInfo currentFileInfo = fileInfos.addFile(new FileInfo(path));

        System.setProperty("file.encoding", "US-ASCII");

        System.out.println("The charset used is :" + System.getProperty("file.encoding"));
        // Get the default charset of the machine
        // TODO: implement this
        File file = new File(path);
        if(!file.exists()){
            throw new FileNotFoundException("File was not found.");
        }
        if(!file.canRead()){
            throw new FileNotFoundException("File exists but could not be read.");
        }
        System.out.println(file.length());
        if(file.length() > (long) 64000 * 1000 * 1000){
            throw new FileNotFoundException("File is too large to be read (Max size: 64 billion bytes).");
        }

        //TODO: Repeat one more time if size is multiple of 64000
        FileInputStream objReader = new FileInputStream(file);


        int numBytes = 64000;
        int chunkNo = 0;
        String fileID = currentFileInfo.fileID;

        while(numBytes == 64000) {
            //Create byte array
            String headerString = version + " " + "PUTCHUNK" + " " + senderID + " " + fileID + " " + chunkNo + " " + replication;
            //byte[] header = headerString.getBytes(StandardCharsets.UTF_8);
            byte[] currentMessage = new byte[headerString.length() + 4 + 64000];

            for (int i = 0; i < headerString.length(); i++) {
                currentMessage[i] = (byte) headerString.charAt(i);
            }
            currentMessage[headerString.length()] = 0x0D;
            currentMessage[headerString.length() + 1] = 0x0A;
            currentMessage[headerString.length() + 2] = 0x0D;
            currentMessage[headerString.length() + 3] = 0x0A;

            numBytes = objReader.read(currentMessage, headerString.length() + 4, 64000);
            if(numBytes == -1){
                numBytes = 0;
            }
            /*for(int i = 0; i < numBytes + 4 + headerString.length()){

            }*/
            FileInfo fileInfo = new FileInfo(path);
            fileInfos.addFile(fileInfo);
            System.out.println(fileInfo.usersBackingUp.size());
            System.out.println(fileInfos.findByFilePath(path).usersBackingUp.size());

            currentFileInfo.usersBackingUp.add(new ArrayList<>());
            chunkBackupProtocol(currentMessage, chunkNo, numBytes + 4 + headerString.length(), replication, path);
            //MDB.sendMessage(currentMessage, numBytes + 4 + headerString.length());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chunkNo++;
        }



        //String message = "1.0 " + "PUTCHUNK " + "12312312312312312312312312312312 " + args[0] + " 0 " + "1";
        //backupChunk()
    }

    public void chunkBackupProtocol(byte[] message, int chunkNo, int bytesToSend, int replicationDeg, String filePath) throws IOException {
        for(int i = 0; i <= 4; i++) {
            /*if(fileInfos.findByFilePath(filePath).usersBackingUp.size() == chunkNo){
                fileInfos.findByFilePath(filePath).usersBackingUp.add(new ArrayList<>());
            }*/
            System.out.println("Current replication degree: " + fileInfos.findByFilePath(filePath).usersBackingUp.get(chunkNo).size());
            if(fileInfos.findByFilePath(filePath).usersBackingUp.get(chunkNo).size() >= replicationDeg){
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
    public void restore(String path) throws RemoteException {
        // TODO: implement this
    }

    @Override
    public void delete(String path) throws RemoteException {
        // TODO: implement this
    }

    @Override
    public void reclaim(int space) throws RemoteException {
        // TODO: implement this
    }

    public static void main(String[] args) {
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
        //fileInfos.addFile(new FileInfo("", fileID));
        FileInfo a = fileInfos.findByFileID(fileID);
        if(a == null){
            System.out.println("No file found.");
            return;
        }
        fileInfos.findByFileID(fileID).addUser(userID, chunkNo);
    }
}
