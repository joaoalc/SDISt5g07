package com.company.dataStructures;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChunkFileInfos {

    //Key: File ID (encrypted); Value: Chunk information in that file that are stored here
    public HashMap<String, ChunkFileInfo> chunkInfos = new HashMap<>();


    public boolean AddChunk(String fileID, int chunkNo){
        ChunkFileInfo a = chunkInfos.get(fileID);
        if(a == null){
            chunkInfos.put(fileID, new ChunkFileInfo());
            a = chunkInfos.get(fileID);
        }
        if(!a.ChunkExists(chunkNo)){
            a.chunks.add(chunkNo);
            return true;
        }
        return false;
    }

    public void printValuesHumanReadable() {
        String a = "";
        a += "This peer contains " + chunkInfos.size() + " files\n\n";
        int fileNum = 0;
        for(Map.Entry<String, ChunkFileInfo> set: chunkInfos.entrySet()){
            a += "File number " + fileNum + ": \n";
            //(File path of when this version of the file was inserted, file could've been manually moved or overwritte, in which case it would not be there)
            a += "The file's ID is: " + set.getKey() + "\n";

            for(int i = 0; i < set.getValue().chunks.size(); i++){
                a += "Chunk number " + i + " is being backed up\n";
            }
        }
        System.out.println(a);
        /*String result = infos.fileInfos.size() + "\n";
        for(FileInfo fInfo: infos.fileInfos){
            result += fInfo.unencryptedFileID + "\n" + fInfo.fileID + "\n" + fInfo.usersBackingUp.size() + "\n";
            for(int i = 0; i < fInfo.usersBackingUp.size(); i++){
                result += i + " " + fInfo.usersBackingUp.get(i).size();
                for(int j = 0; j < fInfo.usersBackingUp.get(i).size(); j++){
                    result += " " + fInfo.usersBackingUp.get(i).get(j);
                }
                result += "\n";
            }
        }*/
    }

}
