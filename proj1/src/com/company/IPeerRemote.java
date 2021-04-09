package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

public interface IPeerRemote extends Remote {
    void backup(String path, int replication) throws IOException;
    void restore(String path) throws IOException;
    void delete(String path) throws IOException;
    void reclaim(int space) throws RemoteException;
}
