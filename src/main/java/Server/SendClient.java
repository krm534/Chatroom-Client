package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

public class SendClient extends Thread {
    private Server server;
    private PrintWriter printWriter;

    public SendClient(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                Queue<String> clientMessages = server.getClientMessagesQueue();

                if (clientMessages.size() > 0) {
                    String clientMessage = clientMessages.poll();
                    printWriter.println(clientMessage);
                    System.out.println("'" + clientMessage + "'" + " sent to clients!");
                }
            } catch (Exception e) {
                System.out.println("Error Received: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void setSocket(Socket socket) {
        try {
            printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error Received: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
