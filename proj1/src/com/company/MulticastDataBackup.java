package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastDataBackup extends MulticastThread{
    MulticastPublisher mPub;

    public MulticastDataBackup(String IP, int port, MulticastPublisher mPub) {
        super(IP, port);
        this.mPub = mPub;
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
                byte[] received = packet.getData();

                //Print string and packet length
                System.out.println(packet.getLength());
                System.out.println(received);


                mPub.processMessage(received);

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

}
