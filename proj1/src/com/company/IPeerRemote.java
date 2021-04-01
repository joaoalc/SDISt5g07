package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

public interface IPeerRemote extends Remote {
    void backup(String path, int replication, String version) throws IOException, NoSuchAlgorithmException;
    void restore(String path) throws RemoteException;
    void delete(String path, String version) throws IOException, NoSuchAlgorithmException;
    void reclaim(int space) throws RemoteException;
}
