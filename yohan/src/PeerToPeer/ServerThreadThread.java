package yohan.src.PeerToPeer;

import java.io.*;
import java.net.Socket;

public class ServerThreadThread extends Thread {
    private ServerThread serverThread; // 이 스레드를 생성한 ServerThread 인스턴스에 대한 참조
    private Socket socket; // 이 스레드가 관리하는 클라이언트와의 연결 소켓
    private PrintWriter printWriter; // 클라이언트로 메시지를 보내기 위한 PrintWriter

    // 생성자: 클라이언트 연결을 처리할 소켓과 상위 ServerThread 인스턴스를 받아 초기화
    public ServerThreadThread(Socket socket, ServerThread serverThread) {
        this.socket = socket;
        this.serverThread = serverThread;
        // PrintWriter 초기화는 여기서 수행해야 하나, 코드에서 누락됨
    }

    // 스레드 실행 메소드: 클라이언트로부터 메시지를 읽는 로직을 포함해야 하나, 현재 누락됨
    public void run() {
        try {
            // 클라이언트로부터 메시지를 읽기 위한 BufferedReader 초기화
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            // 여기에 클라이언트로부터 메시지를 읽고 처리하는 로직이 필요함
            // 예를 들어, 반복문을 사용하여 메시지를 읽고 serverThread를 통해 다른 클라이언트에게 전달할 수 있음
/**
 * 누락된 부분과 개선 사항:
 * PrintWriter 초기화: 클라이언트로 메시지를 전송하기 위해 PrintWriter 객체를 소켓의 출력 스트림과 연결해야 합니다.
 * 이 초기화는 생성자 또는 run 메소드 내에서 이루어져야 하나, 코드에서는 이 부분이 누락되어 있습니다.
 *
 * 메시지 처리 로직: 클라이언트로부터 메시지를 읽고, 이를 다시 서버를 통해 다른 클라이언트에게 전달하는 로직이
 * run 메소드에 구현되어야 합니다. 현재 run 메소드는 BufferedReader를 초기화만 하고 실제로 클라이언트로부터
 * 메시지를 읽는 부분이 구현되어 있지 않습니다.
 *
 * 예외 처리와 자원 정리: 예외가 발생했을 때, 혹은 스레드가 종료되기 전에 열려 있는 소켓과 스트림을 적절히
 * 닫아주는 자원 정리 로직이 필요합니다. 이는 시스템의 안정성을 보장하고 리소스 누수를 방지하는 데 중요합니다.
 *
 * 이러한 누락된 부분과 개선 사항을 코드에 반영한다면, ServerThreadThread 클래스는 개별 클라이언트와의
 * 통신을 효과적으로 관리할 수 있게 됩니다.
 */
            // PrintWriter도 여기서 초기화해야 함
            this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);

        } catch (Exception e) {
            // 예외 발생 시 현재 스레드를 ServerThread의 스레드 집합에서 제거
            serverThread.getServerThreadThreads().remove(this);
            // 자원 정리 로직도 필요함 (예: socket.close())
        }
    }

    // 클라이언트로 메시지를 보내기 위한 PrintWriter 반환
    public PrintWriter getPrintWriter() {
        return printWriter;
    }
}