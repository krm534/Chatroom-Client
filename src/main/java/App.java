import Client.Client;
import Server.Server;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    public static void main(String[] args) {
        String address = "";
        int port = 6789;

        if (!args[0].isEmpty()) {
            System.out.println("Address entered is " + args[0]);
            address = args[0];
        }

        if (!args[1].isEmpty()) {
            System.out.println("Port entered is " + args[1]);
            port = Integer.parseInt(args[1]);
        }

        if (args[2].equals("-c")) {
            Client client = new Client(address, port);
            client.start();
        } else {
            Server server = new Server();
            server.start();
        }
    }
}