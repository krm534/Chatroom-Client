import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Thread {
  private String address;
  private Controller controller;
  private Socket socket;
  private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

  public Client(String address, Controller controller) {
    try {
      this.address = address;
      this.controller = controller;
      this.socket = new Socket(address, Constants.DEFAULT_PORT);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Client Exception: " + e.getMessage());
    }
  }

  @Override
  public void run() {
    try {
      Scanner input = new Scanner(socket.getInputStream());

      // Receive port number for client to use
      int port = Integer.parseInt(input.nextLine());
      LOGGER.log(Level.INFO, String.format("Port %d returned from the chatroom server", port));
      socket.close();

      // Update server socket to use new port number
      socket = new Socket(address, port);
      input = new Scanner(socket.getInputStream());

      // Listen for messages from Server
      while (true) {
        controller.addMessage(input.nextLine());
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Client Exception: " + e.getMessage());
    }
  }

  public void sendMessageHandler(String message) {
    final ServerSender serverSender = new ServerSender(socket, message);
    serverSender.start();
  }
}
