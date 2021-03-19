package com.company;

import java.io.*;

public class Main {


    public static void main(String[] args) throws IOException {

        System.out.println("hello!");
        byte[] chunk = new byte[64000];
	// write your code here
        FileInputStream objReader = new FileInputStream(new File("files/download.gif"));

        int numBytes = objReader.read(chunk, 0, 64000);
        File outputFile = new File("files/f.gif");
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
