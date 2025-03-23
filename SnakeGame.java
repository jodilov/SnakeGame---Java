import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x, y;
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    // Game logic
    int velocityX;
    int velocityY;
    Timer gameLoop;
    boolean gameOver = false;
    boolean isPaused = false;
    boolean canChangeDirection = true; // Prevent multiple key inputs in one frame

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        // Game Timer
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Grid Lines
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Food
        g.setColor(Color.red);
        g.fillOval(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        // Snake Head
        g.setColor(Color.green);
        g.fillOval(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        // Snake Body
        for (Tile snakePart : snakeBody) {
            g.fillOval(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }

        // Score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over! Score: " + snakeBody.size(), tileSize, tileSize);
            g.drawString("Press 'R' to Restart", tileSize, tileSize * 2);
        } else if (isPaused) {
            g.setColor(Color.yellow);
            g.drawString("Paused", tileSize, tileSize);
        } else {
            g.drawString("Score: " + snakeBody.size(), tileSize, tileSize);
        }
    }

    public void placeFood() {
        do {
            food.x = random.nextInt(boardWidth / tileSize);
            food.y = random.nextInt(boardHeight / tileSize);
        } while (isFoodOnSnake());
    }

    private boolean isFoodOnSnake() {
        if (collision(food, snakeHead)) return true;
        for (Tile snakePart : snakeBody) {
            if (collision(food, snakePart)) return true;
        }
        return false;
    }

    public void move() {
        if (gameOver || isPaused) return;

        // Eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
            increaseSpeed();
        }

        // Move snake body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) { // Move the first body part to head's position
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Check for collisions
        for (Tile snakePart : snakeBody) {
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        // Check boundary collision
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize ||
            snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver = true;
        }

        canChangeDirection = true; // Allow new movement input after moving
    }

    public void increaseSpeed() {
        if (snakeBody.size() % 5 == 0) { // Every 5 points
            gameLoop.setDelay(Math.max(50, gameLoop.getDelay() - 5)); // Speed up, minimum 50ms delay
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void resetGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        velocityX = 1;
        velocityY = 0;
        gameOver = false;
        isPaused = false;
        canChangeDirection = true;
        placeFood();
        gameLoop.setDelay(100); // Reset speed
        gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) gameLoop.stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (canChangeDirection) {
                if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
                    velocityX = 0;
                    velocityY = -1;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
                    velocityX = 0;
                    velocityY = 1;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
                    velocityX = -1;
                    velocityY = 0;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
                    velocityX = 1;
                    velocityY = 0;
                }
                canChangeDirection = false; // Prevent multiple changes per frame
            }

            // Pause
            if (e.getKeyCode() == KeyEvent.VK_P) {
                isPaused = !isPaused;
                if (isPaused) {
                    gameLoop.stop();
                } else {
                    gameLoop.start();
                }
            }
        }

        // Restart game
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame(500, 500);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
