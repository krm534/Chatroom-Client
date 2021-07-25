package Server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

public class SendClient extends Thread {
    private Server server;
    private Socket socket;
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
                printWriter = new PrintWriter(socket.getOutputStream());

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
        this.socket = socket;
    }
}
