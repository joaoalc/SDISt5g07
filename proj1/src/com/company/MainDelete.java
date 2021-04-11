package com.company;

import com.company.dataStructures.PeerStorage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MainDelete {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        String senderID = args[0];

        MulticastThread MC = new MulticastThread("230.0.0.0", 4446, senderID, "MC");
        MulticastThread MDB = new MulticastThread("230.0.0.1", 4446, senderID, "MDB");
        MulticastThread MDR = new MulticastThread("230.0.0.2", 4446, senderID, "MDR");

        PeerStorage peerStorage = new PeerStorage(Integer.parseInt(senderID));

        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(16);

        Peer peer = new Peer("1.0", MC, MDB, MDR, senderID, peerStorage, threadPool);

        MC.setChannelSockets(MC, MDB, MDR);
        MDB.setChannelSockets(MC, MDB, MDR);
        MDR.setChannelSockets(MC, MDB, MDR);

        MC.setPeer(peer);
        MDB.setPeer(peer);
        MDR.setPeer(peer);


        /*MC.start();
        MDB.start();
        MDR.start();*/

        threadPool.execute(MC);
        threadPool.execute(MDB);
        threadPool.execute(MDR);

        peer.delete(peerStorage.getFilesDirectory(Integer.parseInt(senderID)) + "/spooky_month.gif");
    }
}
