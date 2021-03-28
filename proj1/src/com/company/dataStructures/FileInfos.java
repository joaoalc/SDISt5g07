package com.company.dataStructures;

import java.util.ArrayList;

public class FileInfos {

    public ArrayList<FileInfo> fileInfos;

    public FileInfos(){
        fileInfos = new ArrayList<>();
    }

    public FileInfo findByFilePath(String filePath) {
        for(int i = 0; i < fileInfos.size(); i++){
            if(fileInfos.get(i).filePath.compareTo(filePath) == 0){
                return fileInfos.get(i);
            }
        }
        return null;
    }

    public FileInfo findByFileID(String fileID) {
        for(int i = 0; i < fileInfos.size(); i++){
            if(fileInfos.get(i).fileID.compareTo(fileID) == 0){
                return fileInfos.get(i);
            }
        }
        return null;
    }


    public FileInfo addFile(FileInfo fileInfo) {
        FileInfo a = findByFilePath(fileInfo.filePath);
        if(a == null){
            fileInfos.add(fileInfo);
            return fileInfo;
        }
        return a;

    }
}
