package multipleChat;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;

public class ChatMessageC extends Frame implements ActionListener, KeyListener {
    TextArea display;
    TextField wtext, ltext;
    Label mlbl, wlbl, loglbl;
    BufferedWriter output;
    BufferedReader input;
    Socket client;
    StringBuffer clientdata;
    String serverdata;
    String ID;

    private static final String SEPARATOR = "|";
    private static final int REQ_LOGON = 1001;
    private static final int REQ_SENDWORDS = 1021;

    public ChatMessageC() {
        super("클라이언트");

        mlbl = new Label("채팅 상태를 보여줍니다.");
        add(mlbl, BorderLayout.NORTH);

        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.CENTER);

        Panel ptotal = new Panel(new BorderLayout());

        Panel pword = new Panel(new BorderLayout());
        wlbl = new Label("대화말");
        wtext = new TextField(30);
        wtext.addKeyListener(this);
        pword.add(wlbl, BorderLayout.WEST);
        pword.add(wtext, BorderLayout.EAST);
        ptotal.add(pword, BorderLayout.CENTER);

        Panel plabel = new Panel(new BorderLayout());
        loglbl = new Label("로그온");
        ltext = new TextField(30);
        ltext.addActionListener(this);
        plabel.add(loglbl, BorderLayout.WEST);
        plabel.add(ltext, BorderLayout.EAST);
        ptotal.add(plabel, BorderLayout.SOUTH);

        add(ptotal, BorderLayout.SOUTH);

        addWindowListener(new WinListener());
        setSize(300, 250);
        setVisible(true);
    }

    public void runClient() {
        try {
            client = new Socket(InetAddress.getLocalHost(), 5000);
            mlbl.setText("연결된 서버이름 : " + client.getInetAddress().getHostAddress());
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            clientdata = new StringBuffer(2048);
            mlbl.setText("접속 완료. 사용할 아이디를 입력하세요.");
            while(true) {
                serverdata = input.readLine();
                display.append(serverdata + "\r\n");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if(ID == null) {
            ID = ltext.getText();
            mlbl.setText(ID + "(으)로 로그인 하였습니다.");
            try {
                clientdata.setLength(0);
                clientdata.append(REQ_LOGON);
                clientdata.append(SEPARATOR);
                clientdata.append(ID);
                output.write(clientdata.toString() + "\r\n");
                output.flush();
                ltext.setVisible(false);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatMessageC c = new ChatMessageC();
        c.runClient();
    }

    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = new String();
            message = wtext.getText();
            if (ID == null) {
                mlbl.setText("다시 로그인 하세요.");
                wtext.setText("");
            } else {
                try {
                    clientdata.setLength(0);
                    clientdata.append(REQ_SENDWORDS);
                    clientdata.append(SEPARATOR);
                    clientdata.append(ID);
                    clientdata.append(SEPARATOR);
                    clientdata.append(message);
                    output.write(clientdata.toString() + "\r\n");
                    output.flush();
                    wtext.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void keyReleased(KeyEvent ke) {

    }

    public void keyTyped(KeyEvent ke) {

    }
}
