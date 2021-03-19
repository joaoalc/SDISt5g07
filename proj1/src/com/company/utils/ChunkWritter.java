package com.company.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChunkWritter {

    public static void WriteChunk(byte[] bytes, String destination){
        File outputFile = new File(destination);

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            outputStream.write(bytes);



        } catch (FileNotFoundException e) {
            System.out.println("Could not create file in selected directory.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
