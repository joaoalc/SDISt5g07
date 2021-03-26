package com.company.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MessageParser {

    private static final Pattern versionPattern = Pattern.compile("[0-9]\\.[0-9]");
    //private static final Pattern versionPattern1 = Pattern.compile("0\\.0");
    private static final Pattern fileIdPattern = Pattern.compile("([0-9A-Fa-f]{2}){32}");
    private static final Pattern chunkNoPattern = Pattern.compile("[0-9]{1,6}");
    private static final Pattern replicationDegPattern = Pattern.compile("[0-9]");


    //TODO: fix, probably byte[] to string conversion breaking the endOfHeader search (at the function call)?
    //TODO: Probably an error if the string ends in the first 0x0D 0x0A 0x0D 0x0A
    public static ArrayList<String> getFirstLineArguments(String message){
        byte[] CRLF = {0x0D, 0x0A};
        byte[] doubleCRLF = {0x0D, 0x0A, 0x0D, 0x0A};
        int endOfHeader = message.indexOf(new String(doubleCRLF)); //Index is at the start of CRLF
        if(endOfHeader == -1){
            System.out.println("No end of header found! This should not happen!");
            return null;
        }
        String msgHeader = message.substring(0, endOfHeader);


        //Get each line of the header; Only the first line is used for the subprotocols in this part
        String[] lines = msgHeader.split(new String(CRLF));

        ArrayList<String> arr_new = new ArrayList<>();
        //If multiline
        if(lines.length > 0){
            String[] lineElems = lines[0].split(" ");


            for(int i=0;i<lineElems.length;i++){
                if(lineElems[i]!=""){
                    arr_new.add(lineElems[i]);
                }
            }
        }
        else{
            String[] lineElems = msgHeader.split(" ");


            for(int i=0;i<lineElems.length;i++){
                if(lineElems[i]!=""){
                    arr_new.add(lineElems[i]);
                }
            }
        }

        try {
            messageFieldsAreValid(arr_new);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return arr_new;
    }

    public static void messageFieldsAreValid(ArrayList<String> messageFields) throws IllegalArgumentException {

        if (messageFields.size() < 6) {
            throw new IllegalArgumentException("Number of fields in message should be less than 6 but " + messageFields.size() + "was given");
        }

        // Check version
        String version = messageFields.get(0);
        if (!versionPattern.matcher(version).matches()) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }

        // Check file Id
        String fileId = messageFields.get(3);
        if (!fileIdPattern.matcher(fileId).matches()) {
            throw new IllegalArgumentException("Invalid file id format: " + fileId);
        }

        // Check chunk number
        String chunkNo = messageFields.get(4);
        if (!chunkNoPattern.matcher(chunkNo).matches()) {
            throw new IllegalArgumentException("Invalid file id format: " + chunkNo);
        }

        // Check fileId
        String replicationDeg = messageFields.get(5);
        if (!replicationDegPattern.matcher(replicationDeg).matches()) {
            throw new IllegalArgumentException("Invalid replication format: " + version);
        }
    }

    public static byte[] getBody(byte[] message){

        byte[] CRLF = {0x0D, 0x0A};
        byte[] doubleCRLF = {0x0D, 0x0A, 0x0D, 0x0A};

        int number = -1;

        for(int i = 0; i < message.length; i++){
            if(message[i] == 0x0D){
                if(message[i+1] == 0x0A && message[i+2] == 0x0D && message[i+3] == 0x0A){
                    number = i;
                }
            }
        }

        return Arrays.copyOfRange(message, number+4, message.length);

    }


    public static void main(String[] args) throws IOException {
        String message = "1.0 " + "PUTCHUNK " + "12312312312312312312312312312312 " + "B2".repeat(32) + " 0 " + "1";

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
        //byte[] body = getBody(bytes);
        System.out.println("");
        //System.out.println("\"" + new String(bytes) + "\"");
        System.out.println("\"" + getFirstLineArguments(new String(bytes)) + "\"");

        //for(int i = 0; i < )
        //System.out.println("Length difference: " + (bytes.length - body.length));


    }


}
