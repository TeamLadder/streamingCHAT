package yohan.src.PeerToPeer;

import java.io.*;
import java.net.Socket;
import jakarta.json.JsonObject;
import jakarta.json.Json;

public class PeerThread extends Thread {
    private BufferedReader bufferedReader; // 소켓 입력 스트림으로부터 읽기 위한 BufferedReader

    // 생성자: 소켓을 받아와서 입력 스트림을 기반으로 BufferedReader를 초기화
    public PeerThread(Socket socket) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // 스레드의 실행 메소드. 다른 피어로부터 메시지를 지속적으로 수신
    public void run() {
        boolean flag = true; // 스레드를 계속 실행할지 여부를 결정하는 플래그
        while (flag) {
            try {
                // Json 객체를 읽음. 다른 피어로부터 보내진 메시지를 기대함
                JsonObject jsonObject = Json.createReader(bufferedReader).readObject();
                // jsonObject에 "username" 키가 포함되어 있는지 확인
                if (jsonObject.containsKey("username")) {
                    // 메시지를 콘솔에 출력. 포맷: "[username]: message"
                    System.out.println("[" + jsonObject.getString("username") + "]: " + jsonObject.getString("message"));
                }
            } catch (Exception e) {
                // 예외 발생 시 while 루프를 종료하고 스레드를 중단
                flag = false;
                interrupt(); // 스레드에 인터럽트 신호를 보냄
            }
        }
    }
}