// 서버/클라이언트 구조에서 서버와 클라이언트가 1:1로 대화하는 프로그램

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class OneToOneS extends Frame implements ActionListener {
    TextArea display;
    TextField text;
    Label lword;
    Socket connection;
    BufferedWriter output;
    BufferedReader input;
    String clientdata = "";
    String serverdata = "";

    public OneToOneS() {
        super("Server");
        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.CENTER);

        Panel pword = new Panel(new BorderLayout());
        lword = new Label("Input");
        text = new TextField(30); // 전송할 데이터를 입력하는 필드
        text.addActionListener(this); // 입력된 데이터를 송신하기 위한 이벤트 연결
        pword.add(lword, BorderLayout.WEST);
        pword.add(text, BorderLayout.EAST);
        add(pword, BorderLayout.SOUTH);

        addWindowListener(new WinListener());
        setSize(300, 200);
        setVisible(true);
    }

    public void runServer() {
        ServerSocket server;
        try {
            server = new ServerSocket(5000, 100); // 5000번 포트에서 서버 소켓(리스닝 소켓) 생성 100 : 연결 대기 큐의 크기
            connection = server.accept(); // 클라이언트가 서버 요청시 연결 요청 수락 및 Socket 객체 반환/ 연결 수락 시 다음 코드 진행
            // 클라이언트로부터 데이터 수신
            InputStream is = connection.getInputStream(); // 소켓 객체(connection)에서 입력 스트림 얻기
            InputStreamReader isr = new InputStreamReader(is); // 바이트 기반의 InputStream(is)를 문자 기반 스트림으로 변환
            input = new BufferedReader(isr); // BufferedReader 생성하여 문자 입력 스트림을 효율적으로 사용 -> 버퍼링 기능 제공
            // 클라이언트로 데이터 송신
            OutputStream os = connection.getOutputStream(); // 소켓 객체에서 출력스트림 얻기
            OutputStreamWriter osw = new OutputStreamWriter(os); // 바이트 기반 스트림 -> 문자 기반 스트림으로 변환
            output = new BufferedWriter(osw); // BufferedWriter 데이터를 버퍼에 모아두다가 한번에 전송
            while (true) {
                clientdata = input.readLine(); // 입력
                if (clientdata.equals("quit")) {
                    display.append("\n 클라이언트와의 접속이 중단되었습니다.");
                    output.flush(); // flush : 채팅방의 Enter 기능 -> 출력 스트림 비워서 데이터를 즉시 보내도록 함
                    break;
                } else {
                    display.append("\n클라이언트 메시지 : " + clientdata); // 클라이언트 메세지 출력
                    output.flush();
                }
            }
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    //서버에서 클라이언트로 메세지 보내기
    @Override
    public void actionPerformed(ActionEvent ae) { // GUI에서 텍스트 입력하고 엔터키 누르면 호출되는 이벤트 핸들러
        serverdata = text.getText(); // 사용자가 입력한 텍스트 가져오기
        try {
            display.append("\n서버 : " + serverdata);
            output.write(serverdata + "\r\n"); //사용자(서버)가 입력한 텍스트를 출력 스트림(output)을 통해 클라이언트에게 전송
            output.flush(); // /r : enter
            text.setText(""); // 텍스트 보냈으니까 비우기
            if (serverdata.equals("quit"))
                connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        OneToOneS s = new OneToOneS();
        s.runServer();
    }

}


