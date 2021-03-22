package com.company.dataStructures;

public class ChannelInfo {
    protected int port;
    protected String IP;

    public ChannelInfo(){
    }



    //Maybe make a version that has to be multicast
    public boolean verifyCurrentIP(){
        if(IP == null){
            return false;
        }
        if(IP.length() > 15 || IP.length() < 7){
            return false;
        }

        String[] numbers = IP.split(".");
        for(String number: numbers){
            try {
                int num = Integer.parseInt(number);

                if(num < 256 && num >= 0){
                    return false;
                }
            }
            catch(NumberFormatException e){
                return false;
            }
        }
        return true;
    }

    public boolean verifyIP(String IP){
        if(IP == null){
            return false;
        }
        if(IP.length() > 15 || IP.length() < 7){
            return false;
        }

        String[] numbers = IP.split(".");
        for(String number: numbers){
            try {
                int num = Integer.parseInt(number);

                if(num < 256 && num >= 0){
                    return false;
                }
            }
            catch(NumberFormatException e){
                return false;
            }
        }
        return true;
    }

    public boolean setInfo(String IP, int port){
        if(!verifyIP(IP)){
            return false;
        }
        this.port = port;
        this.IP = IP;
        return true;
    }

    public int getPort(){
        return port;
    }
    public String getIP(){
        return IP;
    }


}
