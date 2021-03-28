package com.company;

import com.company.MulticastHandler.MulticastResponseHandler;
import com.company.testApplication.ClientInterface;
import com.company.utils.ChunkWritter;

import java.io.*;
import java.net.MulticastSocket;

public class Main {


    public static void main(String[] args) throws IOException {

        //MulticastThread mThread = new MulticastThread("230.0.0.0", 4446);

        //mThread.run();

        MulticastThread MC = new MulticastThread("230.0.0.0", 4446, args[0], "MC");
        MulticastThread MDB = new MulticastThread("230.0.0.1", 4446, args[0], "MDB");
        MulticastThread MDR = new MulticastThread("230.0.0.2", 4446, args[0], "MDR");

        Peer peer = new Peer(MC, MDB, MDR, args[0]);

        MC.setChannelSockets(MC, MDB, MDR);
        MDB.setChannelSockets(MC, MDB, MDR);
        MDR.setChannelSockets(MC, MDB, MDR);

        MC.setPeer(peer);
        MDB.setPeer(peer);
        MDR.setPeer(peer);

        MC.start();
        MDB.start();
        MDR.start();

        //MulticastDataBackup MDBThread = new MulticastDataBackup("230.0.0.0", 4446);

        //MDBThread.start();
        //ClientInterface cliInt = new ClientInterface();

        //cliInt.run();


        /*
        System.out.println("hello!");
        byte[] chunk = new byte[64000];
	// write your code here
        FileInputStream objReader = new FileInputStream(new File("files/spooky_month.gif"));

        int numBytes = objReader.read(chunk, 0, 64000);
        ChunkWritter.WriteChunk(chunk, "files/output");*/
        /*File outputFile = new File("files/f.gif");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(chunk);

            //numBytes = objReader.read(chunk, 0, 64000);
            //outputStream.write(chunk, 0, numBytes);
        }


        /*
        int numBytes = objReader.read(chunk, 0, 64000);
        System.out.println(numBytes);
        System.out.println(chunk.toString());
        System.out.println(chunk.toString());
        */

    }
}
