import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
    //Variables to control size of the field
    private final int B_WIDTH = 1920;// private final int B_WIDTH = 300;
    private final int B_HEIGHT = 1080;// private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 20736; //private final int ALL_DOTS = 900;
    //Made size of board scalable
    private final int RAND_POS_X = B_WIDTH / 10; //private final int RAND_POS = 29;
    private final int RAND_POS_Y = B_HEIGHT / 10;
    private int DELAY = 70;
    //Renamed Variables
    private final int[] snakeX = new int[ALL_DOTS];
    private final int[] snakeY = new int[ALL_DOTS];

    private int dots;
    //Changed code to allow for 10 Apples
    //TODO add way for user to adjust apples
    private int[] appleX = new int[10];
    private int[] appleY = new int[10];

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {

        initBoard();
    }

    //TODO add obstacles, likely fixed and maybe change with 'levels' later on
    //TODO add score system, and save scores and high scores
    //TODO add ability for user to select amount of apples that spawn
    //TODO add game modes, Speed increase with size, or select difficulty, higher speed
    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }

    private void initGame() {
        //Starting size of Snake
        dots = 10;
        //Starting Position of Snake
        for (int z = 0; z < dots; z++) {
            snakeX[z] = 150 - z * 10;
            snakeY[z] = 150;
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        if (inGame) {
            for (int i = 0; i < 10; i++) {
                g.drawImage(apple, appleX[i], appleY[i], this);
            }
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, snakeX[z], snakeY[z], this);
                } else {
                    g.drawImage(ball, snakeX[z], snakeY[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }

    //TODO add restart
    private void gameOver(Graphics g) {

        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        //add(new Board());
        /*class RestartKey extends KeyAdapter{

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
                    initBoard();
                } else {
                    System.exit(0);
                }
            }
        }*/
    }

    private void checkApple() {
        for (int i = 0; i < 10; i++) {
            if ((snakeX[0] == appleX[i]) && (snakeY[0] == appleY[i])) {

                dots++;
                locateApple();

            }
        }
    }

    private void move() {

        for (int z = dots; z > 0; z--) {
            snakeX[z] = snakeX[(z - 1)];
            snakeY[z] = snakeY[(z - 1)];
        }

        if (leftDirection) {
            snakeX[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            snakeX[0] += DOT_SIZE;
        }

        if (upDirection) {
            snakeY[0] -= DOT_SIZE;
        }

        if (downDirection) {
            snakeY[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (snakeX[0] == snakeX[z]) && (snakeY[0] == snakeY[z])) {
                inGame = false;
            }
        }

        if (snakeY[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (snakeY[0] < 0) {
            inGame = false;
        }

        if (snakeX[0] >= B_WIDTH) {
            inGame = false;
        }

        if (snakeX[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {
        for (int i = 0; i < 10; i++) {
            int r = (int) (Math.random() * RAND_POS_X);
            appleX[i] = ((r * DOT_SIZE));

            r = (int) (Math.random() * RAND_POS_Y);
            appleY[i] = ((r * DOT_SIZE));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            //Added Speed Control with plus and minus keys
            if (key == KeyEvent.VK_SUBTRACT) {
                timer.setDelay(DELAY += 10);
                if (DELAY > 150) { //Check to make sure speed cannot go lower than 150
                    DELAY = 150;
                }
                if (key == KeyEvent.VK_ADD) {
                    timer.setDelay(DELAY -= 10);
                    if (DELAY < 20) { //Check to make sure speed cannot go higher than 20
                        DELAY = 20;
                    }
                }
            }
        }
    }
}

