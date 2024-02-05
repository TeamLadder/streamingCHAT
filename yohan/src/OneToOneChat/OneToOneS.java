package yohan.src.OneToOneChat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class OneToOneS extends Frame implements ActionListener {
    TextArea display;            // 대화 내용을 보여주는 TextArea
    TextField text;              // 메시지를 입력하는 TextField
    Label lword;
    Socket connection;           // 클라이언트와의 연결을 담당하는 소켓
    BufferedReader input;        // 클라이언트로부터 받은 데이터를 읽는 Reader
    BufferedWriter output;       // 클라이언트로 데이터를 보내는 Writer
    String clientdata = "";      // 클라이언트로부터 받은 데이터를 저장할 변수
    String serverdata = "";      // 서버에서 클라이언트로 보낼 데이터를 저장할 변수

    public OneToOneS() {
        super("서버");
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

    // 서버 실행 메소드
    public void runServer() {
        ServerSocket server;
        try {
            server = new ServerSocket(5000, 100); // 5000번 포트에서 클라이언트 연결을 대기하는 서버 소켓 생성
            connection = server.accept();         // 클라이언트의 연결을 대기하고 수락
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            // 클라이언트로부터 데이터를 받아오는 Reader
            input = new BufferedReader(isr);

            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            // 클라이언트에 데이터를 보내는 Writer
            output = new BufferedWriter(osw);

            while (true) {
                String clientdata = input.readLine(); // 클라이언트로부터 데이터를 읽음
                if (clientdata.equals("quit")) {
                    display.append("\n클라이언트와의 접속이 중단되었습니다.");
                    output.flush();
                    break;
                } else {
                    display.append("\n 클라이언트 메시지 :" + clientdata);
                    output.flush();
                }
            }
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 사용자가 메시지를 입력하면 호출되는 메소드
    public void actionPerformed(ActionEvent ae) {
        serverdata = text.getText();
        try {
            display.append("\n서버 : " + serverdata);
            output.write(serverdata + "\r\n"); // 서버에서 클라이언트로 데이터를 보냄
            output.flush();
            text.setText("");
            if (serverdata.equals("quit")) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 메인 메소드
    public static void main(String[] args) {
        OneToOneS s = new OneToOneS();
        s.runServer();
    }

    // 윈도우 종료 리스너
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}