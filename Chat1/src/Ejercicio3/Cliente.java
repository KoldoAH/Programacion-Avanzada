package Ejercicio3;

import Ejercicio2.*;
import Ejercicio1.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import utiles.Comunicaciones;


public class Cliente implements Runnable {
    
    public static Comunicaciones conexion;

    public static void main(String[] args) throws IOException, InterruptedException {
        Cliente c = new Cliente();
        c.conectar("127.0.0.1", 12000);
        //c.envioConsola();
        //c.terminarChat();
        
        
    }

    public void conectar(String addr, int puerto) throws IOException{
        String hostAddr =addr;
        int port = puerto;
        
        final Socket socket = new Socket(hostAddr, port);
        conexion = new Comunicaciones(socket);
        
        System.out.println("Socket connected to "
                + socket.getInetAddress()
                + ":" + socket.getPort());

        System.out.println("Type empty line for just receiving, exit for finishing the app");
        
    }
    
    public void envioConsola(String textArea){
        final Scanner scanner = new Scanner(System.in);
        String recv="";
        System.out.println("Recv: " + recv);
        for (;;) {
           
            System.out.print("Enter message: ");
            String msg = scanner.nextLine();

            if (textArea.equalsIgnoreCase("exit")) {
                break;
            }

            if (!textArea.isEmpty()) {
                conexion.sendMsg(textArea);
            }
             recv = conexion.receiveMsg();
            System.out.println("Recv: " + recv);
            
            
        }
        
    }
    
    public void terminarChat(){}
    
    
    @Override
    public void run() {
        for (;;) {
            System.out.println("ha llegado aqui");
            String recv = conexion.receiveMsg();
            System.out.println("Recv: " + recv);
            
        }
        
    }
}
