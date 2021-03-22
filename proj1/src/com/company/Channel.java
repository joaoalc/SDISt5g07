package com.company;

public class Channel implements Runnable {
    private String address;
    private int port;

    public Channel(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        // TODO: implement this
    }
}
