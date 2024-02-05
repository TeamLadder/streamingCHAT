package yohan.src.OneToOneChat;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class OneToOneC extends Frame implements ActionListener {

    TextArea display;           // 대화 내용을 보여주는 TextArea
    TextField text;             // 메시지를 입력하는 TextField
    Label lword;
    Socket client;              // 서버와의 연결을 담당하는 소켓
    BufferedReader input;       // 서버로부터 받은 데이터를 읽는 Reader
    BufferedWriter output;      // 서버로 데이터를 보내는 Writer
    String clientdata = "";     // 서버로부터 받은 데이터를 저장할 변수
    String serverdata = "";     // 클라이언트에서 서버로 보낼 데이터를 저장할 변수

    public OneToOneC() {
        super("클라이언트");
        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.CENTER);

        Panel pword = new Panel(new BorderLayout());
        lword = new Label("대화말");
        text = new TextField(30);
        text.addActionListener(this);
        pword.add(lword, BorderLayout.WEST);
        pword.add(text, BorderLayout.EAST);
        add(pword, BorderLayout.SOUTH);

        addWindowListener(new WinListener());
        setSize(300, 200);
        setVisible(true);
    }

    // 클라이언트 실행 메소드
    public void runClient() {
        try {
            client = new Socket(InetAddress.getLocalHost(), 5000); // 서버에 연결
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // 서버로부터 데이터를 받아오는 Reader
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            // 서버에 데이터를 보내는 Writer

            while (true) {
                String serverdata = input.readLine(); // 서버로부터 데이터를 읽음
                if (serverdata.equals("quit")) {
                    display.append("\n서버와의 접속이 중단되었습니다.");
                    output.flush();
                    break;
                } else {
                    display.append("\n서버 메시지 : " + serverdata);
                    output.flush();
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 사용자가 메시지를 입력하면 호출되는 메소드
    public void actionPerformed(ActionEvent ae) {
        serverdata = text.getText();
        try {
            display.append("\n 클라이언트 : " + serverdata);
            output.write(serverdata + "\r\n"); // 클라이언트에서 서버로 데이터를 보냄
            output.flush();
            text.setText("");
            if (serverdata.equals("quit")) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 메인 메소드
    public static void main(String[] args) {
        OneToOneC c = new OneToOneC();
        c.runClient();
    }

    // 윈도우 종료 리스너
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}