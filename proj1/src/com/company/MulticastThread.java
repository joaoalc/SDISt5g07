package com.company;

import com.company.MulticastHandler.MulticastResponseHandler;
import com.company.dataStructures.ChannelInfo;
import com.company.utils.MessageParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MulticastThread extends Thread{
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[65536];

    ChannelInfo name;
    InetAddress group;

    private MulticastThread MC;
    private MulticastThread MDB;
    private MulticastThread MDR;

    //TODO: Use this to know which channel the message comes from
    String channelType;

    String senderID;
    //The peer that owns this channel
    public Peer peer;



    public MulticastThread(String IP, int port, String senderID, String channelType) throws IOException {
        name = new ChannelInfo();
        name.setInfo(IP, port);

        socket = new MulticastSocket(name.getPort());
        group = InetAddress.getByName(name.getIP());
        socket.joinGroup(group);
        this.senderID = senderID;
        this.channelType = channelType;
        System.setProperty("file.encoding", "US-ASCII");
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

    public void setPeer(Peer peer){
        this.peer = peer;
    }

    public void run() {
        try{
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                System.out.println("Receiving packet.");
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                byte[] packetData = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, packetData, 0, packet.getLength());

                //System.out.println("Size: " + packetData.length);
                ArrayList<String> args = MessageParser.getFirstLineArguments(received);
                for(int i = 0; i < args.size(); i++){
                    System.out.print(args.get(i) + " ");
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

    public void sendMessage(byte[] message, int messageLength) throws IOException {
        socket.send(new DatagramPacket(message, messageLength, group, name.getPort()));
    }
}
