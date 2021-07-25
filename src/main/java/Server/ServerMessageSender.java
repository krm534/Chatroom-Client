package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMessageSender extends Thread {
    private String hostAddress;
    private ArrayList<String> listOfClients;
    private DataOutputStream dataOutputStream;
    private int port;
    private String data;

    public ServerMessageSender(String hostAddress, ArrayList<String> listOfClients, int port, String data) {
        this.hostAddress = hostAddress;
        this.listOfClients = listOfClients;
        this.port = port;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            System.out.println("Number of clients: " + listOfClients.size());
            for (String address : listOfClients) {
                if (hostAddress.equals(address)) {
                    System.out.println(hostAddress + " equals " + address + " found!");
                    Socket returnSocket = new Socket(address, port);
                    System.out.println("Socket reached here!");
                    dataOutputStream = new DataOutputStream(returnSocket.getOutputStream());
                    System.out.println("Socket reached here! (2)");
                    dataOutputStream.write(data.getBytes());
                    System.out.println("Sent message to client " + address);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
