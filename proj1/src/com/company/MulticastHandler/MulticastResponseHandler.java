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
    String path = "files/meme";
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



        //Is this my own message?
        if(arguments.get(2).compareTo(senderID) != 0) {
            if (arguments.get(1).compareTo("PUTCHUNK") == 0) {
                System.out.println("Putchunk request.");

                byte[] body = MessageParser.getBody(request);
                System.out.println("Body size: " + body.length);
                ChunkWritter.WriteChunk(body, path + arguments.get(4));
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
                    //byte[] msg = (message).getBytes(StandardCharsets.UTF_8);
                    System.out.println(b2);
                    System.out.println(b2.length);
                    System.out.println(MC.getGroup());
                    System.out.println(MC.getInfo().getPort());

                    randomSleep(100, 400);

                    MC.getSocket().send(new DatagramPacket(b2, b2.length, MC.getGroup(), MC.getInfo().getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        else{
            System.out.println("Received own message");
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
