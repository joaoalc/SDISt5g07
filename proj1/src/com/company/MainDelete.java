package com.company;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MainDelete {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //MulticastPublisher mpub = new MulticastPublisher();
        //mpub.multicast("despacito lmao");

        MulticastThread MC = new MulticastThread("230.0.0.0", 4446, "5", "MC");
        MulticastThread MDB = new MulticastThread("230.0.0.1", 4446, "5", "MDB");
        MulticastThread MDR = new MulticastThread("230.0.0.2", 4446, "5", "MDR");


        Peer peer = new Peer(MC, MDB, MDR, "5");

        MC.setChannelSockets(MC, MDB, MDR);
        MDB.setChannelSockets(MC, MDB, MDR);
        MDR.setChannelSockets(MC, MDB, MDR);

        MC.setPeer(peer);
        MDB.setPeer(peer);
        MDR.setPeer(peer);


        MC.start();
        MDB.start();
        MDR.start();

        peer.delete("files/spooky_month.gif", "1.0");

    }
}
