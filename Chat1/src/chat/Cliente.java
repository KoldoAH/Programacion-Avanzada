package chat;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import utiles.Comunicaciones;

public class Cliente {

    public static void main(String[] args) throws IOException {
        final String hostAddr = "127.0.0.1";
        final int port = 12000;
        final Scanner scanner = new Scanner(System.in);
        final Socket socket = new Socket(hostAddr, port);
        Comunicaciones con = new Comunicaciones(socket);

        System.out.println("Socket connected to "
                + socket.getInetAddress()
                + ":" + socket.getPort());

        System.out.println("Type empty line for just receiving, exit for finishing the app");

        for (;;) {
            System.out.print("Enter message: ");
            String msg = scanner.nextLine();

            if (msg.equalsIgnoreCase("exit")) {
                break;
            }

            if (!msg.isEmpty()) {
                con.sendMsg(msg);
            }

            String recv = con.receiveMsg();
            System.out.println("Recv: " + recv);
        }

    }
}
