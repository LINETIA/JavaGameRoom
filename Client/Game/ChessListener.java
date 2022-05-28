package GameRoom.Client.Game;

import GameRoom.Client.ChatManager;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class ChessListener extends MouseAdapter implements ActionListener{

    private Integer x, y;    // 座標
    private Graphics g;
    private int turn = 0;  // 1は黒の番, -1は白の番
    private int[][] koma;
    private Graphics2D g2d;
    private Chessboard gomoku;
    private WhoWin win;			// 勝ったかどうかをチェックする
    private int cnt = 0;			// 歩数
    private ArrayList<Chess> list = new ArrayList<Chess>();    // 打ったコマをArrayListに格納する
    public String myTurn;
    public String[] whoTurn = {"Black", "White"};

    public ChessListener(Chessboard gomoku) {
        this.gomoku = gomoku;
    }

    public void setMyTurn(String myTurn) {
        this.myTurn = myTurn;
    }

    public void initeTurn() {
        this.whoTurn[0] = "Black";
        this.whoTurn[1] = "White";
    }

    public void changeTurn() {
        String tem;
        tem = whoTurn[0];
        whoTurn[0] = whoTurn[1];
        whoTurn[1] = tem;
    }

    public void initkoma(int[][] koma) {
        this.koma = koma;
        win = new WhoWin(koma);
    }

    public class Chess {

        private int r;
        private int c;

        public Chess(int r,int c){
            this.setR(r);
            this.setC(c);
        }

        public int getR() {
            return r;
        }
        public void setR(int r) {
            this.r = r;
        }
        public int getC() {
            return c;
        }
        public void setC(int c) {
            this.c = c;
        }
    }

    public void actionPerformed(ActionEvent e) {

    }

    // マウスを左クリックすると
    public void mouseClicked(MouseEvent e) {

        x = e.getX();
        y = e.getY();

        for(int r=0;r<Config.ROWS;r++)
        {
            for(int c=0;c<Config.COLUMNS;c++)
            {
                if(koma[r][c] == 0) // その位置にはコマがあるかどうかをチェックする
                {
                    // どこにclickしたかのをチェックする
                    if((Math.abs(x-Config.X0-c*Config.SIZE) < Config.SIZE/3.0)
                            && (Math.abs(y-Config.Y0-r*Config.SIZE) < Config.SIZE/3.0))
                    {
                        // 自分の番かどうかをチェックする
                        if(myTurn != null)
                        {
                            if(whoTurn[0].equals(myTurn))
                            {

                                ChatManager.getCM().setkoma(x, y); // 相手にsetkomaの命令を送る
                                setkoma(x, y, r, c);
                                changeTurn();
                                ChatManager.getCM().changeWindowTurn(whoTurn[0]);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    // boardの初期化
    public void reset(){

        turn = 1;
        cnt = 0;

        for(int r = 0;r<koma.length;r++)
        {
            for(int c = 0;c<koma[r].length;c++)
            {
                koma[r][c]=0;
            }
        }
        gomoku.repaint();
    }

    public void getXY(int x, int y){

        for(int r=0;r<Config.ROWS;r++)
            for(int c=0;c<Config.COLUMNS;c++)
                if(koma[r][c] == 0) // その位置にはコマがあるかどうかをチェックする
                    // どこにclickしたかのをチェックする
                    if((Math.abs(x-Config.X0-c*Config.SIZE) < Config.SIZE/3.0)
                            && (Math.abs(y-Config.Y0-r*Config.SIZE) < Config.SIZE/3.0))
                        setkoma(x, y, r, c);
    }

    // コマを打つ
    public void setkoma(int x,int y, int r, int c) {

        cnt++;		// 歩数++
        g = gomoku.getGraphics();
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(turn == 1)
        {
            // 黒のコマを打つ
            g2d.setColor(Color.BLACK);
            koma[r][c] = 1;
            g2d.fillOval(Config.X0+c*Config.SIZE-Config.KOMA_SIZE/2,Config.Y0+r*Config.SIZE-Config.KOMA_SIZE/2 , Config.KOMA_SIZE, Config.KOMA_SIZE);
            turn = -1; // 次は白の番
            list.add(new Chess(r,c)); // 打ったコマをArrayListに格納する

            if(win.checkWin() == 1)  // 勝ったかどうかをチェックする
            {
                if(myTurn.equals("Black"))
                {
                    ChatManager.getCM().win();
                }
                else {
                    ChatManager.getCM().lose();
                }
                return;
            }
        }
        else if(turn == -1)
        {
            // 白のコマを打つ
            g2d.setColor(Color.WHITE);
            koma[r][c] = -1;
            g2d.fillOval(
                    Config.X0+c*Config.SIZE-Config.KOMA_SIZE/2,
                    Config.Y0+r*Config.SIZE-Config.KOMA_SIZE/2 ,
                    Config.KOMA_SIZE, Config.KOMA_SIZE
            );
            turn = 1; // 次は黒の番
            list.add(new Chess(r,c)); // 打ったコマをArrayListに格納する

            if(win.checkWin() == -1) // 勝ったかどうかをチェックする
            {
                if(myTurn.equals("Black"))
                {
                    ChatManager.getCM().lose();
                }
                else {
                    ChatManager.getCM().win();
                }
                return;
            }
        }
    }

    // どちらが勝ったかを判断する
    public class WhoWin {

        private int[][]	koma;
        public WhoWin(int koma[][]) {
            this.koma = koma;
        }


        public int checkWin() {

            if((rowWin()==1)||(columnWin()==1)||(rightSideWin()==1)||(leftSideWin()==1)) return 1;
            else if((rowWin()==-1)||(columnWin()==-1)||(rightSideWin()==-1)||(leftSideWin()==-1)) return -1;
            return 0;
        }

        public int rowWin() {

            for(int i = 0; i< Config.ROWS; i++)
            {
                for(int j=0;j<Config.COLUMNS-4;j++)
                {
                    if(koma[i][j] == -1)
                    {
                        if(koma[i][j+1] == -1 && koma[i][j+2] == -1 && koma[i][j+3] == -1 && koma[i][j+4] == -1) return -1;
                    }
                    if(koma[i][j] == 1)
                    {
                        if(koma[i][j+1] == 1 && koma[i][j+2] == 1 && koma[i][j+3] == 1 &&koma[i][j+4] == 1) return 1;
                    }
                }
            }
            return 0;
        }

        public int columnWin() {

            for(int i=0;i<Config.ROWS-4;i++)
            {
                for(int j=0;j<Config.COLUMNS;j++)
                {
                    if(koma[i][j] == -1)
                    {
                        if(koma[i+1][j] == -1 && koma[i+2][j] == -1 && koma[i+3][j] == -1 && koma[i+4][j] == -1) return -1;
                    }
                    if(koma[i][j] == 1)
                    {
                        if(koma[i+1][j] == 1 && koma[i+2][j] == 1 && koma[i+3][j] == 1 && koma[i+4][j] == 1) return 1;
                    }
                }
            }
            return 0;
        }

        public int rightSideWin() {

            for(int i=0;i<Config.ROWS-4;i++)
            {
                for(int j=0;j<Config.COLUMNS-4;j++)
                {
                    if(koma[i][j] == -1)
                    {
                        if(koma[i+1][j+1] == -1 && koma[i+2][j+2] == -1 && koma[i+3][j+3] == -1 && koma[i+4][j+4] == -1) return -1;
                    }
                    if(koma[i][j] == 1)
                    {
                        if(koma[i+1][j+1] == 1 && koma[i+2][j+2] == 1 && koma[i+3][j+3] == 1 && koma[i+4][j+4] == 1) return 1;
                    }
                }
            }
            return 0;
        }

        public int leftSideWin() {

            for(int i=4;i<Config.ROWS;i++)
            {
                for(int j=0;j<Config.COLUMNS-4;j++)
                {
                    if(koma[i][j] == -1)
                    {
                        if(koma[i-1][j+1] == -1 && koma[i-2][j+2] == -1 && koma[i-3][j+3] == -1 && koma[i-4][j+4] == -1) return -1;
                    }
                    if(koma[i][j] == 1)
                    {
                        if(koma[i-1][j+1] == 1 && koma[i-2][j+2] == 1 && koma[i-3][j+3] == 1 && koma[i-4][j+4] == 1) return 1;
                    }
                }
            }
            return 0;
        }
    }

}
