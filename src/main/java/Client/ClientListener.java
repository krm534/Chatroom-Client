package Client;

import java.util.Scanner;

public class ClientListener extends Thread {
    private Scanner scanner;

    public ClientListener(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Client Listening!");
            String serverMessage = scanner.nextLine();
            System.out.println("Message received from server: " + serverMessage);
        }
    }
}
