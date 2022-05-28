package GameRoom.Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class ChatSever extends Thread {

    @Override
    public void run(){

        try {

            ServerSocket serverSocket = new ServerSocket(10086);

            while (true)
            {
                //接続を待つ
                Socket socket = serverSocket.accept();

                //接続が確認されたら新しいChatSocketを作る
                ChatSeverSocket css = new ChatSeverSocket(socket);
                css.start();
                ChatManager.getChatManager().addlcss(css);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
