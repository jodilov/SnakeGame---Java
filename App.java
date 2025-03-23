import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int boardWidth = 600;
        int boardHeight = boardWidth;

        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Ensure focus on game panel
        SwingUtilities.invokeLater(() -> snakeGame.requestFocusInWindow());

        // Set visible as the last step
        frame.setVisible(true);
    }
}
