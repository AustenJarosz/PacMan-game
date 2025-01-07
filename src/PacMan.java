import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
  class Block {
    int x;
    int y;
    int width;
    int height;
    Image image;

    int startX;
    int startY;
    char direction = 'U'; //U "up", L
    int velocityX = 0;
    int velocityY = 0;

    Block(Image image, int x, int y, int width, int height) {
      this.image = image;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.startX = x;
      this.startY = y;
    }

    void updateDirection(char direction) {
      char prevDirection = this.direction;
      this.direction = direction;
      updateVelocity();
      this.x += this.velocityX;
      this.y += this.velocityY;

      for (Block wall: walls) {
        if (collision(this, wall)) {
          this.x -= this.velocityX;
          this.y -= this.velocityY;
          this.direction = prevDirection;
          updateVelocity();
        }
      }
    }
    void updateVelocity() {
      if (this.direction == 'U') { //if direction is up
        this.velocityX = 0;
        this.velocityY = -tileSize / 4;
      } else if (this.direction == 'D') { //if direction is down
        this.velocityX = 0;
        this.velocityY = tileSize / 4;
      } else if (this.direction == 'L') { //if direction is left
        this.velocityX = -tileSize / 4;
        this.velocityY = 0;
      } else if (this.direction == 'R') { //if direction is right
        this.velocityX = tileSize / 4;
        this.velocityY = 0;
      }
    }

    void reset(){
        this.x = this.startX;
        this.y = this.startY;
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
  //edit this string below to change the map
  private String[] tileMap = {
    "XXXXXXXXXXXXXXXXXXX",
    "X        X        X",
    "X XX XXX X XXX XX X",
    "X                 X",
    "X XX X XXXXX X XX X",
    "X    X       X    X",
    "XXXX XXXX XXXX XXXX",
    "OOOX X       X XOOO",
    "XXXX X XXrXX X XXXX",
    "O       bpo       O",
    "XXXX X XXXXX X XXXX",
    "OOOX X       X XOOO",
    "XXXX X XXXXX X XXXX",
    "X        X        X",
    "X XX XXX X XXX XX X",
    "X  X     P     X  X",
    "XX X X XXXXX X X XX",
    "X    X   X   X    X",
    "X XXXXXX X XXXXXX X",
    "X                 X",
    "XXXXXXXXXXXXXXXXXXX" 
  };

  HashSet < Block > walls;
  HashSet < Block > foods;
  HashSet < Block > ghosts;
  Block pacman;

  Timer gameLoop;
  char[] directions = {'U', 'D', 'L', 'R'};
  Random random = new Random();
  int score = 0;
  int lives = 3;
  boolean gameOver = true;


  PacMan() {
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

    loadMap();
    for (Block ghost : ghosts){
        char newDirection = directions[random.nextInt(4)];
        ghost.updateDirection(newDirection);
    }
    gameLoop = new Timer(50, this);
    gameLoop.start();
    addKeyListener(this);
    setFocusable(true);

  }

  // Initialize the map by creating and placing game objects based on the `tileMap` array
  public void loadMap() {
    // Sets up collections for walls, foods, and ghosts
    walls = new HashSet < Block > ();
    foods = new HashSet < Block > ();
    ghosts = new HashSet < Block > ();

    // Iterate through each row and column of the `tileMap`
    for (int r = 0; r < rowCount; r++) {
      for (int c = 0; c < columnCount; c++) {
        String row = tileMap[r]; // Get the current row.
        char tileMapChar = row.charAt(c); // Get the character at the current column.

        // Calculate screen coordinates for the current tile.
        int x = c * tileSize; // Horizontal position (col * tile size)
        int y = r * tileSize; // Vertical position (row * tile size)

        // Determine what object to place based on the character in the tile map.
        switch (tileMapChar) {
        case 'X': // wall
          Block wall = new Block(wallImage, x, y, tileSize, tileSize);
          walls.add(wall);
          break;
        case 'b': // blue ghost
          Block blueGhost = new Block(blueGhostImage, x, y, tileSize, tileSize);
          ghosts.add(blueGhost);
          break;

        case 'o': // orange ghost
          Block orangeGhost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
          ghosts.add(orangeGhost);
          break;

        case 'p': // pink ghost
          Block pinkGhost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
          ghosts.add(pinkGhost);
          break;

        case 'r': // red ghost
          Block redGhost = new Block(redGhostImage, x, y, tileSize, tileSize);
          ghosts.add(redGhost);
          break;

        case 'P': // Pac-Man
          pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
          break;

        case ' ': // food
          Block food = new Block(null, x + 14, y + 14, 4, 4);
          foods.add(food);
          break;

        }

      }

    }
  }

  // Paint the game screen. Automatically called whenever the screen needs to be updated
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  // Draw all game objects on the screen
  public void draw(Graphics g) {
    // Draw Pac-Man
    g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

    // Draw ghosts
    for (Block ghost: ghosts) {
      g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

    }

    // Draw walls
    for (Block wall: walls) {
      g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

    }

    //Draw foods
    g.setColor(Color.white);
    for (Block food: foods) {
      g.fillRect(food.x, food.y, food.width, food.height);

    }

    //score
    if(gameOver){
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Game Over: " + String.valueOf(score), tileSize + 195, tileSize + 120);
        g.drawString("Press Space to play again. ", tileSize + 140, tileSize * 6);
    }else{
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Lives: " + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize - 8, tileSize - 8);
    }
  }

  public void move() {
    pacman.x += pacman.velocityX;
    pacman.y += pacman.velocityY;

    //check wall collisions
    for (Block wall: walls) {
      if (collision(pacman, wall)) {
        pacman.x -= pacman.velocityX;
        pacman.y -= pacman.velocityY;
        break;
      }
    }

    //check ghost collisions
    for (Block ghost: ghosts){
            if(collision(ghost, pacman)){
                lives -= 1;
                resetPositions();
                if(lives == 0){
                    gameOver = true;
                    return;
                }
            }
            if(ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls){
                if(collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods){
            if (collision (pacman, food)){
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);
        
        if(foods.isEmpty()){
            loadMap();
            resetPositions();
        }
    }

  public boolean collision(Block a, Block b) {
    return a.x < b.x + b.width &&
      a.x + a.width > b.x &&
      a.y < b.y + b.height &&
      a.y + a.height > b.y;
  }

  public void resetPositions(){
    pacman.reset();
    pacman.velocityX = 0;
    pacman.velocityY = 0;
    for (Block ghost : ghosts){
        ghost.reset();
        char newDirection = directions[random.nextInt(4)];
        ghost.updateDirection(newDirection);
    }
  }

  // Called whenever an action is performed (e.g, movement)
  @Override
  public void actionPerformed(ActionEvent e) {
    move(); //update positions
    repaint(); //re-paint
    if (gameOver){
        gameLoop.stop();
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // Start moving Pac-Man in the specified direction when a key is pressed
    switch (e.getKeyCode()) {
    case KeyEvent.VK_P:
    
    case KeyEvent.VK_SPACE:
    if(gameOver){
        loadMap();
        resetPositions();
        lives = 3;
        score = 0;
        gameOver = false;
        gameLoop.start();
        }
        break;
    case KeyEvent.VK_UP:
      pacman.direction = 'U';
      pacman.velocityX = 0;
      pacman.velocityY = -tileSize / 4;
      pacman.image = pacmanUpImage;
      break;
    case KeyEvent.VK_DOWN:
      pacman.direction = 'D';
      pacman.velocityX = 0;
      pacman.velocityY = tileSize / 4;
      pacman.image = pacmanDownImage;
      break;
    case KeyEvent.VK_LEFT:
      pacman.direction = 'L';
      pacman.velocityX = -tileSize / 4;
      pacman.velocityY = 0;
      pacman.image = pacmanLeftImage;
      break;
    case KeyEvent.VK_RIGHT:
      pacman.direction = 'R';
      pacman.velocityX = tileSize / 4;
      pacman.velocityY = 0;
      pacman.image = pacmanRightImage;
      break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // Stop Pac-Man's movement when the key is released
    switch (e.getKeyCode()) {
    case KeyEvent.VK_UP:
    case KeyEvent.VK_DOWN:
    case KeyEvent.VK_LEFT:
    case KeyEvent.VK_RIGHT:
      pacman.velocityX = 0;
      pacman.velocityY = 0;
      break;
    }
  }

@Override
public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
}

}