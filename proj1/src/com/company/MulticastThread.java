package com.company;

import com.company.MulticastHandler.MulticastResponseHandler;
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
    InetAddress group;

    private MulticastThread MC;
    private MulticastThread MDB;
    private MulticastThread MDR;

    String senderID;



    public MulticastThread(String IP, int port, String senderID) throws IOException {
        name = new ChannelInfo();
        name.setInfo(IP, port);

        socket = new MulticastSocket(name.getPort());
        group = InetAddress.getByName(name.getIP());
        socket.joinGroup(group);
        this.senderID = senderID;
    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public InetAddress getGroup() {
        return group;
    }

    public ChannelInfo getInfo() {
        return name;
    }

    public void setChannelSockets(MulticastThread MCSocket, MulticastThread MDBSocket, MulticastThread MDRSocket){
        this.MC = MCSocket;
        this.MDB = MDBSocket;
        this.MDR = MDRSocket;
    }


    public void run() {
        try{
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                System.out.println("Receiving packet.");
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                byte[] packetData = packet.getData();
                int a = Integer.min(100, received.length());
                for(int i = 0; i < a; i++){
                    System.out.print(received.charAt(i));
                }
                System.out.println();
                MulticastResponseHandler packetHandler = new MulticastResponseHandler(senderID, packetData, MC, MDB, MDR);
                packetHandler.start();
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

    public void sendMessage(byte[] message) throws IOException {
        socket.send(new DatagramPacket(message, message.length, group, name.getPort()));
    }
}
