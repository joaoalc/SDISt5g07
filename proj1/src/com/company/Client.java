package com.company;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private IPeerRemote peer;

    public Client(String accessPoint) {
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry("localhost");
            this.peer = (IPeerRemote) registry.lookup(accessPoint);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void backup(String path, int replication) {
        try {
            this.peer.backup(path, replication);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restore(String path) {
        try {
            this.peer.restore(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String path) {
        try {
            this.peer.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reclaim(int space) {
        try {
            this.peer.reclaim(space);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void state() {
        try {
            System.out.println(this.peer.state());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
