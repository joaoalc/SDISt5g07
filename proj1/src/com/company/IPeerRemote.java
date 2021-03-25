package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPeerRemote extends Remote {
    void backup(String path, int replication, String version) throws IOException;
    void restore(String path) throws RemoteException;
    void delete(String path) throws RemoteException;
    void reclaim(int space) throws RemoteException;
}
