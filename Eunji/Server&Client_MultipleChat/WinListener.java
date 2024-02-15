package multipleChat;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WinListener extends WindowAdapter {
    public  void WindowClosing(WindowEvent e){
        System.exit(0);
    }
}
