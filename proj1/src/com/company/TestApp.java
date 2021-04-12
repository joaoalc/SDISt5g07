package com.company;

import com.company.Client;
import com.company.utils.StringVerification;

import java.util.Scanner;

public class TestApp {

    public static void printUsage() {
        System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
    }

    public static void main(String[] args) {

        if ((args.length < 2) || (args.length > 4)) {
            printUsage();
            System.exit(-1);
        }

        String peerAp = args[0];
        String operation = args[1];

        if (operation.equalsIgnoreCase("BACKUP")) {
            if (args.length != 4) {
                printUsage();
                System.exit(-1);
            }

            String path = args[2];

            if (!StringVerification.verifyPathExistance(path)) {
                System.out.println("Path not found: " + path);
                System.exit(-1);
            }

            int replicationDegree = StringVerification.verifyPositiveInt(args[3]);

            if (replicationDegree == -1) {
                System.out.println("Invalid replication degree: " + args[3]);
                System.exit(-1);
            }

            new Client(peerAp).backup(path, replicationDegree);
        }

        else if (operation.equalsIgnoreCase("RESTORE")) {
            if (args.length != 3) {
                printUsage();
                System.exit(-1);
            }

            String path = args[2];

            if (!StringVerification.verifyPathExistance(path)) {
                System.out.println("Path not found: " + path);
                System.exit(-1);
            }

            new Client(peerAp).restore(path);
        }

        else if (operation.equalsIgnoreCase("DELETE")) {
            if (args.length != 3) {
                printUsage();
                System.exit(-1);
            }

            String path = args[2];

            if (!StringVerification.verifyPathExistance(path)) {
                System.out.println("Path not found: " + path);
                System.exit(-1);
            }

            new Client(peerAp).delete(path);
        }

        else if (operation.equalsIgnoreCase("RECLAIM")) {
            if (args.length != 3) {
                printUsage();
                System.exit(-1);
            }

            int space = StringVerification.verifyPositiveInt(args[2]);

            if (space == -1) {
                System.out.println("Invalid replication degree: " + args[2]);
                System.exit(-1);
            }

            new Client(peerAp).reclaim(space);
        }

        else if (operation.equalsIgnoreCase("STATE")) {
            if (args.length != 2) {
                printUsage();
                System.exit(-1);
            }

            new Client(peerAp).state();
        }

        else {
            printUsage();
            System.exit(-1);
        }
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

    }
}
