package PeerToPeer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class ServerThread extends Thread {
    private ServerSocket serverSocket;  // 서버 소켓
    private Set<ServerThreadThread> serverThreadThreads = new HashSet<ServerThreadThread>();  // 클라이언트들의 ServerThreadThread 객체를 저장하는 Set

    // 생성자: 포트 번호를 받아서 서버 소켓 생성
    public ServerThread(String portNumb) throws IOException {
        serverSocket = new ServerSocket(Integer.valueOf(portNumb));
    }

    // 스레드 실행 메서드
    public void run() {
        try {
            while (true) {
                // 클라이언트로부터 연결 요청이 들어올 때마다 ServerThreadThread 생성하여 Set에 추가하고 시작
                ServerThreadThread serverThreadThread = new ServerThreadThread(serverSocket.accept(), this);
                serverThreadThreads.add(serverThreadThread);
                serverThreadThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 모든 클라이언트에게 메시지를 전송하는 메서드
    void sendMessage(String message) {
        try {
            // Set에 있는 모든 ServerThreadThread 객체의 PrintWriter를 이용하여 메시지를 전송
            serverThreadThreads.forEach(t -> t.getPrintWriter().println(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ServerThreadThreads Set을 반환하는 메서드
    public Set<ServerThreadThread> getServerThreadThreads() {
        return serverThreadThreads;
    }
}
