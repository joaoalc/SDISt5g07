package com.company.IOTest;

import com.company.utils.ChunkWritter;

import java.io.*;

public class FileCompilingTest {


    public static void main(String[] args) throws IOException {

        byte[] bytes = new byte[64000];
        File outputFile = new File("files/spook.gif");
        int i = 0;
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            while(i < 18) {
                FileInputStream objReader = new FileInputStream(new File("files/meme" + i));

                int numBytes = objReader.read(bytes, 0, 64000);


                outputStream.write(bytes);
                i++;
            }


        } catch (FileNotFoundException e) {
            System.out.println("Could not create file in selected directory.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
