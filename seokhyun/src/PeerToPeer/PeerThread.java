package PeerToPeer;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PeerThread extends Thread {
    private BufferedReader bufferedReader;  // 클라이언트로부터 데이터를 읽어오기 위한 BufferedReader 객체

    // 생성자: Socket을 받아 BufferedReader 초기화
    public PeerThread(Socket socket) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // 스레드 실행 메서드
    public void run() {
        boolean flag = true;  // 스레드 동작을 제어하는 플래그
        while (flag) {
            try {
                // 클라이언트로부터 받은 JSON 데이터를 읽어와서 JsonObject로 파싱
                JsonObject jsonObject = Json.createReader(bufferedReader).readObject();

                // 만약 JsonObject에 "username" 키가 포함되어 있다면 해당 정보를 출력
                if (jsonObject.containsKey("username")) {
                    System.out.println("[" + jsonObject.getString("username") + "]: " + jsonObject.getString("message"));
                }
            } catch (Exception e) {
                // 예외가 발생하면 스레드 종료
                flag = false;
                interrupt();
            }
        }
    }
}
