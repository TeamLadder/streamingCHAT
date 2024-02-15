package multipleChat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

// GUI 를 이용한 클라이언트 구현 클래스
public class ChatMessageC extends Frame implements ActionListener, KeyListener {
    TextArea display; // 채팅 내용을 표시하는 TextArea
    TextField wtext, ltext; // 메시지 입력 필드(wtext)와 로그인 입력 필드(ltext)
    Label mlbl, wlbl, loglbl; // 채팅 상태를 나타내는 레이블(mlbl), 메시지 입력 레이블(wlbl), 로그인 레이블(loglbl)
    BufferedWriter output; // 서버로 데이터를 보내기 위한 출력 스트림
    BufferedReader input; // 서버로부터 데이터를 받기 위한 입력 스트림
    Socket client; // 클라이언트 소켓
    StringBuffer clientdata; // 클라이언트에서 서버로 보낼 데이터를 저장하는 버퍼
    String serverdata; // 서버에서 받은 데이터
    String ID; // 클라이언트의 아이디

    // 프로토콜 상수 정의
    private static final String SEPARATOR = "|";
    private static final int REQ_LOGON = 1001;
    private static final int REQ_SENDWORDS = 1021;

    // 생성자
    public ChatMessageC() {
        super("클라이언트");

        // 레이아웃 및 컴포넌트 초기화
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

    // 클라이언트 실행 메서드
    public void runClient() {
        try {
            // 서버에 연결
            client = new Socket(InetAddress.getLocalHost(), 5000);
            mlbl.setText("연결된 서버이름 : " + client.getInetAddress().getHostAddress());
            // 입력 및 출력 스트림 초기화
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            clientdata = new StringBuffer(2048);
            mlbl.setText("접속 완료. 사용할 아이디를 입력하세요.");
            while(true) {
                // 서버로부터 데이터 수신 및 화면에 출력
                serverdata = input.readLine();
                display.append(serverdata + "\r\n");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // ActionListener 인터페이스 구현 - 로그인 버튼 클릭 시(이벤트) 호출됨
    public void actionPerformed(ActionEvent ae) {
        // 로그인이 아직 이루어지지 않았을 때
        if(ID == null) {
            // 아이디 입력
            ID = ltext.getText();
            mlbl.setText(ID + "(으)로 로그인 하였습니다.");
            try {
                // 로그인 요청 데이터 생성 및 서버에 전송
                clientdata.setLength(0);
                clientdata.append(REQ_LOGON);
                clientdata.append(SEPARATOR);
                clientdata.append(ID);
                output.write(clientdata.toString() + "\r\n");
                output.flush();
                ltext.setVisible(false); // 로그인 요청을 서버에 전송 후 로그인 입력 필드 숨김
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 메인 메서드
    public static void main(String[] args) {
        ChatMessageC c = new ChatMessageC();
        c.runClient();
    }

    // KeyListener 인터페이스 구현
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = wtext.getText();
            if (ID == null) {
                mlbl.setText("다시 로그인 하세요.");
                wtext.setText("");
            } else {
                try {
                    // 메시지 전송 요청 데이터 생성 및 서버에 전송
                    clientdata.setLength(0);
                    clientdata.append(REQ_SENDWORDS);
                    clientdata.append(SEPARATOR);
                    clientdata.append(ID);
                    clientdata.append(SEPARATOR);
                    clientdata.append(message);
                    output.write(clientdata.toString() + "\r\n");
                    output.flush();
                    wtext.setText(""); // 메시지 입력 필드 초기화
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // KeyListener 인터페이스 구현
    public void keyReleased(KeyEvent ke) {}

    // KeyListener 인터페이스 구현
    public void keyTyped(KeyEvent ke) {}

    // 윈도우 이벤트 리스너 클래스
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent we) {
            // 윈도우가 닫힐 때 소켓 종료
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}
