package multipleChat;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// 서버 클래스 정의
public class ChatMessageS extends Frame {
    TextArea display; // 채팅 내용을 표시하는 TextArea
    Label info; // 서버 상태를 나타내는 레이블
    List<ServerThread> list; // 연결된 클라이언트를 관리하는 스레드 리스트
    public ServerThread SThread; // 서버 스레드

    // 생성자
    public ChatMessageS() {
        super("서버");

        // 레이아웃 설정
        info = new Label();
        add(info, BorderLayout.CENTER);
        display = new TextArea(" ", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.SOUTH);

        // 윈도우 이벤트 리스너 등록
        addWindowListener(new WinListener());

        // 윈도우 크기 및 표시 설정
        setSize(300, 250);
        setVisible(true);
    }

    // 서버 실행 메서드
    public void runServer() {
        ServerSocket server;
        Socket sock;
        ServerThread SThread;
        try {
            // 클라이언트 스레드 리스트 초기화 및 서버 소켓 생성
            list = new ArrayList<ServerThread>();
            server = new ServerSocket(5000, 100);
            try {
                // 클라이언트의 연결을 대기하고, 연결되면 클라이언트 스레드를 생성하여 시작
                while (true) { // 서버가 항상 클라이언트의 연결을 대기하도록 함
                    sock = server.accept(); // 클라이언트가 연결되면 소켓 'sock'으로 받아옴
                    SThread = new ServerThread(this, sock, display, info); // 연결된 클라이언트와 통신을 담당할 ServerThread 객체 생성
                    SThread.start(); // 생성한 클라이언트 스레드 시작
                    info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
                }
            } catch (IOException ioe) {
                // 예외 발생 시 서버 종료
                server.close();
                ioe.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // 메인 메서드
    public static void main(String args[]) {
        // 서버 객체 생성 및 실행
        ChatMessageS s = new ChatMessageS();
        s.runServer();
    }
}