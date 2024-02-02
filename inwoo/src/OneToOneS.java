import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class OneToOneS extends Frame implements ActionListener {
    TextArea display;   // 클라이언트와의 대화 내용을 표시할 TextArea
    TextField text;     // 서버에서 입력한 데이터를 입력받을 TextField
    Label lword;        // 입력 필드 옆에 표시되는 레이블
    Socket connection;  // 클라이언트와의 소켓 연결
    BufferedWriter output;  // 클라이언트로 데이터를 전송하기 위한 BufferedWriter
    BufferedReader input;   // 클라이언트로부터 데이터를 수신하기 위한 BufferedReader
    String clientdata = "";  // 클라이언트로부터 수신한 데이터 저장 변수
    String serverdata = "";  // 서버에서 입력한 데이터 저장 변수

    public OneToOneS() {
        // 서버 창 제목 설정
        super("서버");

        // 대화 내용을 표시할 TextArea 생성, 설정
        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.CENTER);

        // 입력 필드와 레이블을 담을 패널 생성
        Panel pword = new Panel(new BorderLayout());

        // 레이블 생성 및 텍스트 설정
        lword = new Label("대화말");

        // 입력 데이터를 받을 텍스트 필드 생성
        text = new TextField(30);

        // 입력된 데이터를 전송하기 위한 이벤트 연결
        text.addActionListener(this);

        // 패널에 레이블과 텍스트 필드 추가
        pword.add(lword, BorderLayout.WEST);
        pword.add(text, BorderLayout.EAST);

        // 패널을 창의 아래쪽에 추가
        add(pword, BorderLayout.SOUTH);

        // 창 닫기 이벤트 처리를 위한 리스너 추가
        addWindowListener(new WinListener());

        // 창 크기 설정
        setSize(300, 200);

        // 창을 보이도록 설정
        setVisible(true);
    }

    // 서버를 실행하는 메서드
    public void runServer() {
        ServerSocket server;
        try {
            server = new ServerSocket(5000, 100);
            connection = server.accept();   // 클라이언트의 연결을 기다림
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            input = new BufferedReader(isr);
            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            output = new BufferedWriter(osw);
            while (true) {
                // 클라이언트로부터 데이터를 수신
                String clientdata = input.readLine();
                if (clientdata.equals("quit")) {
                    display.append("\n클라이언트와의 접속이 중단되었습니다");
                    output.flush();
                    break;
                } else {
                    display.append(("\n클라이언트 메시지 : " + clientdata));
                    output.flush();
                }
            }
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 사용자가 데이터를 입력하고 엔터를 눌렀을 때 호출되는 메서드
    public void actionPerformed(ActionEvent ae) {
        serverdata = text.getText();  // 입력된 데이터를 가져옴
        try {
            display.append("\n서버 : " + serverdata);
            output.write(serverdata + "\r\n");  // 클라이언트로 데이터 전송
            output.flush();
            text.setText("");  // 입력 필드 초기화
            if (serverdata.equals("quit")) {
                connection.close();  // 클라이언트와의 연결 종료
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        OneToOneS s = new OneToOneS();
//        s.runServer();
//    }

    // 창 닫기 이벤트를 처리하는 내부 클래스
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}
