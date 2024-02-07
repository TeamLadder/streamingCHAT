package multipleChat;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ChatMessageS extends Frame{
    TextArea display;
    Label info;
    List<ServerThread> list;
    public ServerThread SThread;

    public ChatMessageS(){
        super("서버");
        info = new Label();
        add(info, BorderLayout.CENTER);
        display = new TextArea(" ", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.SOUTH);
        addWindowListener(new WinListener());
        setSize(300,250);
        setVisible(true);

    }
    public void runServer(){
        ServerSocket server;
        Socket sock;
        ServerThread SThread;
        try{
            list = new ArrayList<ServerThread>();
            server = new ServerSocket(5000,100);
            try{
                while (true){
                    sock = server.accept();
                    SThread = new ServerThread(this, sock, display, info);
                    SThread.start();
                    info.setText(sock.getInetAddress().getHostName()+" 서버는 클라이언트와 연결됨");
                }
            } catch (IOException ioe){
                server.close();
                ioe.printStackTrace();
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    public static void main(String args[]){
        ChatMessageS s = new ChatMessageS();
        s.runServer();
    }

}
