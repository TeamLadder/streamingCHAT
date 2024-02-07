package multipleChat;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerThread extends Thread{
    Socket sock;
    BufferedWriter output;
    BufferedReader input;
    TextArea display;
    Label info;
    TextField text;
    String clientdata;
    String serverdata = "";
    ChatMessageS cs;

    private static final String SEPARATOR = "|";
    private static final int REQ_LOGON = 1001;
    private static final int REQ_SENDWORDS = 1021;

    public ServerThread(ChatMessageS c, Socket s, TextArea ta, Label l){
        sock = s;
        display = ta;
        info = l;
        cs = c;
        try {
            input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    public void run() {
        cs.list.add(this);
        try {
            while ((clientdata = input.readLine() )!= null) {
                StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
                int command = Integer.parseInt(st.nextToken());
                int cnt = cs.list.size();
                switch (command) {
                    case REQ_LOGON: {
                        String ID = st.nextToken();
                        display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
                        break;
                    }
                    case REQ_SENDWORDS: { // "102l I 아이디 I 대화말*를 수신
                        String ID = st.nextToken();
                        String message = st.nextToken();
                        display.append(ID + " : " + message + "\r\n");
                        for (int i = 0; i < cnt; i++) { // 모든 클라이언트에 전송
                            ServerThread SThread = (ServerThread) cs.list.get(i);
                            SThread.output.write(ID + " ： " + message + "\r\n");
                            SThread.output.flush();

                        }
                        break;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        cs.list.remove(this);
        try {
            sock.close();
        } catch (IOException ea) {
            ea.printStackTrace();
        }
    }

}
