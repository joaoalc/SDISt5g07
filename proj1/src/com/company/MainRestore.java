package com.company;

import com.company.dataStructures.PeerStorage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MainRestore {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        String senderID = args[0];

        MulticastThread MC = new MulticastThread("230.0.0.0", 4446, senderID, "MC");
        MulticastThread MDB = new MulticastThread("230.0.0.1", 4446, senderID, "MDB");
        MulticastThread MDR = new MulticastThread("230.0.0.2", 4446, senderID, "MDR");

        PeerStorage peerStorage = new PeerStorage(Integer.parseInt(senderID));

        Peer peer = new Peer(MC, MDB, MDR, senderID, peerStorage);

        MC.setChannelSockets(MC, MDB, MDR);
        MDB.setChannelSockets(MC, MDB, MDR);
        MDR.setChannelSockets(MC, MDB, MDR);

        MC.setPeer(peer);
        MDB.setPeer(peer);
        MDR.setPeer(peer);


        MC.start();
        MDB.start();
        MDR.start();

        peer.restore(peerStorage.getFilesDirectory(Integer.parseInt(senderID)) + "/runnel_thino.png", "1.0");
    }
}