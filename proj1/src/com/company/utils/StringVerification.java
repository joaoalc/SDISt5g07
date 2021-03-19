package com.company.utils;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class StringVerification {

    public static int verifyPositiveIntRange(String string, int min, int max) {
        if (string.isEmpty()) {
            return -1;
        }
        int number;
        try {
            number = Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
            return -1;
        }
        if (number >= min && number <= max)
            return number;
        return -1;
    }

    public static int verifyPositiveInt(String string){
        if(string.isEmpty()){
            return -1;
        }
        int number;
        try {
            number = Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
            return -1;
        }
        return number;
    }

    public static boolean verifyPathExistance(String path){
        File f = new File(path);
        if(f.isFile()){
            return true;
        }
        return false;
    }

}
