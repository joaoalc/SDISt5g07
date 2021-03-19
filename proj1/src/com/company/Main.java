package com.company;

import com.company.testApplication.ClientInterface;
import com.company.utils.ChunkWritter;

import java.io.*;

public class Main {


    public static void main(String[] args) throws IOException {

        MulticastThread mThread = new MulticastThread();

        mThread.run();

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
