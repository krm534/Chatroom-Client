import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
  private String address;
  private Controller controller;
  private Socket socket;
  private BufferedWriter bufferedWriter;
  private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

  public ClientHandler(String address, Controller controller) {
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
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      // Listen for messages from Server
      while (true) {
        controller.addMessage(input.nextLine());
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Client Exception: " + e.getMessage());
    }
  }

  public void sendMessageHandler(String message) {
    final ServerSender serverSender = new ServerSender(bufferedWriter, message);
    serverSender.start();
  }
}
