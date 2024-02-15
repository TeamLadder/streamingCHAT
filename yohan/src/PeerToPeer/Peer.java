package yohan.src.PeerToPeer;

import java.io.*;
import java.net.Socket;
import jakarta.json.Json;

public class Peer {
    // 메인 메소드: 프로그램 실행의 시작점
    public static void main(String[] args) throws Exception {
        // 사용자 입력을 위한 BufferedReader 준비
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        // 사용자에게 사용자 이름과 포트 번호 입력 요청
        System.out.println("> enter username & port # for this peer: ");
        // 사용자 입력을 공백으로 분리하여 배열에 저장
        String[] setupValues = bufferedReader.readLine().split(" ");
        // 서버 스레드 생성 및 시작
        ServerThread serverThread = new ServerThread(setupValues[1]);
        serverThread.start();
        // 다른 피어와의 연결 업데이트 및 메시지 송수신 시작
        new Peer().updateListenToPeers(bufferedReader, setupValues[0], serverThread);
    }

    // 다른 피어와의 연결을 업데이트하는 메소드
    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception {
        // 사용자에게 다른 피어의 호스트네임과 포트 번호 입력 요청
        System.out.println("> enter (space separated) hostname:port#");
        System.out.println("  peers to receive messages from (s to skip):");
        String input = bufferedReader.readLine();
        String[] inputValues = input.split(" ");
        // 사용자가 's'를 입력하지 않은 경우, 입력된 주소로 소켓 연결 시도
        if (!input.equals("s")) {
            for (String inputValue : inputValues) {
                String[] address = inputValue.split(":");
                Socket socket = null;
                try {
                    // 주어진 호스트네임과 포트로 새 소켓 생성 및 PeerThread 시작
                    socket = new Socket(address[0], Integer.parseInt(address[1]));
                    new PeerThread(socket).start();
                } catch (Exception e) {
                    // 연결 실패 시 소켓 닫기
                    if (socket != null) {
                        socket.close();
                    } else {
                        System.out.println("invalid input. skipping to next step.");
                    }
                }
            }
        }
        // 메시지 송수신 시작
        communicate(bufferedReader, username, serverThread);
    }

    // 메시지 송수신을 처리하는 메소드
    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread) {
        try {
            // 사용자에게 메시지 입력, 종료, 연결 정보 변경 요청
            System.out.println("> you can now communicate (e to exit, c to change)");
            boolean flag = true;
            while (flag) {
                String message = bufferedReader.readLine();
                // 'e' 입력 시 프로그램 종료
                if ("e".equals(message)) {
                    flag = false;
                    // 'c' 입력 시 다른 피어로의 연결 정보 업데이트
                } else if ("c".equals(message)) {
                    updateListenToPeers(bufferedReader, username, serverThread);
                    // 그 외의 메시지는 JSON 형식으로 변환 후 전송
                } else {
                    StringWriter stringWriter = new StringWriter();
                    Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder().add("username", username).add("message", message).build());
                    serverThread.sendMessage(stringWriter.toString());
                }
            }
            // 정상 종료
            System.exit(0);
        } catch (Exception e) {
            // 오류 처리는 특별히 구현되지 않음
        }
    }
}