package com.company.MulticastHandler;

public class RestoreInfo {
    public boolean repeat = false;
    String fileID;
    String chunkNum;

    RestoreInfo(String fileID, String chunkNum){
        this.fileID = fileID;
        this.chunkNum = chunkNum;
    }




}
