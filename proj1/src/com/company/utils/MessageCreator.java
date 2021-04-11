package com.company.utils;

import com.company.dataStructures.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MessageCreator {

    public static byte[] CreateChunkReclaimMessage(byte[] header, File file) throws IOException {
        byte[] result = new byte[(int) (header.length + file.length())];
        System.arraycopy(header, 0, result, 0, header.length);
        FileInputStream objReader = new FileInputStream(file);
        int numBytes = objReader.read(result, header.length, (int) file.length());
        if(numBytes != file.length()){
            System.out.println("File size different than file size?");
        }
        objReader.close();
        return result;
    }
}
