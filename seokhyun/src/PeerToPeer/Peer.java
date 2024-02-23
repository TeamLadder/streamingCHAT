package PeerToPeer;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;

public class Peer {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        // 사용자로부터 username과 port #을 입력받음
        System.out.println("> enter username & port # for this peer: ");
        String[] setupValues = bufferedReader.readLine().split(" ");

        // 서버 스레드를 생성하고 시작
        ServerThread serverThread = new ServerThread(setupValues[1]);
        serverThread.start();

        // Peer 객체의 메서드를 호출하여 피어 간 통신 설정 업데이트
        new Peer().updateListenToPeers(bufferedReader, setupValues[0], serverThread);
    }

    // 피어 간 통신 설정을 업데이트하는 메서드
    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception {
        System.out.println("> enter (space separated) hostname:port#");
        System.out.println("  peers to receive messages from (s to skip):");
        String input = bufferedReader.readLine();
        String[] inputValues = input.split(" ");
        if (!input.equals("s")) {
            for (int i = 0; i < inputValues.length; i++) {
                String[] address = inputValues[i].split(":");
                Socket socket = null;
                try {
                    // 입력 받은 호스트와 포트로 소켓을 생성하고 PeerThread를 시작
                    socket = new Socket(address[0], Integer.valueOf(address[1]));
                    new PeerThread(socket).start();
                } catch (Exception e) {
                    if (socket != null) {
                        socket.close();
                    } else {
                        System.out.println("invalid input. skipping to next step.");
                    }
                }
            }
        }
        // 다음 단계로 진행하여 메시지 통신
        communicate(bufferedReader, username, serverThread);
    }

    // 피어 간 메시지 통신을 담당하는 메서드
    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread) {
        try {
            System.out.println("> you can now communicate (e to exit, c to change)");
            boolean flag = true;
            while (flag) {
                String message = bufferedReader.readLine();
                if (message.equals("e")) {
                    flag = false;
                    break;
                } else if (message.equals("c")) {
                    // 통신 설정을 변경하는 경우 updateListenToPeers 메서드를 호출
                    updateListenToPeers(bufferedReader, username, serverThread);
                } else {
                    // Json 형식의 메시지를 생성하고 서버 스레드를 통해 모든 클라이언트에 전송
                    StringWriter stringWriter = new StringWriter();
                    Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder().add("username", username).add("message", message).build());
                    serverThread.sendMessage(stringWriter.toString());
                }
            }
            // 사용자가 종료를 선택하면 프로그램 종료
            System.exit(0);
        } catch (Exception e) {
            // 예외 발생 시 무시
        }
    }
}
