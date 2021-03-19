package com.company;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class ClientInterface {

    Scanner inputScanner;
    public ClientInterface(){
        inputScanner = new Scanner(System.in);
    };

    public void Option(int number){
        if(number == 1){
            Boolean valid_path_and_replication = false;
            while(!valid_path_and_replication) {
                System.out.println("Insert your desired file path. Warning: This is the path you will have to specify when deleting a file.");
                String path = inputScanner.nextLine();

                if(!StringVerification.verifyPathExistance(path)) {
                    System.out.println("Invalid file path. Please try again.");
                    continue;
                }
                System.out.println("Insert your desired replication degree.");
                int replicationDegree = StringVerification.verifyPositiveInt(inputScanner.nextLine());
                if(replicationDegree != -1)
                    valid_path_and_replication = true;
                else {
                    System.out.println("Invalid replication degree. Insert file path.");
                    continue;
                }
                System.out.println("Path: " + path + " replication degree: " + replicationDegree);
            }
        }
    }

    public void run(){
        System.out.println("What would you like to do: \n");
        System.out.println("1-Backup a file;");
        System.out.println("2-Restore a file;");
        System.out.println("3-Delete a file;");
        System.out.println("4-Manage local service storage;");
        System.out.println("5-Retrieve local service state information.");


        int number;
        while(true) {
            number = StringVerification.verifyPositiveIntRange(inputScanner.nextLine(), 1, 5);
            if(number != -1){
                break;
            }
        }

        Option(number);
    }


}
