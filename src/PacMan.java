import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener{
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction){
            this.direction = direction;
            updateVelocity();
        }
        void updateVelocity(){
            if(this.direction == 'U'){
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if (this.direction == 'D'){
                this.velocityX = 0;
                this.velocityX = tileSize/4;
            }
        }
    }
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X     X     X     X",
        "X XXX X XXX X XXX X",
        "X                 X",
        "X  X X  XXX XXX X X",
        "X                 X",
        "X X XX XXXXXXX  XXX",
        "X         r        X",
        "XXX XXXXX XXX XXX XXX",
        "X   X    bpo    X   X",
        "XXX XXX XXXXXXXX XXX",
        "X     X       X     X",
        "X XXXXX XXXX XXXXXXX",
        "X                 X",
        "X XX   XXXXX  XX  X",
        "X  X     P      X X",
        "XXX X XXXX XXX X XXX",
        "X   X   X   X   X   X",
        "X XXX XXX X XXX XXX X",
        "X                  X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;


    PacMan(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();

        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadmap();
        gameLoop = new Timer(50, this);
        gameLoop.start();
        addKeyListener(this);
        setFocusable(true);
        
    }
    
    public void loadmap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++){
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize; // you do * tilesize because each tile is 32px
                int y = r*tileSize;

                    switch(tileMapChar){
                        case 'X': //wall
                            Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                            walls.add(wall);
                        break;
                        case 'b': //blue ghost
                        Block blueGhost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(blueGhost);
                        break;

                    case 'o': //orange ghost
                        Block orangeGhost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(orangeGhost);
                        break;
                     
                    case 'p': //pink ghost
                        Block pinkGhost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(pinkGhost);
                        break;
                        
                    case 'r': //red ghost
                        Block redGhost = new Block(redGhostImage, x, y, tileSize, tileSize);
                        ghosts.add(redGhost);
                        break;

                    case 'P': //Pac-Man
                        pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                        break;

                    case ' ': //food
                        Block food = new Block(null, x + 14, y + 14, 4, 4);
                        foods.add(food);
                        break;

                     }

                }

            }
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            draw(g);
        }

        public void draw(Graphics g){
            g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

            for(Block ghost : ghosts){
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

            }

            for(Block wall : walls){
                g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

            }

            g.setColor(Color.white);
            for(Block food : foods){
                g.fillRect(food.x, food.y, food.width, food.height);

            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("key pressed: " + e.getKeyCode());
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }

