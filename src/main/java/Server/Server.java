package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread  {
    private Socket socket;
    private ServerSocket serverSocket;
    private int port = 3000;
    private PrintWriter printWriter;
    private Scanner scanner;
    private HashMap<Integer, Integer> existingClientPorts;
    private Queue<String> clientMessagesQueue;

    public Server() {
        try {
            existingClientPorts = new HashMap<>();
            serverSocket = new ServerSocket(port);
            clientMessagesQueue = new LinkedList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Waiting for connection on port " + port);
                socket = serverSocket.accept();
                scanner = new Scanner(socket.getInputStream());
                System.out.println("Initiation message received from client " + socket.getInetAddress().getHostAddress());

                // Create new client listener and sender threads
                int newClientPort = generateClientListenPort();
                SendClient sendClient = new SendClient(this);
                ReceiveClient receiveClient = new ReceiveClient(newClientPort, this, sendClient);
                sendClient.start();
                receiveClient.start();

                // Send new client port info back to client
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println(newClientPort);
                System.out.println("Port " + newClientPort + " sent to client!");

                // Close socket
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error Received: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int generateClientListenPort() {
        Random random = new Random();
        int randomPort = random.nextInt(65535) + 1000;
        System.out.println("Random port generated is " + randomPort);

        if (existingClientPorts.containsValue(randomPort)) {
            System.out.println("Port " + randomPort + " is already in use!");
            generateClientListenPort();
        }

        existingClientPorts.put(randomPort, randomPort);
        return randomPort;
    }

    public void addClientMessageToQueue(String message) {
        clientMessagesQueue.add(message);
    }

    public Queue<String> getClientMessagesQueue() {
        return clientMessagesQueue;
    }
}
