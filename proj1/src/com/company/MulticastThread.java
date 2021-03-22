package com.company;

import com.company.dataStructures.ChannelInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastThread extends Thread{
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[65536];

    ChannelInfo name;

    public MulticastThread(String IP, int port){
        name = new ChannelInfo();
        name.setInfo(IP, port);
    }

    public void run() {
        try{
            socket = new MulticastSocket(name.getPort());
            InetAddress group = InetAddress.getByName(name.getIP());
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                System.out.println("Receiving packet.");
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(packet.getLength());
                System.out.println(received);
                /*if ("end".equals(received)) {
                    break;
                }*/

            }
            /*socket.leaveGroup(group);
            socket.close();*/

        } catch (UnknownHostException e) {
            System.out.println("Could not find host.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO exception.");
            e.printStackTrace();
        }
    }

    public void storeChunk(){

    }
}
