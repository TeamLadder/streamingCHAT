package multipleChat;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

// 클라이언트와의 통신을 관리하고 채팅 메시지를 전달하는 역할
public class ServerThread extends Thread {
    Socket sock; // 클라이언트와의 통신을 위한 소켓
    BufferedWriter output; // 클라이언트에게 데이터를 보내기 위한 출력 스트림
    BufferedReader input; // 클라이언트로부터 데이터를 받기 위한 입력 스트림
    TextArea display; // 서버 채팅 창
    Label info; // 서버 정보 표시 레이블
    TextField text; // 텍스트 입력 필드
    String clientdata; // 클라이언트로부터 수신한 데이터
    String serverdata = ""; // 서버에서 클라이언트로 보낼 데이터
    ChatMessageS cs; // 채팅 메시지 객체

    // 프로토콜 상수 정의
    private static final String SEPARATOR = "|";
    private static final int REQ_LOGON = 1001;
    private static final int REQ_SENDWORDS = 1021;

    // 생성자
    public ServerThread(ChatMessageS c, Socket s, TextArea ta, Label l) {
        sock = s;
        display = ta;
        info = l;
        cs = c;
        try {
            // 클라이언트와의 통신을 위한 입출력 스트림 초기화
            input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // 스레드 실행 메서드
    public void run() {
        cs.list.add(this); // 현재 스레드를 채팅 메시지 객체에 추가
        try {
            // 클라이언트로부터 데이터를 읽어오는 루프
            while ((clientdata = input.readLine()) != null) {
                // 수신된 데이터를 구분자(SEPARATOR)를 기준으로 분리
                StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
                // 첫 번째 토큰은 명령 코드로 파싱
                int command = Integer.parseInt(st.nextToken());
                // 현재 채팅 서버에 연결된 클라이언트의 수
                int cnt = cs.list.size();
                switch (command) {
                    case REQ_LOGON: {
                        // 클라이언트 로그인 요청 처리
                        String ID = st.nextToken();
                        // 서버 채팅 창에 로그인한 클라이언트 정보 출력
                        display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
                        break;
                    }
                    case REQ_SENDWORDS: {
                        // 클라이언트가 메시지를 보낸 경우 처리
                        String ID = st.nextToken(); // 클라이언트 ID
                        String message = st.nextToken(); // 메시지 내용
                        // 서버 채팅 창에 클라이언트의 메시지 출력
                        display.append(ID + " : " + message + "\r\n");
                        // 현재 연결된 모든 클라이언트에게 메시지 전송
                        for (int i = 0; i < cnt; i++) {
                            ServerThread SThread = (ServerThread) cs.list.get(i);
                            // 각 클라이언트에게 메시지 전송
                            SThread.output.write(ID + " ： " + message + "\r\n");
                            SThread.output.flush();
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 채팅 메시지 객체에서 현재 스레드 제거
        cs.list.remove(this);
        try {
            // 소켓 닫기
            sock.close();
        } catch (IOException ea) {
            ea.printStackTrace();
        }
    }
}