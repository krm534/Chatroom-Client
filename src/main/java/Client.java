import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {
    private Socket socket;
    private String socketAddress;
    private int port;
    private DataOutputStream dataOutputStream;

    public Client(String address, int port) {
        socketAddress = address.isEmpty() ? "127.0.0.1" : address;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter message to server: " );
            String message = scanner.nextLine();

            socket = new Socket(socketAddress, port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(message.getBytes());
            System.out.println("Message '" + message + "' sent to server!");

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
