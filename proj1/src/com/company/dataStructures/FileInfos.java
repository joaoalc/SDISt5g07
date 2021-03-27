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

    public void addFile(FileInfo fileInfo) {
        if(findByFilePath(fileInfo.filePath) == null){
            fileInfos.add(fileInfo);
        }

    }
}
