package OneToOneChat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 서버를 나타내는 OneToOneS 클래스.
 */
public class OneToOneS extends Frame implements ActionListener {

    TextArea display;       // 대화 내용을 표시하는 TextArea
    TextField text;         // 사용자가 메시지를 입력하는 TextField
    Label lword;            // 입력창 라벨
    Socket connection;      // 클라이언트와의 연결을 나타내는 소켓
    BufferedWriter output;  // 클라이언트에게 메시지를 보내기 위한 BufferedWriter
    BufferedReader input;   // 클라이언트로부터 메시지를 받기 위한 BufferedReader
    String clientdata = ""; // 클라이언트가 입력한 메시지를 저장할 변수
    String serverdata = ""; // 서버에서 받은 메시지를 저장할 변수

    /**
     * OneToOneS 클래스의 생성자.
     */
    public OneToOneS() {
        super("서버");

        // 대화창 설정
        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY); // 대화창 초기화 및 스크롤바 설정
        display.setEditable(false); // 대화창을 편집 불가능하도록 설정
        add(display, BorderLayout.CENTER); // 프레임의 중앙에 대화창 추가

        // 입력창 설정
        Panel pword = new Panel(new BorderLayout()); // 입력창과 라벨을 담을 패널 생성
        lword = new Label("대화말"); // 입력창 라벨 생성
        text = new TextField(30); // 입력창 생성
        text.addActionListener(this); // 엔터 키 이벤트 처리를 위한 ActionListener 등록
        pword.add(lword, BorderLayout.WEST); // 라벨을 패널의 서쪽에 추가
        pword.add(text, BorderLayout.EAST); // 입력창을 패널의 동쪽에 추가
        add(pword, BorderLayout.SOUTH); // 패널을 프레임의 남쪽에 추가

        // 윈도우 리스너 등록
        addWindowListener(new WinListener()); // 윈도우 이벤트 처리를 위한 리스너 등록

        // 프레임 크기 설정 및 화면 표시
        setSize(300, 200); // 프레임 크기 설정
        setVisible(true); // 프레임을 화면에 표시
    }

    /**
     * 서버를 실행하는 메서드.
     */
    public void runServer() {
        ServerSocket server;
        try {
            // 서버 소켓 생성 및 클라이언트 연결 대기
            server = new ServerSocket(5000, 100); // 5000번 포트에서 서버 소켓 생성
            connection = server.accept(); // 클라이언트와의 연결 대기 및 연결 수락

            // 클라이언트와의 통신을 위한 스트림 설정
            InputStream is = connection.getInputStream(); // 클라이언트에서 서버로의 입력 스트림 생성
            InputStreamReader isr = new InputStreamReader(is);
            input = new BufferedReader(isr);

            OutputStream os = connection.getOutputStream(); // 서버에서 클라이언트로의 출력 스트림 생성
            OutputStreamWriter osw = new OutputStreamWriter(os);
            output = new BufferedWriter(osw);

            // 클라이언트로부터 메시지 수신 및 처리
            while (true) {
                clientdata = input.readLine(); // 클라이언트로부터 한 줄의 메시지를 읽음
                if (clientdata.equals("quit")) {
                    display.append("\n클라이언트 접속 끊겼다잉");
                    output.flush(); // 출력 스트림 버퍼 비우기
                    break;
                } else {
                    display.append("\n클라이언트 메시지: " + clientdata);
                    output.flush(); // 출력 스트림 버퍼 비우기
                }
            }

            // 클라이언트 소켓 닫기
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 입력창에서 엔터키가 눌렸을 때 호출되는 메서드.
     */
    public void actionPerformed(ActionEvent ae) {
        // 입력된 메시지 가져오기
        serverdata = text.getText();

        try {
            // 화면에 메시지 표시 및 클라이언트로 전송
            display.append("\n서버: " + serverdata);
            output.write(serverdata + "\r\n"); // 클라이언트로 메시지 전송
            output.flush(); // 출력 스트림 버퍼 비우기
            text.setText(""); // 입력창 초기화

            // 사용자가 'quit'을 입력한 경우 클라이언트 소켓 닫기
            if (serverdata.equals("quit")) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 창을 닫을 때 종료되는 윈도우 리스너 클래스.
     */
    class WinListener extends WindowAdapter {
        /**
         * 창이 닫힐 때 호출되는 메서드.
         */
        public void windowClosing(WindowEvent e) {
            System.exit(0); // 프로그램 종료
        }
    }
}
