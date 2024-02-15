package yohan.src.PeerToPeer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class ServerThread extends Thread {
    private ServerSocket serverSocket; // 서버 소켓을 관리하기 위한 변수
    private Set<ServerThreadThread> serverThreadThreads = new HashSet<>(); // 연결된 클라이언트를 관리하는 ServerThreadThread 인스턴스의 집합

    // 생성자: 포트 번호를 받아 서버 소켓을 생성
    public ServerThread(String portNumb) throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(portNumb));
    }

    // 스레드의 주 실행 메소드
    public void run() {
        try {
            while (true) { // 무한 루프를 통해 클라이언트의 연결을 계속해서 수락
                // 새 클라이언트 연결을 수락하고 해당 연결을 처리할 ServerThreadThread 객체 생성
                ServerThreadThread serverThreadThread = new ServerThreadThread(serverSocket.accept(), this);
                serverThreadThreads.add(serverThreadThread); // 생성된 객체를 집합에 추가
                serverThreadThread.start(); // ServerThreadThread 스레드 시작
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
        }
    }

    // 모든 연결된 클라이언트에게 메시지 전송
    void sendMessage(String message) {
        try {
            // serverThreadThreads 집합에 있는 모든 ServerThreadThread 객체에 대해 메시지 전송
            serverThreadThreads.forEach(t -> t.getPrintWriter().println(message));
        } catch (Exception e) {
            e.printStackTrace(); // 메시지 전송 중 예외 발생 시 스택 트레이스 출력
        }
    }

    // 연결된 모든 ServerThreadThread 객체를 반환하는 메소드
    public Set<ServerThreadThread> getServerThreadThreads() {
        return serverThreadThreads;
    }
}