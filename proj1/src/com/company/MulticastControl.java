package com.company;

import com.company.utils.MessageParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MulticastControl extends MulticastThread{


    public MulticastControl(String IP, int port) {
        super(IP, port);
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
                ArrayList<String> header = MessageParser.getFirstLineArguments(new String(received));
                byte[] body = MessageParser.getBody(received);

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
