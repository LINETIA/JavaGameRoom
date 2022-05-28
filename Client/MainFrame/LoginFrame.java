/*
 * Created by JFormDesigner on Fri Jun 19 03:28:41 JST 2020
 */

package GameRoom.Client.MainFrame;

import GameRoom.Client.ChatManager;
import GameRoom.Client.Game.ChessListener;
import GameRoom.Client.Game.Chessboard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import  java.util.*;
import java.text.*;

/**
 * @author unknown
 */
public class LoginFrame extends JFrame {

    Integer cnt = 0;
    String name;
    String addr;
    String serverIP;
    String roomID;
    boolean isHost = false;
    DefaultListModel rooms = new DefaultListModel();
    DefaultListModel players = new DefaultListModel();
    CardLayout cl = new CardLayout();
    boolean joined = false;
    public boolean isWin = false;
    private Chessboard chessBoard;

    public void appendText(String message){
        txt.append("\n" + message);
    }
    public void appendRoomText(String message){
        roomText.append("\n" + message);
    }

    public void switchToPanel(JPanel panel) {

        parentcard.removeAll();
        parentcard.add(panel);
        parentcard.repaint();
        parentcard.revalidate();
    }

    public String getname(){
        return name;
    }
    public String getAddr(){
        return addr;
    }
    public String getRoomID(){
        return roomID;
    }
    public boolean getisHost(){
        return isHost;
    }
    public void ChangeRoomID(String roomID){
        this.roomID = roomID;
    }
    public void initChessboard(){

        chessBoard = new Chessboard();
    }
    
    public DefaultListModel getrooms(){
        return rooms;
    }
    public DefaultListModel getplayers(){
        return players;
    }

    public JPanel getMainWindow(){
        return MainWindow;
    }
    public JPanel getRoomWindow(){
        return RoomWindow;
    }
    public JPanel getGomoku(){
        return Gomoku;
    }
    public Chessboard getChessBoard() { return chessBoard; }

    public JList getRoomList(){
        return RoomList;
    }
    public JList getPlayerList(){
        return playerList;
    }
    public void changeTurn(String str) {
        turn.setText("Turn: " + str);
        turn.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 16));
    }

    public LoginFrame() {
        initComponents();
    }
    
    private void sendMouseClicked(MouseEvent e) {
        
        //メッセージを送る
        ChatManager.getCM().send("/lobbychat/" + maininput.getText());

        //自身のwindowにもメッセージをprintする
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

        appendText(name + "   " + ft.format(now));
        appendText(maininput.getText() + "\n");
        maininput.setText("");
        
    }

    private void disconnectMouseClicked(MouseEvent e) {
        cnt = 0;
        ChatManager.getCM().disconnect();
    }

    private void JoinRoomMouseClicked(MouseEvent e) {

        // ルーム情報からroomIDを切り取る
        String roominfo = RoomList.getSelectedValue().toString();
        String roomID = null;
        for(int i = 0;i < roominfo.length();++i)
        {
            if(roominfo.charAt(i) == ':')
            {
                roomID = roominfo.substring(i+2);
                break;
            }
        }

        // 加入房间
        if(joined == false) {
            ChatManager.getCM().send("/joinroom//" + roomID);
            joined = true;
        }
        else if(joined == true)
        {
            joined = false;
        }
    }

    private void CreateRoomMouseClicked(MouseEvent e) {
        
        ChatManager.getCM().send("/createroom");
        isHost = true;
        switchToPanel(RoomWindow);
    }

    private void entryMouseClicked(MouseEvent e) {

        addr = ip.getText();
        serverIP = serverAddr.getText();
        name = username.getText();
        ChatManager.getCM().connect(serverIP, 10086);
    }

    private void roomSendMouseClicked(MouseEvent e) {
        //メッセージを送る
        ChatManager.getCM().send("/roomchat//" + roomID + "#" + roominput.getText());

        //自身のwindowにもメッセージをprintする
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

        appendRoomText(name + "   " + ft.format(now));
        appendRoomText(roominput.getText() + "\n");
        roominput.setText("");
    }

    private void testgameMouseClicked(MouseEvent e) {
        switchToPanel(Gomoku);
    }

    private void quitMouseClicked(MouseEvent e) {

        ChatManager.getCM().send("/quitroom//" + roomID);
        players.removeAllElements();
        playerList.setModel(players);
        isHost = false;
        switchToPanel(MainWindow);
    }

    private void kickMouseClicked(MouseEvent e) {

        // isHostかどうかをcheckする
        if(isHost)
        {
            String playerinfo = playerList.getSelectedValue().toString();

            // 自分自身かどうかをチェックする
            if(! playerinfo.equals(this.name))
                ChatManager.getCM().send("/roomkick//" + playerinfo.length() + "#" + playerinfo + roomID);
        }
    }

    private void startgameMouseClicked(MouseEvent e) {
        
        // isHostかどうかをcheckする
        if(isHost) ChatManager.getCM().send("/startgame/" + roomID);
    }
    
    private void GGMouseClicked(MouseEvent e) {

        if(isWin)
        {
            chessBoard.getChessListener().myTurn = null;
            JOptionPane.showMessageDialog(chessBoard, "You Win !");
            switchToPanel(RoomWindow);
            chessBoard.getChessListener().reset();
            chessBoard.getChessListener().initeTurn();
            isWin = false;
        }
        else {
            chessBoard.getChessListener().myTurn = null;
            JOptionPane.showMessageDialog(chessBoard, "You Lose !");
            ChatManager.getCM().send("/gg////////" + roomID);
            switchToPanel(RoomWindow);
            chessBoard.getChessListener().reset();
            chessBoard.getChessListener().initeTurn();
            isWin = false;
        }
    }

    private void surrenderMouseClicked(MouseEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        parentcard = new JPanel();
        Login = new JPanel();
        entry = new JButton();
        username = new JTextField();
        label1 = new JLabel();
        label2 = new JLabel();
        ip = new JTextField();
        label5 = new JLabel();
        serverAddr = new JTextField();
        MainWindow = new JPanel();
        Chat = new JScrollPane();
        txt = new JTextArea();
        maininput = new JTextField();
        send = new JButton();
        Rooms = new JScrollPane();
        RoomList = new JList();
        JoinRoom = new JButton();
        CreateRoom = new JButton();
        label4 = new JLabel();
        RoomWindow = new JPanel();
        roominput = new JTextField();
        roomChat = new JScrollPane();
        roomText = new JTextArea();
        roomSend = new JButton();
        scrollPane1 = new JScrollPane();
        playerList = new JList();
        kick = new JButton();
        startgame = new JButton();
        label3 = new JLabel();
        quit = new JButton();
        Gomoku = new JPanel();
        chessBoard = new Chessboard();
        GameInfo = new JPanel();
        black = new JLabel();
        white = new JLabel();
        turn = new JLabel();
        GG = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== parentcard ========
        {
            parentcard.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new
            javax.swing.border.EmptyBorder(0,0,0,0), "JF\u006frmDes\u0069gner \u0045valua\u0074ion",javax
            .swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM,new java
            .awt.Font("D\u0069alog",java.awt.Font.BOLD,12),java.awt
            .Color.red),parentcard. getBorder()));parentcard. addPropertyChangeListener(new java.beans.
            PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e){if("\u0062order".
            equals(e.getPropertyName()))throw new RuntimeException();}});
            parentcard.setLayout(new CardLayout());

            //======== Login ========
            {

                //---- entry ----
                entry.setText("Login");
                entry.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        entryMouseClicked(e);
                    }
                });

                //---- label1 ----
                label1.setText("Username");

                //---- label2 ----
                label2.setText("IP");

                //---- label5 ----
                label5.setText("Server IP");

                GroupLayout LoginLayout = new GroupLayout(Login);
                Login.setLayout(LoginLayout);
                LoginLayout.setHorizontalGroup(
                    LoginLayout.createParallelGroup()
                        .addGroup(LoginLayout.createSequentialGroup()
                            .addGap(237, 237, 237)
                            .addGroup(LoginLayout.createParallelGroup()
                                .addGroup(LoginLayout.createSequentialGroup()
                                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(ip, GroupLayout.PREFERRED_SIZE, 298, GroupLayout.PREFERRED_SIZE))
                                .addGroup(LoginLayout.createSequentialGroup()
                                    .addGroup(LoginLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(label1, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                        .addComponent(label5, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(LoginLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(username, GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                                        .addComponent(entry, GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                                        .addComponent(serverAddr, GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))))
                            .addContainerGap(276, Short.MAX_VALUE))
                );
                LoginLayout.setVerticalGroup(
                    LoginLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, LoginLayout.createSequentialGroup()
                            .addContainerGap(166, Short.MAX_VALUE)
                            .addGroup(LoginLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                                .addComponent(ip, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(LoginLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label5, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                                .addComponent(serverAddr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(LoginLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                .addComponent(username, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(42, 42, 42)
                            .addComponent(entry, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
                            .addGap(109, 109, 109))
                );
            }
            parentcard.add(Login, "card1");

            //======== MainWindow ========
            {

                //======== Chat ========
                {

                    //---- txt ----
                    txt.setText("Ready...");
                    Chat.setViewportView(txt);
                }

                //---- send ----
                send.setText("Send");
                send.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        sendMouseClicked(e);
                    }
                });

                //======== Rooms ========
                {
                    Rooms.setViewportView(RoomList);
                }

                //---- JoinRoom ----
                JoinRoom.setText("Join room");
                JoinRoom.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JoinRoomMouseClicked(e);
                        JoinRoomMouseClicked(e);
                    }
                });

                //---- CreateRoom ----
                CreateRoom.setText("Create room");
                CreateRoom.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        CreateRoomMouseClicked(e);
                    }
                });

                //---- label4 ----
                label4.setText("Rooms");

                GroupLayout MainWindowLayout = new GroupLayout(MainWindow);
                MainWindow.setLayout(MainWindowLayout);
                MainWindowLayout.setHorizontalGroup(
                    MainWindowLayout.createParallelGroup()
                        .addGroup(MainWindowLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(MainWindowLayout.createParallelGroup()
                                .addGroup(MainWindowLayout.createSequentialGroup()
                                    .addGroup(MainWindowLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(Rooms, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                                        .addGroup(MainWindowLayout.createSequentialGroup()
                                            .addComponent(maininput, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(send, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)))
                                    .addGap(4, 4, 4))
                                .addGroup(MainWindowLayout.createSequentialGroup()
                                    .addComponent(label4, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 541, Short.MAX_VALUE)))
                            .addGroup(MainWindowLayout.createParallelGroup()
                                .addGroup(MainWindowLayout.createSequentialGroup()
                                    .addComponent(CreateRoom, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(JoinRoom, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE))
                                .addComponent(Chat, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap())
                );
                MainWindowLayout.setVerticalGroup(
                    MainWindowLayout.createParallelGroup()
                        .addGroup(MainWindowLayout.createSequentialGroup()
                            .addGap(13, 13, 13)
                            .addComponent(label4, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(MainWindowLayout.createParallelGroup()
                                .addComponent(Rooms, GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                                .addComponent(Chat, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(MainWindowLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(JoinRoom, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                .addComponent(send, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                                .addComponent(CreateRoom, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
                                .addComponent(maininput, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap())
                );
            }
            parentcard.add(MainWindow, "card2");

            //======== RoomWindow ========
            {

                //======== roomChat ========
                {
                    roomChat.setViewportView(roomText);
                }

                //---- roomSend ----
                roomSend.setText("Send");
                roomSend.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        roomSendMouseClicked(e);
                    }
                });

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(playerList);
                }

                //---- kick ----
                kick.setText("Kick");
                kick.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        kickMouseClicked(e);
                    }
                });

                //---- startgame ----
                startgame.setText("StartGame");
                startgame.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        startgameMouseClicked(e);
                    }
                });

                //---- label3 ----
                label3.setText("Players");

                //---- quit ----
                quit.setText("Quit");
                quit.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        quitMouseClicked(e);
                    }
                });

                GroupLayout RoomWindowLayout = new GroupLayout(RoomWindow);
                RoomWindow.setLayout(RoomWindowLayout);
                RoomWindowLayout.setHorizontalGroup(
                    RoomWindowLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(RoomWindowLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(RoomWindowLayout.createParallelGroup()
                                .addGroup(RoomWindowLayout.createSequentialGroup()
                                    .addComponent(roominput, GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(roomSend, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))
                                .addGroup(RoomWindowLayout.createSequentialGroup()
                                    .addComponent(label3, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(RoomWindowLayout.createParallelGroup()
                                .addGroup(RoomWindowLayout.createSequentialGroup()
                                    .addComponent(kick)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(startgame, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(roomChat)
                                .addGroup(GroupLayout.Alignment.TRAILING, RoomWindowLayout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(quit, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap())
                );
                RoomWindowLayout.setVerticalGroup(
                    RoomWindowLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, RoomWindowLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(RoomWindowLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(quit))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(RoomWindowLayout.createParallelGroup()
                                .addGroup(RoomWindowLayout.createSequentialGroup()
                                    .addComponent(roomChat, GroupLayout.PREFERRED_SIZE, 462, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(RoomWindowLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(startgame, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                        .addComponent(kick, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                        .addComponent(roomSend, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)))
                                .addGroup(RoomWindowLayout.createSequentialGroup()
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 462, GroupLayout.PREFERRED_SIZE)
                                    .addGap(5, 5, 5)
                                    .addComponent(roominput, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap())
                );
            }
            parentcard.add(RoomWindow, "card3");

            //======== Gomoku ========
            {

                //======== chessBoard ========
                {
                    chessBoard.setBackground(new Color(255, 204, 51));

                    GroupLayout chessBoardLayout = new GroupLayout(chessBoard);
                    chessBoard.setLayout(chessBoardLayout);
                    chessBoardLayout.setHorizontalGroup(
                        chessBoardLayout.createParallelGroup()
                            .addGap(0, 601, Short.MAX_VALUE)
                    );
                    chessBoardLayout.setVerticalGroup(
                        chessBoardLayout.createParallelGroup()
                            .addGap(0, 596, Short.MAX_VALUE)
                    );
                }

                //======== GameInfo ========
                {

                    //---- black ----
                    black.setText("Black :");
                    black.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 20));

                    //---- white ----
                    white.setText("White :");
                    white.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 20));

                    //---- turn ----
                    turn.setText("Turn:");
                    turn.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 20));

                    //---- GG ----
                    GG.setText("GG");
                    GG.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 24));
                    GG.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            surrenderMouseClicked(e);
                            GGMouseClicked(e);
                        }
                    });

                    GroupLayout GameInfoLayout = new GroupLayout(GameInfo);
                    GameInfo.setLayout(GameInfoLayout);
                    GameInfoLayout.setHorizontalGroup(
                        GameInfoLayout.createParallelGroup()
                            .addGroup(GameInfoLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(GameInfoLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(GG, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                    .addComponent(black, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(white, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(turn, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                    GameInfoLayout.setVerticalGroup(
                        GameInfoLayout.createParallelGroup()
                            .addGroup(GameInfoLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(black, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(white, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(turn, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
                                .addComponent(GG, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                    );
                }

                GroupLayout GomokuLayout = new GroupLayout(Gomoku);
                Gomoku.setLayout(GomokuLayout);
                GomokuLayout.setHorizontalGroup(
                    GomokuLayout.createParallelGroup()
                        .addGroup(GomokuLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(chessBoard, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(GameInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                );
                GomokuLayout.setVerticalGroup(
                    GomokuLayout.createParallelGroup()
                        .addGroup(GomokuLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(GomokuLayout.createParallelGroup()
                                .addComponent(GameInfo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chessBoard, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addContainerGap())
                );
            }
            parentcard.add(Gomoku, "card4");
        }
        contentPane.add(parentcard, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JPanel parentcard;
    private JPanel Login;
    private JButton entry;
    private JTextField username;
    private JLabel label1;
    private JLabel label2;
    private JTextField ip;
    private JLabel label5;
    private JTextField serverAddr;
    private JPanel MainWindow;
    private JScrollPane Chat;
    private JTextArea txt;
    private JTextField maininput;
    private JButton send;
    private JScrollPane Rooms;
    private JList RoomList;
    private JButton JoinRoom;
    private JButton CreateRoom;
    private JLabel label4;
    private JPanel RoomWindow;
    private JTextField roominput;
    private JScrollPane roomChat;
    private JTextArea roomText;
    private JButton roomSend;
    private JScrollPane scrollPane1;
    private JList playerList;
    private JButton kick;
    private JButton startgame;
    private JLabel label3;
    private JButton quit;
    private JPanel Gomoku;
    private JPanel GameInfo;
    public JLabel black;
    public JLabel white;
    private JLabel turn;
    private JButton GG;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}



