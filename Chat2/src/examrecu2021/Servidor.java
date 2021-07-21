package examrecu2021;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Servidor extends Thread {

    private static final String PASSWORD = "12345";
    private static ArrayList nombresBaneados, numeroMensajesUsuarios,nombresUsuarios;
    
    
    public static void main(String[] args) {
        Servidor server = new Servidor(12000);
        server.start();
        nombresBaneados= new ArrayList<>();
        numeroMensajesUsuarios= new ArrayList<>();
        nombresUsuarios= new ArrayList<>();
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
            int userCount = 1;
            while (!interrupted()) {
                Socket clientSocket = serverSocket.accept();
                
                ClientThread clientThread = new ClientThread(clients,
                        new Comunicaciones(clientSocket), "Usuario " + userCount);
                numeroMensajesUsuarios.add("0");
                nombresUsuarios.add("Usuario " + userCount);
                clientThread.start();
                userCount += 1;
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class ClientThread extends Thread {

        final List<ClientThread> clients;
        final Comunicaciones con;
        String nombre;
        boolean isAdmin;

        public ClientThread(List<ClientThread> clients, Comunicaciones con, String nombre) {
            this.clients = clients;
            this.con = con;
            this.nombre = nombre;
        }

        synchronized public void sendMsg(String msg) {
            con.sendMsg(msg);
        }
        
        public void sendMsgToAll(String msg) {
            synchronized (clients) {
                clients.forEach(c -> c.sendMsg(msg));
            }
        }

        @Override
        public void run() {
            try {
                System.out.println(con);

                synchronized (clients) {
                    clients.add(this);
                }

                for (String line; ( line = con.receiveMsg() ).equals("EOF") == false; ) {
                    if (line.startsWith("/nombre ")) {
                        String partes[]= line.split(" ");
                        if (nombresBaneados.contains(partes[1])){
                            sendMsg("El nombre está baneado");
                        }
                        else{
                            String antiguoNombre= nombre;
                            int index= nombresUsuarios.indexOf(antiguoNombre);
                            
                            
                            nombre = line.replaceAll("/nombre ", "");
                            nombresUsuarios.set(index, nombre);
                            
                            System.out.println(nombresUsuarios.get(0));
                            
                           
                            
                        }
                        
                    }else if(line.startsWith("/admin")){
                        String[] parts = line.split(" ");
                        
                        if (parts.length == 2 && parts[1].equals(PASSWORD)){
                            isAdmin = true;
                            sendMsg("Now you are admin");
                        }else{
                            sendMsg("Incorrect password");
                        }
                    }else{ 
                        String partes[]= line.split(" ");
                        System.out.println("partes0: "+partes[0]);
                        if(isAdmin){
                            if(partes[0].equals("/ban")){
                                nombresBaneados.add(partes[1]);
                            }
                            else if(partes[0].equals("/unban")){
                                nombresBaneados.remove(partes[1]);
                            }
                            else if(partes[0].equals("/stats")){
                                BufferedWriter out = null;
                                
                                String listaStats;
                                listaStats="";
                                int numeroUsuarios= nombresUsuarios.size();
                                for (int i =0; i<numeroUsuarios; i++){
                                    String nombreUsuario= nombresUsuarios.get(i)+"";
                                    int numeroMensajes= parseInt(numeroMensajesUsuarios.get(i)+"");
                                    listaStats= listaStats+nombreUsuario+" |"+numeroMensajes+"";
                                    if(i<numeroUsuarios-1){
                                        listaStats=listaStats+"||";
                                    }
                                    try {
                                        FileWriter fstream = new FileWriter("stats.csv", true); //con true, indico que ha de adicionar texto al fichero
                                        out = new BufferedWriter(fstream);
                                        out.write(nombreUsuario+ ";"+numeroMensajes+"\n");
                                    }
                                    catch (IOException e) {
                                        System.err.println("Error: " + e.getMessage());
                                    }
                                    finally {
                                        if(out != null) {
                                            out.close();
                                        }
                                  }
                                     
                                }
                                System.out.println(listaStats);
                                sendMsg(listaStats);
                                
                                
                                

                                 
                            }
                            else{
                            String msg = nombre + ": " + line;
                            sendMsgToAll( msg );}
                        
                            int index= nombresUsuarios.indexOf(nombre);
                            String numeroMensajes=numeroMensajesUsuarios.get(index)+"";
                            int nuevoNumero= parseInt(numeroMensajes)+1;
                            String nuevoNumeroEnEnLista= nuevoNumero+"";
                            numeroMensajesUsuarios.set(index,nuevoNumeroEnEnLista);
                            System.out.println(numeroMensajesUsuarios.get(0));
                            
                        }
                        else{
                            String msg = nombre + ": " + line;
                            sendMsgToAll( msg );}
                        
                            int index= nombresUsuarios.indexOf(nombre);
                            String numeroMensajes=numeroMensajesUsuarios.get(index)+"";
                            int nuevoNumero= parseInt(numeroMensajes)+1;
                            String nuevoNumeroEnEnLista= nuevoNumero+"";
                            numeroMensajesUsuarios.set(index,nuevoNumeroEnEnLista);
                            System.out.println(numeroMensajesUsuarios.get(0));
                        
                    }
                }
                
                con.sendMsg("EOF"); //fin de mensajes
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    con.desconectar();
                } catch (Exception ex) {}
                synchronized (clients) {
                    clients.remove(this);
                }
            }
        }

    }

}
