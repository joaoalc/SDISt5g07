package com.company.MulticastHandler;

import com.company.Message;
import com.company.MulticastThread;
import com.company.Peer;
import com.company.dataStructures.Chunk;
import com.company.dataStructures.ChunkFileInfo;
import com.company.dataStructures.ChunkFileInfos;
import com.company.dataStructures.PeerStorage;
import com.company.utils.ChunkWritter;
import com.company.utils.MessageCreator;
import com.company.utils.MessageParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

public class MulticastResponseHandler extends Thread{
    String path;
    String senderID;
    byte[] request;

    MulticastThread MC;
    MulticastThread MDB;
    MulticastThread MDR;

    PeerStorage peerStorage;

    String callerChannelType;

    RestoreInfo restoreInfo = null;

    public MulticastResponseHandler(String senderID, byte[] request, MulticastThread MC, MulticastThread MDB, MulticastThread MDR, PeerStorage peerStorage, String callerChannelType){
        super();
        this.senderID = senderID;
        this.request = request;
        this.MC = MC;
        this.MDB = MDB;
        this.MDR = MDR;
        this.peerStorage = peerStorage;
        this.callerChannelType = callerChannelType;
        System.setProperty("file.encoding", "US-ASCII");
        path = MC.peer.peerStorage.getChunksDirectory(Integer.parseInt(senderID));
    }

    public void run() {


        ArrayList<String> arguments = MessageParser.getFirstLineArguments(new String(request));



        //Is this my own message?
        if(arguments.get(2).compareTo(senderID) != 0) {
            if (arguments.get(1).compareTo("PUTCHUNK") == 0 && this.callerChannelType == "MDB") {
                //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg>

                //Find if someone else has received the REMOVE command recently
                RemoveInfo rInfo = MC.removePeers.get(arguments.get(3) + "-" + arguments.get(4));
                if(rInfo != null){
                    rInfo.repeat = true;
                    System.out.println("No other thread will try to backup a chunk now.");
                }

                //Cannot back up own message
                if(peerStorage.infos.findByFileID(arguments.get(3)) == null){
                    System.out.println("This peer owns the file; not backing it up.");
                    return;
                }

                System.out.println("Putchunk request.");

                byte[] body = MessageParser.getBody(request);
                System.out.println("Body size: " + body.length);
                ChunkWritter.WriteChunk(body, path + "/" + arguments.get(3) + "-" + arguments.get(4));
                peerStorage.chunkInfos.addChunk(arguments.get(3), new Chunk(Integer.parseInt(arguments.get(4)), body.length, Integer.parseInt(arguments.get(5)), 1, arguments.get(3)));
                peerStorage.WriteInfoToChunkData();
                System.out.println("Sending now!");
                try {
                    String msgNoEndLine = arguments.get(0) + " STORED " + senderID + " " + arguments.get(3) + " " + arguments.get(4);

                    byte eol1 = 0x0D;
                    byte eol2 = 0x0A;
                    String message = msgNoEndLine;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(message.getBytes(StandardCharsets.UTF_8));
                    byte[] arr = {0x0D, 0x0A, 0x0D, 0x0A};
                    baos.write(arr);
                    byte[] b2 = baos.toByteArray();

                    randomSleep(100, 400);

                    MC.getSocket().send(new DatagramPacket(b2, b2.length, MC.getGroup(), MC.getInfo().getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(arguments.get(1).compareTo("STORED") == 0 && this.callerChannelType == "MC"){
                //Version STORED SenderID FileID ChunkNo
                MC.peer.addStoredPeer(arguments.get(3), arguments.get(2), Integer.parseInt(arguments.get(4)));
                peerStorage.chunkInfos.incrementChunkPerceivedReplicationDegree(arguments.get(3), Integer.parseInt(arguments.get(4)), peerStorage);
            }
            else if(arguments.get(1).compareTo("DELETE") == 0 && this.callerChannelType == "MC"){
                try {
                    ChunkFileInfo info = peerStorage.chunkInfos.chunkInfos.get(arguments.get(3));
                    if(info != null) {
                        for (Chunk chunk : info.chunks) {
                            File file = new File(path + "/" + arguments.get(3) + "-" + chunk.getChunkNo());
                            Files.deleteIfExists(file.toPath());
                        }
                    }
                    peerStorage.chunkInfos.chunkInfos.remove(arguments.get(3));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                peerStorage.WriteInfoToChunkData();
            }
            else if(arguments.get(1).compareTo("GETCHUNK") == 0 && this.callerChannelType == "MC"){
                //<Version> GETCHUNK <SenderID> <FileID> <ChunkNo>
                ChunkFileInfo info = peerStorage.chunkInfos.chunkInfos.get(arguments.get(3));
                if (info != null) {

                    //Add to file queue
                    MC.restorePeers.put(arguments.get(3) + "-" + arguments.get(4), this);
                    restoreInfo = new RestoreInfo(arguments.get(3), arguments.get(4));


                    if(info.chunks.get(Integer.parseInt(arguments.get(4))) != null){
                        String response = "1.0" + " " + "CHUNK" + " " + senderID + " " + arguments.get(3) + " " + arguments.get(4);
                        byte[] header = new byte[response.length() + 4];
                        System.arraycopy(response.getBytes(StandardCharsets.US_ASCII), 0, header, 0, response.length());


                        header[response.length()] = 0x0D;
                        header[response.length() + 1] = 0x0A;
                        header[response.length() + 2] = 0x0D;
                        header[response.length() + 3] = 0x0A;

                        /*
                        System.out.print("Header: ");
                        for(int i = 0; i < header.length; i++){
                            System.out.print(header[i]);
                        }
                        System.out.println("");*/


                        File file = new File(path + "/" + arguments.get(3) + "-" + arguments.get(4));

                        //TODO: IMPORTANT - MESSAGE SHOULD NOT BE SENT IF ANOTHER PEER HAS SENT THE CHUNK ALREADY IN THE MEANTIME OF THE SLEEP
                        randomSleep(100, 400);

                        if(!restoreInfo.repeat) {

                            byte[] message;
                            try {
                                message = MessageCreator.CreateChunkReclaimMessage(header, file);
                                MDR.getSocket().send(new DatagramPacket(message, message.length, MDR.getGroup(), MDR.getInfo().getPort()));
                            } catch (IOException e) {
                                System.out.println("Could not create chunk reclaim message");
                                e.printStackTrace();
                            }
                        }
                        else{
                            System.out.println("Not sending repeat message.");
                        }


                    }
                }
            }
            else if(arguments.get(1).compareTo("CHUNK") == 0 && this.callerChannelType == "MDR") {
                //Version CHUNK SenderID FileID ChunkNO
                MulticastResponseHandler MRH = MC.restorePeers.get(arguments.get(3) + "-" + arguments.get(4));
                if(MRH != null){
                    //MC.restorePeers.put(arguments.get(3) + "-" + arguments.get(4), this);
                    System.out.println("Stopping other thread from sending CHUNK response");
                    MRH.restoreInfo.repeat = true;
                }

                if(MC.peer.restoreFileChunks != null){
                    if(arguments.get(3).compareTo(MC.peer.restoreFileChunks.fileID) == 0){
                        byte[] body = MessageParser.getBody(request);
                        MC.peer.restoreFileChunks.addChunk(body, Integer.parseInt(arguments.get(4)));
                    }
                }
            }
            else if(arguments.get(1).compareTo("REMOVED") == 0 && this.callerChannelType == "MC"){
                //<Version> REMOVED <SenderId> <FileId> <ChunkNo>
                ChunkFileInfos CFIS = MC.peer.peerStorage.chunkInfos;
                ChunkFileInfo CFI = CFIS.chunkInfos.get(arguments.get(3));
                if(CFI != null){
                    Chunk chunk = CFI.chunks.get(Integer.parseInt(arguments.get(4)));
                    if(chunk != null){
                        chunk.decrementPerceivedReplicationDegree(MC.peer.peerStorage);
                        if(chunk.getPerceivedReplicationDegree() == 0){
                            chunk.incrementPerceivedReplicationDegree(MC.peer.peerStorage);
                        }
                        if(chunk.getPerceivedReplicationDegree() < chunk.getDesiredReplicationDegree()){
                            RemoveInfo removeInfo = new RemoveInfo(arguments.get(3), arguments.get(4), this);
                            MC.removePeers.put(arguments.get(3) + "-" + arguments.get(4), removeInfo);

                            randomSleep(100, 400);
                            //Interrupt other processes trying to backup this chunk
                            RemoveInfo rInfo = MC.removePeers.get(arguments.get(3) + "-" + arguments.get(4));
                            if(rInfo != null){
                                if(rInfo.repeat == true){
                                    System.out.println("Another chunk is already going to be backed up.");
                                    return;
                                }
                            }
                            try {
                                MDB.peer.backupChunk(arguments.get(3), chunk.getDesiredReplicationDegree(), Integer.parseInt(arguments.get(4)), "1.0");
                                System.out.println("Sent putchunk message now!");
                                MC.removePeers.remove(arguments.get(3) + "-" + arguments.get(2));
                            } catch (IOException e) {
                                System.out.println("Could not execute chunk backup.");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        else{
            //System.out.println("Received own message");
        }
    }

    void randomSleep(int min, int max){
        Random ra = new Random();
        int sleep_millisecond =  ra.nextInt((max - min) + 1) + min;
        try {
            Thread.sleep(sleep_millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
