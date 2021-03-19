package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastPublisher {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;

    public void multicast(String multicastMessage) throws IOException {
        socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        buf = multicastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
        System.out.println("Sending packet.");
        socket.send(packet);
        socket.close();
    }

    /*public void sendHeader(String version, String messageType){

    }*/

    public void sendBackupHeader(byte[] message){

        /*byte[] bytes = new byte[message.length() + 64000];
        for(int i = 0; i < message.length(); i++) {
            bytes[i] = (byte) message.charAt(i);
            System.out.println(bytes[i]);
        }*/
        try {
            if(socket == null) {
                socket = new DatagramSocket();
            }
            if(group == null){
                group = InetAddress.getByName("230.0.0.0");
            }
            socket.send(new DatagramPacket(message, message.length, group, 4446));
        }
        catch(IOException e){
            System.out.println("[MulticastPublisher] Cannot send through the socket; IO exception.");
        }
    }
}