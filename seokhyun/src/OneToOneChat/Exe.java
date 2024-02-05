package OneToOneChat;

/**
 * 프로그램 실행을 위한 Exe 클래스.
 */
public class Exe {
    public static void main(String[] args) {
        // 서버와 클라이언트 객체 생성
        OneToOneS s = new OneToOneS(); // 서버 객체 생성
        OneToOneC c = new OneToOneC(); // 클라이언트 객체 생성

        // 서버와 클라이언트를 각각 별도의 스레드에서 실행
        Thread serverThread = new Thread(() -> s.runServer()); // 서버 스레드 생성 및 실행 메서드 설정
        Thread clientThread = new Thread(() -> c.runClient()); // 클라이언트 스레드 생성 및 실행 메서드 설정

        // 각각의 스레드 시작
        serverThread.start(); // 서버 스레드 시작
        clientThread.start(); // 클라이언트 스레드 시작
    }
}

