package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main2 {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //MulticastPublisher mpub = new MulticastPublisher();
        //mpub.multicast("despacito lmao");

        MulticastThread MC = new MulticastThread("230.0.0.0", 4446, "5", "MC");
        MulticastThread MDB = new MulticastThread("230.0.0.1", 4446, "5", "MDB");
        MulticastThread MDR = new MulticastThread("230.0.0.2", 4446, "5", "MDR");


        Peer peer = new Peer(MC, MDB, MDR, "5");

        MC.setChannelSockets(MC, MDB, MDR);
        MDB.setChannelSockets(MC, MDB, MDR);
        MDR.setChannelSockets(MC, MDB, MDR);

        MC.setPeer(peer);
        MDB.setPeer(peer);
        MDR.setPeer(peer);


        MC.start();
        MDB.start();
        MDR.start();

        peer.backup("files/spooky_month.gif", 2, "1.0");
        /*
        String message = "1.0 " + "PUTCHUNK " + "12312312312312312312312312312312 " + args[0] + " 0 " + "1";

        byte[] bytes = new byte[message.length() + 4 + 64000];
        bytes[message.length()] = 0x0D;
        bytes[message.length() + 1] = 0x0A;
        bytes[message.length() + 2] = 0x0D;
        bytes[message.length() + 3] = 0x0A;

        for(int i = 0; i < message.length(); i++) {
            bytes[i] = (byte) message.charAt(i);
        }

        for(int i = 0; i < message.length() + 4; i++){
            System.out.print(i + "->" + bytes[i] + " ");
        }
        System.out.println("");
        FileInputStream objReader = new FileInputStream(new File("files/spooky_month.gif"));

        int numBytes = objReader.read(bytes, message.length() + 4, 64000);

        for(int i = 0; i < message.length() + 4; i++){
            System.out.print(i + "->" + bytes[i] + " ");
        }

        System.out.println("");

        MulticastThread MC = new MulticastThread("230.0.0.0", 4446, "12312312312312312312312312312312");
        MulticastThread MDB = new MulticastThread("230.0.0.1", 4446, "12312312312312312312312312312312");
        MulticastThread MDR = new MulticastThread("230.0.0.2", 4446, "12312312312312312312312312312312");

        MC.start();
        MDB.start();
        MDR.start();

        MDB.sendMessage(bytes);*/

        /*
        MulticastPublisher mpub = new MulticastPublisher();
        mpub.sendBackupHeader(bytes);*/
    }
}
