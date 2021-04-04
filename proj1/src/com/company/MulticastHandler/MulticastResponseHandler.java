package com.company.MulticastHandler;

import com.company.MulticastThread;
import com.company.Peer;
import com.company.dataStructures.PeerStorage;
import com.company.utils.ChunkWritter;
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

    public MulticastResponseHandler(String senderID, byte[] request, MulticastThread MC, MulticastThread MDB, MulticastThread MDR, PeerStorage peerStorage){
        super();
        this.senderID = senderID;
        this.request = request;
        this.MC = MC;
        this.MDB = MDB;
        this.MDR = MDR;
        this.peerStorage = peerStorage;
        System.setProperty("file.encoding", "US-ASCII");

        System.out.println("The charset used is :" + System.getProperty("file.encoding"));
        path = MC.peer.peerStorage.getChunksDirectory(Integer.parseInt(senderID));
    }

    public void run() {


        ArrayList<String> arguments = MessageParser.getFirstLineArguments(new String(request));



        //Is this my own message?
        if(arguments.get(2).compareTo(senderID) != 0) {
            if (arguments.get(1).compareTo("PUTCHUNK") == 0) {
                System.out.println("Putchunk request.");

                byte[] body = MessageParser.getBody(request);
                System.out.println("Body size: " + body.length);
                ChunkWritter.WriteChunk(body, path + "/" + arguments.get(3) + "-" + arguments.get(4));
                peerStorage.chunkInfos.AddChunk(arguments.get(3), Integer.parseInt(arguments.get(4)));
                peerStorage.WriteInfoToChunkData();
                /*Random r = new Random();
                int sleep_milliseconds =  r.nextInt((400 - 100) + 1) + 100;
                System.out.println("Sleeping now for " + sleep_milliseconds + " milliseconds");
                try {
                    Thread.sleep(sleep_milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
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
                    //byte[] b1 = msgNoEndLine.getBytes(StandardCharsets.UTF_8);
                    byte[] b2 = baos.toByteArray();

                    randomSleep(100, 400);

                    MC.getSocket().send(new DatagramPacket(b2, b2.length, MC.getGroup(), MC.getInfo().getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(arguments.get(1).compareTo("STORED") == 0){
                //Version STORED SenderID FileID ChunkNo
                MC.peer.addStoredPeer(arguments.get(3), arguments.get(2), Integer.parseInt(arguments.get(4)));
            }
            else if(arguments.get(1).compareTo("DELETE") == 0){
                int currentChunk = 0;
                try {
                    while(true) {
                        System.out.println(path + "/" + arguments.get(3) + "-" + currentChunk);
                        File file = new File(path + arguments.get(3) + "-" + currentChunk);
                        if(!(file.exists() && !file.isDirectory())){
                            break;
                        }
                        Files.deleteIfExists(file.toPath());
                        currentChunk++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
