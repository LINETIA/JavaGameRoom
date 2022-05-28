package GameRoom.Client;

import GameRoom.Client.Game.ChessListener;
import GameRoom.Client.MainFrame.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import  java.util.*;
import java.text.*;

public class ChatManager {

    private ChatManager(){} // Singletonパターンを保証する (ChatManagerのinstanceはただ一つの存在)

    private static final ChatManager cm = new ChatManager();

    public static ChatManager getCM(){
        return cm;
    }

    Socket socket;
    LoginFrame window;
    BufferedReader in;
    PrintWriter out;
    boolean disconnect = false;

    public void setWindow(LoginFrame window){
        this.window = window;
    }

    public void connect(String ip, Integer port){

        new Thread()
        {
            @Override
            public void run() {

                try {

                    // コネクションを作る
                    socket = new Socket(ip, port);
                    window.appendText("Connected to " + ip + ", PORT: " + port);

                    in = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()));

                    out = new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            socket.getOutputStream())),true);

                    sleep(100);
                    Integer length = window.getname().length();
                    send("/init//////" + length + "#" + window.getname() + window.getAddr());

                    window.switchToPanel(window.getMainWindow());

                    String message;
                    while(!disconnect)
                    {
                        if((message = in.readLine()) != null)
                        {

                            if(message.substring(0, 11).equals("/createroom"))
                            {
                                // ルームを作る
                                int namelength = 0;
                                String name = null;
                                String roomID = null;

                                for(int i = 11;i < message.length();++i)
                                {
                                    if(message.charAt(i) == '#')
                                    {
                                        namelength = Integer.parseInt(message.substring(11, i));
                                        name = message.substring(i+1, i+1+namelength);
                                        roomID = message.substring(i+1+namelength);
                                        break;
                                    }
                                }

                                window.getrooms().addElement(name + "'s game    " + "RoomID: " + roomID);
                                window.getRoomList().setModel(window.getrooms());
                            }
                            else if(message.substring(0, 11).equals("/joinroom//"))
                            {
                                // プレイヤーを追加する
                                int namelength = 0;
                                String name = null;
                                String roomID = null;

                                for(int i = 11;i < message.length();++i)
                                {
                                    if(message.charAt(i) == '#')
                                    {
                                        namelength = Integer.parseInt(message.substring(11, i));
                                        name = message.substring(i+1, i+1+namelength);
                                        roomID = message.substring(i+1+namelength);
                                        break;
                                    }
                                }

                                window.getplayers().addElement(name);
                                window.getPlayerList().setModel(window.getplayers());
                            }
                            else if(message.substring(0, 11).equals("/joinedroom"))
                            {
                                if(message.substring(11).equals("failed"))
                                {
                                    // 満員の場合
                                    JOptionPane.showMessageDialog(window.getMainWindow(), "This room is full");
                                }
                                else{
                                    // ルームに入れる場合
                                    window.ChangeRoomID(message.substring(11));
                                    window.switchToPanel(window.getRoomWindow());
                                }
                            }
                            else if(message.substring(0, 11).equals("/removeroom"))
                            {
                                // ルームを削除する
                                int namelength = 0;
                                String roomname = "";
                                String roomID = message.substring(11);

                                for(int i = 11;i < message.length();++i)
                                {
                                    if(message.charAt(i) == '#')
                                    {
                                        namelength = Integer.parseInt(message.substring(11, i));
                                        roomname = message.substring(i+1, i+1+namelength);
                                        roomID = message.substring(i+1+namelength);
                                        break;
                                    }
                                }

                                window.getrooms().removeElement(roomname + "'s game    " + "RoomID: " + roomID);
                                window.getRoomList().setModel(window.getrooms());
                            }
                            else if(message.substring(0, 11).equals("/quitroom//"))
                            {
                                // プレイヤーを削除する
                                String name = message.substring(11);

                                window.getplayers().removeElement(name);
                                window.getPlayerList().setModel(window.getplayers());
                            }
                            else if(message.substring(0, 11).equals("/youkicked/"))
                            {
                                window.getplayers().removeAllElements();
                                window.getPlayerList().setModel(window.getplayers());
                                window.switchToPanel(window.getMainWindow());
                            }
                            else if(message.substring(0, 11).equals("/lobbychat/"))
                            {
                                // メッセージの処理
                                Integer namelength = 0;
                                String name = null;

                                for(int i = 0;i < message.length();++i)
                                {
                                    if(message.charAt(i) == '#')
                                    {
                                        namelength = Integer.parseInt(message.substring(11, i));
                                        name = message.substring(i+1, i+1+namelength);
                                        message = message.substring(i+1+namelength);
                                        break;
                                    }
                                }

                                Date now = new Date();
                                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                                window.appendText(name + "   " + ft.format(now));
                                window.appendText(message + "\n");

                            }
                            else if(message.substring(0, 11).equals("/roomchat//"))
                            {
                                // メッセージの処理
                                Integer namelength = 0;
                                String name = null;

                                for(int i = 0;i < message.length();++i)
                                {
                                    if(message.charAt(i) == '#')
                                    {
                                        namelength = Integer.parseInt(message.substring(11, i));
                                        name = message.substring(i+1, i+1+namelength);
                                        message = message.substring(i+1+namelength);
                                        break;
                                    }
                                }

                                Date now = new Date();
                                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                                window.appendRoomText(name + "   " + ft.format(now));
                                window.appendRoomText(message + "\n");

                            }
                            else if(message.substring(0, 11).equals("/startgame/"))
                            {
                                window.getChessBoard().getChessListener().reset();
                                // 番目をリセットする
                                window.getChessBoard().getChessListener().initeTurn();
                                window.changeTurn("Black");
                                window.isWin = false;


                                // 番目をセットする
                                if(message.substring(11, 16).equals("Black"))
                                {
                                    window.getChessBoard().getChessListener().setMyTurn("Black");
                                    window.black.setText("Black: " + window.getname());
                                    window.black.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 20));
                                    window.white.setText("White: " + message.substring(16));
                                    window.white.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 20));
                                }
                                else if(message.substring(11, 16).equals("White"))
                                {
                                    window.getChessBoard().getChessListener().setMyTurn("White");
                                    window.black.setText("Black: " + message.substring(16));
                                    window.black.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 20));
                                    window.white.setText("White: " + window.getname());
                                    window.white.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 20));
                                }

                                window.switchToPanel(window.getGomoku());
                            }
                            else if(message.substring(0, 11).equals("/setkoma///"))
                            {
                                Integer x = null;
                                Integer y = null;

                                for(int i = 0;i < message.length();++i)
                                {
                                    if(message.charAt(i) == '#')
                                    {
                                        x = Integer.parseInt(message.substring(11, i));
                                        y = Integer.parseInt(message.substring(i+1));
                                        break;
                                    }
                                }

                                window.getChessBoard().getChessListener().getXY(x, y);

                                // 番目を変更する
                                window.getChessBoard().getChessListener().changeTurn();
                                window.changeTurn(window.getChessBoard().getChessListener().whoTurn[0]);
                            }
                            else if(message.substring(0, 11).equals("/gg////////"))
                            {
                                win();
                            }
                        }
                    }

                    //ここからは接続が閉めたので
                    System.out.println("closing...");
                    socket.close();
                    window.appendText("Disconnected");

                } catch (IOException e) {
                    window.appendText("Cannot conncect to server");
                    e.printStackTrace();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void send(String message){

        if(out != null){

            out.println(message);
            out.flush();

        }else{
            window.appendText("接続はもう中断しました");
        }
    }

    public void setkoma(Integer x, Integer y){

        send("/setkoma///" + window.getRoomID() + "#" + x.toString() + "#" + y.toString());
    }
    public void changeWindowTurn(String str){

        window.changeTurn(str);
    }
    public void win() {

        window.isWin = true;
        JOptionPane.showMessageDialog(window.getChessBoard(), "You Win !");
        window.getChessBoard().getChessListener().myTurn = null;
        window.getChessBoard().getChessListener().reset();
        window.switchToPanel(window.getRoomWindow());

        // 番目をリセットする
        window.getChessBoard().getChessListener().initeTurn();
    }
    public void lose() {

        window.isWin = false;
        JOptionPane.showMessageDialog(window.getChessBoard(), "You Lose !");
        window.getChessBoard().getChessListener().myTurn = null;
        window.getChessBoard().getChessListener().reset();
        window.switchToPanel(window.getRoomWindow());

        // 番目をリセットする
        window.getChessBoard().getChessListener().initeTurn();
    }

    public void disconnect()
    {
        out.println("/disconnect");
        out.flush();
        disconnect = true;
    }
}
