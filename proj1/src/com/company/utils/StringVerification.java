package com.company.utils;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class StringVerification {

    private static final Pattern ipPattern = Pattern.compile("2(2[4-9]|3[0-9])(\\.\\b([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])){3}");
    private static final Pattern versionPattern = Pattern.compile("[0-9]\\.[0-9]");

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

    public static boolean verifyIpAddress(String ip) {
        // 224.0.0.0 - 239.255.255.255
        return ipPattern.matcher(ip).matches();
    }

    public static boolean verifyVersion(String version) {
        // 1.0
        return versionPattern.matcher(version).matches();
    }

    public static boolean verifyPathExistance(String path){
        File f = new File(path);
        if(f.isFile()){
            return true;
        }
        return false;
    }

}
