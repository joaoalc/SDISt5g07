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

    public String getValuesHumanReadable() {
        String a = "";
        a += "This peer contains " + fileInfos.size() + " files\n\n";
        int fileNum = 0;
        for(FileInfo fInfo: fileInfos){
            a += "File number " + fileNum + ": \n";
            //(File path of when this version of the file was inserted, file could've been manually moved or overwritte, in which case it would not be there)
            a += "File path: " + fInfo.filePath + "\n";
            a += "File is: " + fInfo.unencryptedFileID + "\n";
            a += "The file's ID is: " + fInfo.fileID + "\n";
            a += "The file has a total of " + fInfo.usersBackingUp.size() + " chunks\n\n";

            for(int i = 0; i < fInfo.usersBackingUp.size(); i++){
                a += "Chunk number " + i + " has " + fInfo.usersBackingUp.get(i).size() + " users backing it up, whose Ids are:\n";

                for(int j = 0; j < fInfo.usersBackingUp.get(i).size(); j++) {
                    a += " " + fInfo.usersBackingUp.get(i).get(j);
                }
                a += "\n";
            }
        }
        return a;
    }

    public String getState() {
        String result = "";
        result += "This peer contains " + fileInfos.size() + " files\n";
        int fileNum = 0;
        for(FileInfo fInfo: fileInfos){
            result += "File number " + fileNum + ": \n";
            //(File path of when this version of the file was inserted, file could've been manually moved or overwritte, in which case it would not be there)
            result += "File path: " + fInfo.filePath + "\n";
            result += "File is: " + fInfo.unencryptedFileID + "\n";
            result += "The file's ID is: " + fInfo.fileID + "\n";
            //result += "The file's desired replication degree is: " + fInfo. + "\n";
            result += "The file has a total of " + fInfo.usersBackingUp.size() + " chunks:\n";
            result += "Desired replication degree: " + fInfo.desiredReplicationDegree + "\n";
            for(int i = 0; i < fInfo.usersBackingUp.size(); i++){
                result += "\t- Chunk number " + i + " has " + fInfo.usersBackingUp.get(i).size() + " perceived replication degree." + "\n";
            }
        }
        return result;
    }

    public void printValuesHumanReadable() {

        System.out.println(getValuesHumanReadable());
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
