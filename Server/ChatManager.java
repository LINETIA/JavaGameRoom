package GameRoom.Server;
import java.net.InetSocketAddress;
import  java.util.*;

public class ChatManager {

    private ChatManager(){} // Singletonパターンを保証する (ChatManagerのinstanceはただ一つの存在)

    private static final ChatManager cm = new ChatManager();

    public static ChatManager getChatManager()
    {
        return cm;
    }

    private Integer roomIDCounter = 0;

    // LobbyのChatSeverSocketのVectorを作る
    Vector<ChatSeverSocket> lobbycss = new Vector<ChatSeverSocket>();
    // LobbyのChatSeverSocketのインスタンスをVectorに追加する
    public void addlcss(ChatSeverSocket lcss)
    {
        lobbycss.add(lcss);
    }
    // LobbyのChatSeverSocketのインスタンスをVectorから削除する
    public void removelcss(ChatSeverSocket lcss)
    {
        lobbycss.remove(lcss);
    }

    // Roomの可変長配列を作る
    Vector<Room> rooms = new Vector<Room>();
    // RoomのインスタンスをVectorに追加する

    // 送り手以外のclientにメッセージを送る
    public void send(ChatSeverSocket css, String message)
    {
        for(int i = 0; i < lobbycss.size(); ++i)
        {
             ChatSeverSocket tempcss = lobbycss.get(i);

             if(!css.equals(tempcss)) // 送り手を除外する
             {
                 Integer length = css.getname().length();
                 tempcss.out("/lobbychat/" + length.toString() + "#" + css.getname() + message);
                 System.out.println("sended " + "/lobbychat/" + length.toString() + "#" + css.getname() + message + " to " + i);
             }
        }

    }

    // ターミナルにメッセージを入力して、すべてのClientに送る
    public void broadcast(String message)
    {
        for(int i = 0; i < lobbycss.size(); ++i)
        {
            ChatSeverSocket tempcss = lobbycss.get(i);
            tempcss.out("/lobbychat/" + "6" + "#" + "Server" + message);
            System.out.println("send " + message + " to " + i);
        }
    }

    // createroomの命令を処理する
    public void createroom(ChatSeverSocket css)
    {
        // 2人のルームを作る
        Room room = new Room(css, 2);
        rooms.add(room);
        room.roomID = roomIDCounter.toString();
        roomIDCounter++;
        room.hostname = css.getname();

        // ホストプレイヤーをルームに追加する
        joinroom(css, room.roomID);

        // すべてのClientにルーム情報を送る
        for(int i = 0; i < lobbycss.size(); ++i)
        {
            ChatSeverSocket tempcss = lobbycss.get(i);
            Integer length = css.getname().length();
            tempcss.out("/createroom" + length.toString() + "#" + css.getname() + room.roomID);
        }

        System.out.println("created a room by " + css.getname() + ", roomNumber: " + room.roomID);
    }

    // joinroomの命令を処理する
    public void joinroom(ChatSeverSocket css, String roomID)
    {
        // roomを特定する
        Room selectedRoom = null;
        for(int i = 0;i < rooms.size();++i)
        {
            if(rooms.get(i).roomID.equals(roomID))
                selectedRoom = rooms.get(i);
        }

        // 入れるかどうかを判断する (満員かどうか)
        if(selectedRoom.roomcss.size() == selectedRoom.roomSize)
        {
            css.out("/joinedroom" + "failed");
        }
        else {

            // 多重の加入を認めない
            if(selectedRoom.roomcss.contains(css)) return;

            // 指定されたルームに加入したいプレイヤーをそのルームに追加する
            selectedRoom.add(css);

            // ルームにいるほかのプレーヤーの情報を送る
            for(int i = 0; i < selectedRoom.roomcss.size(); ++i)
            {
                ChatSeverSocket tempcss = selectedRoom.roomcss.get(i);

                if(!css.equals(tempcss)) // 送り手を除外する
                {
                    Integer length = tempcss.getname().length();
                    css.out("/joinroom//" + length.toString() + "#" + tempcss.getname() + tempcss.getAddr());
                }
            }

            // ルームに属するClientにこのプレイヤー情報を送る
            for(int i = 0; i < selectedRoom.roomcss.size(); ++i)
            {
                ChatSeverSocket tempcss = selectedRoom.roomcss.get(i);
                Integer length = css.getname().length();
                tempcss.out("/joinroom//" + length.toString() + "#" + css.getname() + css.getAddr());
            }

            // 確認情報を送る
            css.out("/joinedroom" + roomID);
            css.joinedRoom = selectedRoom;

            System.out.println(css.getname() + "from" + css.getAddr() + "has just joined the room" + roomID);
        }
    }

    // quitroomの命令を処理する
    public void quitroom(ChatSeverSocket css, String roomID)
    {
        // roomを特定する
        Room selectedRoom = null;
        for(int i = 0;i < rooms.size();++i)
        {
            if(rooms.get(i).roomID.equals(roomID))
                selectedRoom = rooms.get(i);
        }

        // プレイヤーを削除する
        selectedRoom.removercss(css);

        // ルームに属するClientにプレイヤー情報を送る
        for(int i = 0; i < selectedRoom.roomcss.size(); ++i)
        {
            ChatSeverSocket tempcss = selectedRoom.roomcss.get(i);
            tempcss.out("/quitroom//" + css.getname());
        }

        // hostがquitしたらroomにいるすべてのplayerをkickする
        if(css.equals(selectedRoom.host))
        {
            for(int i = 0; i < selectedRoom.roomcss.size(); ++i)
            {
                ChatSeverSocket selectedcss = selectedRoom.roomcss.get(i);
                selectedcss.out("/youkicked/");
                selectedRoom.removercss(selectedcss);
            }
        }

        // roomにはプレイヤーがもう一人もいない場合roomを削除する
        if(selectedRoom.roomcss.size() == 0)
        {
            rooms.remove(selectedRoom);
            String roomname = selectedRoom.hostname;
            Integer namelength = roomname.length();

            for(int i = 0; i < lobbycss.size(); ++i)
            {
                ChatSeverSocket tempcss = lobbycss.get(i);
                tempcss.out("/removeroom" + namelength.toString() + "#" + roomname + selectedRoom.roomID);
            }

            System.out.println("removed the room " + selectedRoom.roomID);
        }

        css.joinedRoom = null;

        System.out.println(css.getname() + "from" + css.getAddr() + "has just quited the room" + selectedRoom.roomID);
    }


    // changehostの命令を処理する
    public void changehost(ChatSeverSocket css, String roomID)
    {

    }

    // roomkickの命令を処理する
    public void roomkick(String name, String roomID)
    {
        // roomを特定する
        Room selectedRoom = null;
        for(int i = 0;i < rooms.size();++i)
        {
            if(rooms.get(i).roomID.equals(roomID))
                selectedRoom = rooms.get(i);
        }

        // kickしたいプレイヤーを探す
        for(int i = 0; i < selectedRoom.roomcss.size(); ++i)
        {
            if(selectedRoom.roomcss.get(i).getname().equals(name))
            {
                ChatSeverSocket selectedcss = selectedRoom.roomcss.get(i);
                selectedcss.out("/youkicked/");
                quitroom(selectedcss, roomID);
            }
        }
    }

    // startgameの命令を処理する
    public void startgame(ChatSeverSocket css, String roomID)
    {
        // roomを特定する
        Room selectedRoom = null;
        for(int i = 0;i < rooms.size();++i)
        {
            if(rooms.get(i).roomID.equals(roomID))
                selectedRoom = rooms.get(i);
        }

        // startgameの命令と番目の情報と相手の名前をすべてのプレイヤーに送る
        for(int i = 0; i < selectedRoom.roomcss.size(); ++i)
        {
            ChatSeverSocket selectedcss = selectedRoom.roomcss.get(i);
            if(!selectedcss.equals(selectedRoom.host)) {
                selectedRoom.host.out("/startgame/" + "Black" + selectedcss.getname());
                selectedcss.out("/startgame/" + "White" + selectedRoom.host.getname());
            }
        }
    }

    // setkomaの命令を処理する
    public void setkoma(ChatSeverSocket css, String roomID, String x, String y)
    {
        // roomを特定する
        Room selectedRoom = null;
        for(int i = 0;i < rooms.size();++i)
        {
            if(rooms.get(i).roomID.equals(roomID))
                selectedRoom = rooms.get(i);
        }

        // setkomaの命令を送り手以外のプレイヤーに送信する
        for(int i = 0; i < selectedRoom.roomcss.size(); ++i)
        {
            ChatSeverSocket selectedcss = selectedRoom.roomcss.get(i);

            if(!selectedcss.equals(css)) // 送り手を除外する
            {
                selectedcss.out("/setkoma///" + x + "#" + y);
            }
        }
    }

    // ggの命令を処理する ( = googgame, 投了する, 投げる)
    public void gg(ChatSeverSocket css, String roomID)
    {
        // roomを特定する
        Room selectedRoom = null;
        for(int i = 0;i < rooms.size();++i)
        {
            if(rooms.get(i).roomID.equals(roomID))
                selectedRoom = rooms.get(i);
        }

        // ggの命令を送り手以外のプレイヤーに送信する
        for(int i = 0; i < selectedRoom.roomcss.size(); ++i)
        {
            ChatSeverSocket selectedcss = selectedRoom.roomcss.get(i);

            if(!selectedcss.equals(css)) // 送り手を除外する
            {
                selectedcss.out("/gg////////");
            }
        }
    }
}
