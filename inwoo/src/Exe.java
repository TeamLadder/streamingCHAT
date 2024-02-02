public class Exe {
    public static void main(String[] args) {
        OneToOneS s = new OneToOneS();
        OneToOneC c = new OneToOneC();

        // 서버와 클라이언트를 각각 별도의 스레드에서 실행
        Thread serverThread = new Thread(() -> s.runServer());
        Thread clientThread = new Thread(() -> c.runClient());

        serverThread.start();
        clientThread.start();
    }
}
