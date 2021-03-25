package com.company.MulticastHandler;

import com.company.MulticastThread;
import com.company.utils.ChunkWritter;
import com.company.utils.MessageParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class MulticastResponseHandler extends Thread{
    //ArrayList<byte[]> requestStrings = new ArrayList<>();

    //ArrayList<byte[]> messageStrings = new ArrayList<>();

    String path = "out/meme.jpg";

    String senderID;

    byte[] request;

    MulticastThread MC;
    MulticastThread MDB;
    MulticastThread MDR;



    public MulticastResponseHandler(String senderID, byte[] request, MulticastThread MC, MulticastThread MDB, MulticastThread MDR){
        super();
        this.senderID = senderID;
        this.request = request;
        this.MC = MC;
        this.MDB = MDB;
        this.MDR = MDR;
    }
    /*
    public void AddMessage(byte[] message){
        System.out.println("Request has been added");
        requestStrings.add(message);
    }*/

    public void run() {


        ArrayList<String> arguments = MessageParser.getFirstLineArguments(new String(request));
        if(arguments.get(2) != senderID) {
            if (arguments.get(1).compareTo("PUTCHUNK") == 0) {
                System.out.println("Putchunk request.");

                byte[] body = MessageParser.getBody(request);
                ChunkWritter.WriteChunk(body, path);
                Random r = new Random();
                int sleep_milliseconds =  r.nextInt((400 - 100) + 1) + 100;
                System.out.println("Sleeping now for " + sleep_milliseconds + " milliseconds");
                try {
                    Thread.sleep(sleep_milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    byte[] b1 = msgNoEndLine.getBytes(StandardCharsets.UTF_8);
                    byte[] b2 = baos.toByteArray();
                    for(int i = 0; i < b1.length; i++){
                        System.out.print(b1[i]);
                    }
                    System.out.println();
                    for(int i = 0; i < b2.length; i++){
                        System.out.print(b2[i]);
                    }
                    System.out.println();
                    //byte[] msg = (message).getBytes(StandardCharsets.UTF_8);
                    MC.getSocket().send(new DatagramPacket(b2, b2.length, MC.getGroup(), MC.getInfo().getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        Random ra = new Random();
        int sleep_millisecond =  ra.nextInt((400 - 100) + 1) + 100;
        try {
            Thread.sleep(sleep_millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /*
        System.out.println("Running now!");
        while(true){
            Random ra = new Random();
            int sleep_millisecond =  ra.nextInt((400 - 100) + 1) + 100;
            try {
                Thread.sleep(sleep_millisecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(!requestStrings.isEmpty()){
                System.out.println("Got a request!");
                byte[] currentRequest = requestStrings.get(requestStrings.size() - 1);

                ArrayList<String> arguments = MessageParser.getFirstLineArguments(new String(currentRequest));

                for(int i = 0; i < arguments.size(); i++){
                    System.out.println(arguments.get(i));
                }
                if(arguments.get(2) != senderID) {
                    if (arguments.get(1).compareTo("PUTCHUNK") == 0) {
                        System.out.println("Putchunk request.");

                        byte[] body = MessageParser.getBody(currentRequest);
                        ChunkWritter.WriteChunk(body, path);
                        Random r = new Random();
                        int sleep_milliseconds =  r.nextInt((400 - 100) + 1) + 100;
                        System.out.println("Sleeping now for " + sleep_milliseconds + " milliseconds");
                        try {
                            Thread.sleep(sleep_milliseconds);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Sending now!");
                        try {
                            MCSender.multicast(arguments.get(0) + " STORED " + senderID + " " + arguments.get(3) + " " + arguments.get(4) + "\n\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    System.out.println("Ignoring own message!");
                }
                requestStrings.remove(currentRequest);
            }*/

        }
    }


}