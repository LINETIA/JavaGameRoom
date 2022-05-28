package GameRoom.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args){

        new ChatSever().start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true){

            try {

                String in = reader.readLine();
                ChatManager.getChatManager().broadcast(in);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}