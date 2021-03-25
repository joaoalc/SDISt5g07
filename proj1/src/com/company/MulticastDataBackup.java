package com.company;

import com.company.MulticastHandler.MulticastResponseHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastDataBackup extends MulticastThread{
    MulticastResponseHandler mRes;

    public MulticastDataBackup(String IP, int port, String senderID) throws IOException {
        super(IP, port, senderID);
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
