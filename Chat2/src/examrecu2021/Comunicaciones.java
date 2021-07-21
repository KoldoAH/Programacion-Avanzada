package examrecu2021;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Comunicaciones {
    
    private final Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    
    public Comunicaciones(Socket socket) throws IOException {
        this.socket = socket;
        openDataStreams();
    }
    
    public Comunicaciones(String hostAddr, int port) throws IOException {
        socket = new Socket(hostAddr, port);
        openDataStreams();
        System.out.println("Socket connected to " + this );
        
    }
    
    final void openDataStreams() throws IOException{
        in = new DataInputStream( socket.getInputStream() );
        out = new DataOutputStream( socket.getOutputStream() );
    }
    
    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush(); 
        } catch (IOException ex) {
            Logger.getLogger(Comunicaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String receiveMsg() {
        try {
            String line = in.readUTF();
            return (line);
        } catch (IOException ex) {
            Logger.getLogger(Comunicaciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return(null);
    }
    
    public void desconectar() throws IOException{
        socket.close();
    }

    @Override
    public String toString() {
        return socket.getInetAddress() + ":" + socket.getPort();
    }
    
    
}
