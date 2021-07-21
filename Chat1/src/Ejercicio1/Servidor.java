package Ejercicio1;

import chat.*;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import utiles.Comunicaciones;

public class Servidor extends Thread {

    public static void main(String[] args) {
        Servidor server = new Servidor(12000);
        server.start();
    }

    final int port;
    final List<ClientThread> clients = new LinkedList<>();

    public Servidor(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("Started server on port " + port);
            while (!interrupted()) {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clients,
                        new Comunicaciones(clientSocket));
                clientThread.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class ClientThread extends Thread {

        final List<ClientThread> clients;
        final Comunicaciones con;

        public ClientThread(List<ClientThread> clients, Comunicaciones con) {
            this.clients = clients;
            this.con = con;
        }

        synchronized public void sendMsg(String msg) {
            con.sendMsg(msg);
        }

        @Override
        public void run() {
            try {
                System.out.println(con);

                synchronized (clients) {
                    clients.add(this);
                }
                
                    for (String line; (line = con.receiveMsg()) != null;) {
                       
                        String mayus = line.toUpperCase();
                        /*synchronized (clients) {
                            clients.forEach(c -> c.sendMsg(mayus));
                        }*/
                    
                        clients.forEach(c -> c.sendMsg(mayus));
                        
                    }
                
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    con.desconectar();
                } catch (Exception ex) {
                }
                synchronized (clients) {
                    clients.remove(this);
                }
            }
        }

    }

}
