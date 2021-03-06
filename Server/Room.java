package GameRoom.Server;

import java.util.Vector;

public class Room {

    ChatSeverSocket host;
    String roomID;
    String hostname;
    int roomSize;
    String nums; // = css.size();

    public Room(ChatSeverSocket host, Integer roomSize)
    {
        this.host = host;
        this.roomSize = roomSize;
    }

    public void changeHost(ChatSeverSocket posthost) {
        this.host = posthost;
    }

    // roomのChatSeverSocketのVectorを恬る
    Vector<ChatSeverSocket> roomcss = new Vector<ChatSeverSocket>();
    // roomのChatSeverSocketのインスタンスをVectorに弖紗する
    public void add(ChatSeverSocket rccs)
    {
        roomcss.add(rccs);
    }
    // roomのChatSeverSocketのインスタンスをVectorから??茅する
    public void removercss(ChatSeverSocket rcss)
    {
        roomcss.remove(rcss);
    }

    // 僕り返參翌のclientにメッセ?`ジを僕る
    public void send(ChatSeverSocket css, String message)
    {
        for(int i = 0; i < roomcss.size(); ++i)
        {
            ChatSeverSocket tempcss = roomcss.get(i);

            if(!css.equals(tempcss)) // 僕り返を茅翌する
            {
                Integer length = css.getname().length();
                tempcss.out("/roomchat//" + length.toString() + "#" + css.getname() + message);

                System.out.println("send " + "/roomchat//" + length.toString() + "#" + css.getname() + message + " to " + i);
            }
        }
    }

    public String getHostName()
    {
        return host.getname();
    }

    public String getHostAddr()
    {
        return host.getAddr();
    }

}
