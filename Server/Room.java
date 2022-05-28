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

    // roomのChatSeverSocketのVectorを作る
    Vector<ChatSeverSocket> roomcss = new Vector<ChatSeverSocket>();
    // roomのChatSeverSocketのインスタンスをVectorに追加する
    public void add(ChatSeverSocket rccs)
    {
        roomcss.add(rccs);
    }
    // roomのChatSeverSocketのインスタンスをVectorから削除する
    public void removercss(ChatSeverSocket rcss)
    {
        roomcss.remove(rcss);
    }

    // 送り手以外のclientにメッセージを送る
    public void send(ChatSeverSocket css, String message)
    {
        for(int i = 0; i < roomcss.size(); ++i)
        {
            ChatSeverSocket tempcss = roomcss.get(i);

            if(!css.equals(tempcss)) // 送り手を除外する
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
