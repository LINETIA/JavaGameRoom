package GameRoom.Client;

import GameRoom.Client.MainFrame.LoginFrame;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run()
            {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                    ChatManager.getCM().setWindow(frame);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
