package com.company;

import java.rmi.RemoteException;

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
}
