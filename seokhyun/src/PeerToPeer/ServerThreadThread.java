package PeerToPeer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThreadThread extends Thread {
    private ServerThread serverThread;  // ServerThread 클래스를 참조하는 멤버 변수
    private Socket socket;  // 클라이언트와의 통신을 위한 Socket 객체
    private PrintWriter printWriter;  // 클라이언트에게 메시지를 보내기 위한 PrintWriter 객체

    // 생성자: Socket과 ServerThread 객체를 받아서 멤버 변수에 할당
    public ServerThreadThread(Socket socket, ServerThread serverThread) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    // 스레드 실행 메서드
    public void run() {
        try {
            // 클라이언트로부터 메시지를 읽어오기 위한 BufferedReader 생성
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // 클라이언트에게 메시지를 보내기 위한 PrintWriter 생성
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);

            // 무한 루프를 돌면서 클라이언트로부터 받은 메시지를 ServerThread의 sendMessage 메서드를 통해 모든 클라이언트에게 전달
            while(true)
                serverThread.sendMessage(bufferedReader.readLine());
        } catch (Exception e) {
            // 예외 발생 시 해당 클라이언트를 ServerThread에서 제거
            serverThread.getServerThreadThreads().remove(this);
        }
    }

    // PrintWriter 객체를 반환하는 메서드
    public PrintWriter getPrintWriter() {
        return printWriter;
    }
}
