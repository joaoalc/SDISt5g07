package com.company;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements IPeerRemote {

    private Channel MC, MDB, MDR;

    public Peer(String MCAddress, int MCPort, String MDBAddress, int MDBPort, String MDRAddress, int MDRPort) {
        this.MC = new Channel(MCAddress, MCPort);
        this.MDB = new Channel(MDBAddress, MDBPort);
        this.MDR = new Channel(MDRAddress, MDRPort);
    }

    @Override
    public void backup(String path, int replication) throws RemoteException {
        // TODO: implement this
    }

    @Override
    public void restore(String path) throws RemoteException {
        // TODO: implement this
    }

    @Override
    public void delete(String path) throws RemoteException {
        // TODO: implement this
    }

    @Override
    public void reclaim(int space) throws RemoteException {
        // TODO: implement this
    }

    public static void main(String[] args) {

        if (args.length != 9) {
            System.out.println("Usage: Peer <protocol_version> <peer_id> <acess_point> <mc_address> <mc_port> <mdb_address> <mdb_port> <mdr_address> <mdr_port>");
            System.exit(-1);
        }

        String protocolVersion = args[0];
        int peerID = Integer.parseInt(args[1]);
        String acessPoint = args[2];

        String MCAddress = args[3];
        int MCPort = Integer.parseInt(args[4]);

        String MDBAddress = args[5];
        int MDBPort = Integer.parseInt(args[6]);

        String MDRAddress = args[7];
        int MDRPort = Integer.parseInt(args[8]);

        try {
            Peer peer = new Peer(MCAddress, MCPort, MDBAddress, MDBPort, MDRAddress, MDRPort);
            IPeerRemote stub = (IPeerRemote) UnicastRemoteObject.exportObject(peer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(acessPoint, stub);

            System.out.println("Peer ready");
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
