import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


// WindowAdapter : WindowListener의 구현체
//  WindowListener : 윈도우에서 발생하는 이벤트( 창이 열리거나 닫힘) 처리하는 인터페이스
class WinListener extends WindowAdapter {
    public void windowClosing(WindowEvent e) { // 창이 닫힐 때 호출
        System.exit(0); // 프로그램을 정상적으로 종료하는 메서드
    }
}