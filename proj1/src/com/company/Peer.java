package com.company;

import java.io.IOException;
import java.rmi.RemoteException;

public class Peer implements IPeerRemote {

    @Override
    public void backup(String path, int replication) throws RemoteException {

    }

    @Override
    public void restore(String path) throws RemoteException {

    }

    @Override
    public void delete(String path) throws RemoteException {

    }

    @Override
    public void reclaim(int space) throws RemoteException {

    }

    public static void main(String[] args) throws IOException {

    }
}
