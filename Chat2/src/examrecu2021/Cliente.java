package examrecu2021;

import java.io.IOException;
import java.util.Scanner;
import javax.swing.JTextArea;

public class Cliente implements Runnable {

    private Comunicaciones con;
    private Thread t;
    JTextArea textoConversacion;
    ChatGUI chat_gui;

    public Cliente(JTextArea textoConversacion,ChatGUI chat_gui) {
        this.textoConversacion = textoConversacion;
        this.chat_gui=chat_gui;
    }

    @Override
    public void run() {
        for (;;) {
            String recv = con.receiveMsg();
            if ("EOF".equals(recv)) {
                System.out.println("Desconectando...");
                textoConversacion.setText(textoConversacion.getText() + "\n" + "Desconectando...");
                break;
            }
            else if(recv.equals("Now you are admin")){
                chat_gui.MostrarBotonStats();
            }
            textoConversacion.setText(textoConversacion.getText() + "\n" + recv);
        }
    }

    public void conectar(String hostAddr, int port) throws IOException {
        this.con = new Comunicaciones(hostAddr, port);
        t = new Thread(this);
        t.start();
    }

    public void desconectar() {
        if (con != null) {
            con.sendMsg("EOF");
        }
    }

    public void terminarChat() throws InterruptedException {
        t.join();
    }

    public Boolean envio(String msg) {
        if (msg.equalsIgnoreCase("exit")) {
            desconectar();
            return (false);
        }

        if (!msg.isEmpty()) {
            con.sendMsg(msg);
        }
        return (true);
    }

    public void envioConsola() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type empty line for just receiving, exit for finishing the app");
        String msg;
        do {
            System.out.print("Enter message: ");
            msg = scanner.nextLine();
        } while (envio(msg));
    }

}
