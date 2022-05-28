package GameRoom.Client.Game;

import java.awt.*;
import java.awt.event.MouseListener;
import javax.swing.*;

public class Chessboard extends JPanel{

    // •≥•ﬁ§Ú≈‰¡–§«±Ì§π
    private int[][] koma = new int [Config.ROWS][Config.COLUMNS];

    private ChessListener cl;

    public Chessboard(){

        cl = new ChessListener(this);
        cl.initkoma(koma);

        MouseListener[] mls = getMouseListeners();
        if(mls.length > 0)
        {
            removeMouseListener(cl);
        }
        cl.reset();
        addMouseListener(cl);
    }

    public ChessListener getChessListener(){
        return cl;
    }

    public void paint(Graphics g){
        super.paint(g);
        drawChessTable(g);
        reDrawChess(g);
    }

    // chessboard§Ú√Ëª≠§π§Î
    public void drawChessTable(Graphics g) {

        for(int r=0;r<Config.ROWS;r++)
        {
            g.drawLine(Config.X0, Config.Y0+r*Config.SIZE, Config.X0+(Config.COLUMNS-1)*Config.SIZE, Config.Y0+r*Config.SIZE);
        }

        for(int c=0;c<Config.COLUMNS;c++)
        {
            g.drawLine(Config.X0+Config.SIZE*c,Config.Y0, Config.X0+Config.SIZE*c, Config.Y0+(Config.ROWS-1)*Config.SIZE);
        }
    }

    // •≥•ﬁ§Ú√Ëª≠§π§Î
    public void reDrawChess(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(int  r=0;r<Config.ROWS;r++)
        {
            for(int c=0;c<Config.COLUMNS;c++)
            {
                if(koma[r][c] != 0)
                {
                    if(koma[r][c] == 1)
                    {
                        // ¸\
                        g2d.setColor(Color.BLACK);
                        g2d.fillOval(Config.X0+c*Config.SIZE-Config.KOMA_SIZE/2,Config.Y0+r*Config.SIZE-Config.KOMA_SIZE/2 , Config.KOMA_SIZE, Config.KOMA_SIZE);
                    }
                    else if(koma[r][c] == -1)
                    {
                        // ∞◊
                        g2d.setColor(Color.WHITE);
                        g2d.fillOval(Config.X0+c*Config.SIZE-Config.KOMA_SIZE/2,Config.Y0+r*Config.SIZE-Config.KOMA_SIZE/2 , Config.KOMA_SIZE, Config.KOMA_SIZE);
                    }
                }
            }
        }
    }

}
