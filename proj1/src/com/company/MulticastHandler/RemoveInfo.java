package com.company.MulticastHandler;

public class RemoveInfo {

    public boolean repeat = false;
    String fileID;
    String chunkNum;
    MulticastResponseHandler handler;

    RemoveInfo(String fileID, String chunkNum, MulticastResponseHandler handler){
        this.fileID = fileID;
        this.chunkNum = chunkNum;
        this.handler = handler;
    }
}
