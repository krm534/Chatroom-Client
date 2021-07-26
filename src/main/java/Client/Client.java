package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {
    private Socket socket;
    private String socketAddress;
    private int port;
    private PrintWriter printWriter;
    private Scanner input;

    public Client(String address, int port) {
        socketAddress = address.isEmpty() ? "127.0.0.1" : address;
        this.port = port;
    }

    @Override
    public void run() {
            try {
                socket = new Socket(socketAddress, port);
                input = new Scanner(socket.getInputStream());
                printWriter = new PrintWriter(socket.getOutputStream(), true);

                // Get username from user
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter Username: " );
                String username = scanner.nextLine();

                // Receive port number for client to use
                port = Integer.parseInt(input.nextLine());
                System.out.println("Received port " + port + " from server!");

                // Change socket to new port number
                socket.close();
                socket = new Socket(socketAddress, port);
                Scanner newInput = new Scanner(socket.getInputStream());
                PrintWriter newPrintWriter = new PrintWriter(socket.getOutputStream(), true);

                // Pass client socket information to listener
                ClientListener clientListener = new ClientListener(newInput);
                clientListener.start();

                // Send confirmation back to server
                printWriter.println("Confirmation");
                System.out.println("Sent Confirmation!");

                while (true) {
                    scanner = new Scanner(System.in);
                    System.out.print("Enter message to server: ");
                    newPrintWriter.println(scanner.nextLine());
                }
            } catch (IOException e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
}
