import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread  {
    private Socket socket;
    private ServerSocket serverSocket;
    int port;
    private DataInputStream dataInputStream;
    private BufferedReader bufferedReader;
    private DataOutputStream dataOutputStream;
    private ArrayList<String> addressList;

    public Server(int port) {
        try {
            addressList = new ArrayList<>();
            this.port = port;
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String input;
            System.out.println("Waiting for connection on port " + port);
            socket = serverSocket.accept();
            String receivedIpAddress = socket.getInetAddress().getHostAddress();

            if (!addressList.contains(receivedIpAddress)) {
                addressList.add(receivedIpAddress);
            }

            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            while ((input = bufferedReader.readLine()) != null) {
                System.out.println("Received " + input + " from server!");
            }

            for (String address : addressList) {
                if (!receivedIpAddress.equals(address)) {
                    socket = new Socket(address, port);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.write(input.getBytes());
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
