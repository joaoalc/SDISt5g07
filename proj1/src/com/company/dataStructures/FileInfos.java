package com.company.dataStructures;

import java.util.ArrayList;

public class FileInfos {

    public ArrayList<FileInfo> fileInfos;

    public FileInfo findByFilePath(String filePath) {
        for(int i = 0; i < fileInfos.size(); i++){
            if(fileInfos.get(i).filePath.compareTo(filePath) == 0){
                return fileInfos.get(i);
            }
        }
        return null;
    }
}
