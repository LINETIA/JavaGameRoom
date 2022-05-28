package GameRoom.Server;
import java.io.*;
import java.net.Socket;

public class ChatSeverSocket extends Thread
{
    Socket socket;
    String username = "NoName";
    String addr = "";
    BufferedReader in;
    PrintWriter out;
    Room joinedRoom = null;

    public ChatSeverSocket(Socket sk)
    {
        this.socket = sk;
    }

    public void out(String message)
    {
        out.println(message);
    }

    public String getname()
    {
        return username;
    }
    public String getAddr()
    {
        return addr;
    }

    @Override
    public void run()
    {
        try {

            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())),true);

            // 先にRoomの情報を送る
            for(int i = 0;i < ChatManager.getChatManager().rooms.size();++i)
            {
                String roomID = ChatManager.getChatManager().rooms.get(i).roomID;
                ChatSeverSocket temp  = ChatManager.getChatManager().rooms.get(i).host;
                Integer length = temp.getname().length();
                this.out("/createroom" + length.toString() + "#" + temp.getname() + roomID);
            }

            String message = null;

            while( (message = in.readLine()) != null ) //String messegeにメッセージを読み取ってnullかどうかをチックする
            {
                if(message.substring(0, 11).equals("/joinroom//")) {

                    ChatManager.getChatManager().joinroom(this, message.substring(11));
                }
                else if(message.substring(0, 11).equals("/createroom")) {

                    ChatManager.getChatManager().createroom(this);
                }
                else if(message.substring(0, 11).equals("/disconnect")) {

                    ChatManager.getChatManager().removelcss(this);
                }
                else if(message.substring(0, 11).equals("/quitroom//"))
                {
                    String roomID = message.substring(11);
                    ChatManager.getChatManager().quitroom(this, roomID);
                }
                else if(message.substring(0, 11).equals("/roomkick//"))
                {
                    int namelength = 0;
                    String roomID = null;
                    String name = null;
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

                    ChatManager.getChatManager().roomkick(name, roomID);
                }
                else if(message.substring(0, 11).equals("/init//////")) {

                    int namelength = 0;
                    for(int i = 11;i < message.length();++i)
                    {
                        if(message.charAt(i) == '#')
                        {
                            namelength = Integer.parseInt(message.substring(11, i));
                            username = message.substring(i+1, i+1+namelength);
                            addr = message.substring(i+1+namelength);
                            break;
                        }
                    }

                    System.out.println("Connected with " + this.getname());
                }
                else if(message.substring(0, 11).equals("/lobbychat/"))
                {
                    ChatManager.getChatManager().send(this, message.substring(11));
                }
                else if(message.substring(0, 11).equals("/roomchat//"))
                {
                    int numberlength = 0;
                    String roomID = null;
                    for(int i = 11;i < message.length();++i)
                    {
                        if(message.charAt(i) == '#')
                        {
                            roomID = message.substring(11, i);
                            message = message.substring(i+1);
                            break;
                        }
                    }

                    // roomを特定してメッセージを送る
                    Room selectedRoom = null;
                    for(int i = 0;i < ChatManager.getChatManager().rooms.size();i++)
                    {
                        if(ChatManager.getChatManager().rooms.get(i).roomID.equals(roomID))
                            selectedRoom = ChatManager.getChatManager().rooms.get(i);
                    }
                    selectedRoom.send(this, message);
                }
                else if(message.substring(0, 11).equals("/startgame/"))
                {
                    ChatManager.getChatManager().startgame(this, message.substring(11));
                }
                else if(message.substring(0, 11).equals("/setkoma///"))
                {
                    String roomID = null;
                    String x = null;
                    String y = null;

                    for(int i = 11;i < message.length();++i)
                    {
                        if(message.charAt(i) == '#')
                        {
                            roomID = message.substring(11, i);
                            message = message.substring(i+1);
                            break;
                        }
                    }

                    for(int i = 0;i < message.length();++i)
                    {
                        if(message.charAt(i) == '#')
                        {
                            x = message.substring(0, i);
                            y = message.substring(i+1);
                            break;
                        }
                    }

                    ChatManager.getChatManager().setkoma(this, roomID, x, y);
                }
                else if(message.substring(0, 11).equals("/gg////////"))
                {
                    String roomID = message.substring(11);
                    ChatManager.getChatManager().gg(this, roomID);

                }
            }

            //ここからは接続が閉めたので
            System.out.println("closing...");
            socket.close();

        } catch (IOException e) {

            if(joinedRoom != null)
            {
                ChatManager.getChatManager().quitroom(this, joinedRoom.roomID);
            }
            ChatManager.getChatManager().removelcss(this);
            System.out.println("Removed " + this.username);
            e.printStackTrace();
        }
    }

}
