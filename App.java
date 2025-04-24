package TwentyFortyEight;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
public class App extends JFrame implements KeyListener {
    private int size;
    private int[][] matrix;
    private JLabel[][] cells;
    private Random random;
    private JLabel timeLabel;
    private Timer gameTimer;
    private int secondsElapsed;

    public App(int size) {
        this.size = size;
        this.matrix = new int[size][size];
        this.cells = new JLabel[size][size];
        this.random = new Random();

        // Initialize the window
        setTitle("2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for title and timer
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0xbbada0));
        timeLabel = new JLabel("Time: 0s", SwingConstants.RIGHT);
        timeLabel.setFont(new Font("Time New Roman", Font.PLAIN, 30));
        timeLabel.setForeground(Color.cyan);
        topPanel.add(timeLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        JPanel gridPanel = new JPanel(new GridLayout(size, size, 5, 5));
        gridPanel.setPreferredSize(new Dimension(500, 500));
        setResizable(false);
        gridPanel.setBackground(new Color(0xbbada0));
        // Create labels and add to frame
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setFont(new Font("Helvetica", Font.BOLD, 40));
                cell.setBackground(new Color(0xcdc1b4)); // Default color for empty tiles
                gridPanel.add(cell);
                cells[i][j] = cell;

                // Add mouse listener to handle hover effects and left-click for spawning
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        Color currentColor = cell.getBackground();
                        // Change color of both empty and non-empty tiles
                        cell.setBackground(lightenColor(currentColor));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // Revert to the original color when mouse exits
                        int value = getCellValue(cell);
                        Color originalColor = getColorForValue(value);
                        cell.setBackground(originalColor);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Left-click event handler to add a 2 or 4 on an empty tile
                        if (e.getButton() == MouseEvent.BUTTON1) { // Left-click
                            int i = findCellIndex(cell)[0]; // Get row index
                            int j = findCellIndex(cell)[1]; // Get column index
                            
                            if (matrix[i][j] == 0) { // Only spawn if the tile is empty
                                matrix[i][j] = random.nextDouble() < 0.5 ? 4 : 2;
                                updateUIBoard(); // Update the board UI
                            }
                        }
                    }
                });
            }
        }
        add(gridPanel, BorderLayout.CENTER);
        secondsElapsed = 0;
        gameTimer = new Timer(1000, e -> {
        secondsElapsed++;
        timeLabel.setText("Time: " + secondsElapsed + "s");
        });
        gameTimer.start();

        // Start the game with two tiles
        addNumber();
        addNumber();
        updateUIBoard();

        // Listen for arrow key presses
        addKeyListener(this);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method to lighten the color by making it slightly lighter
    private Color lightenColor(Color color) {
        int red = (int) Math.min(255, color.getRed() + 15);
        int green = (int) Math.min(255, color.getGreen() + 15);
        int blue = (int) Math.min(255, color.getBlue() + 15);
        return new Color(red, green, blue);
    }
    // Method to get the color based on the tile's value
    private Color getColorForValue(int value) {
        Map<Integer, Color> colors = new HashMap<>();
        colors.put(0, new Color(0xcdc1b4)); // Empty tile color
        colors.put(2, new Color(0xeee4da));
        colors.put(4, new Color(0xede0c8));
        colors.put(8, new Color(0xf2b179));
        colors.put(16, new Color(0xf59563));
        colors.put(32, new Color(0xf67c5f));
        colors.put(64, new Color(0xf65e3b));
        colors.put(128, new Color(0xedcf72));
        colors.put(256, new Color(0xedcc61));
        colors.put(512, new Color(0xedc850));
        colors.put(1024, new Color(0xedc53f));
        colors.put(2048, new Color(0xedc22e));
        colors.put(4096, new Color(0xedc22e));
        return colors.getOrDefault(value, new Color(0x3c3a32)); // Default color for unknown values
    }

    // Helper method to get the value of the tile from the JLabel
    private int getCellValue(JLabel cell) {
        String text = cell.getText();
        if (text.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text);
    }

    // Helper method to find the indices (row and column) of a clicked JLabel
    private int[] findCellIndex(JLabel cell) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] == cell) {
                    return new int[] { i, j };
                }
            }
        }
        return new int[] { -1, -1 }; // Return invalid indices if not found
    }

    //--------------- Matrix logic ---------------//

    private void moveUp() {
        matrix = transpose(merge(transpose(matrix)));
    }

    private void moveDown() {
        matrix = transpose(reverse(merge(reverse(transpose(matrix)))));
    }

    private void moveLeft() {
        matrix = merge(matrix);
    }

    private void moveRight() {
        matrix = reverse(merge(reverse(matrix)));
    }

    private int[][] merge(int[][] mat) {
        int[][] result = new int[size][size];

        for (int r = 0; r < size; r++) {
            ArrayList<Integer> rowVals = new ArrayList<>();
            for (int c = 0; c < size; c++) {
                if (mat[r][c] != 0) {
                    rowVals.add(mat[r][c]);
                }
            }
            for (int c = 0; c < rowVals.size() - 1; c++) {
                if (rowVals.get(c).equals(rowVals.get(c + 1))) {
                    rowVals.set(c, rowVals.get(c) + rowVals.get(c + 1));
                    rowVals.remove(c + 1);
                }
            }
            for (int c = 0; c < size; c++) {
                if (c < rowVals.size()) {
                    result[r][c] = rowVals.get(c);
                } else {
                    result[r][c] = 0;
                }
            }
        }
        return result;
    }

    private int[][] transpose(int[][] mat) {
        int[][] result = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                result[r][c] = mat[c][r];
            }
        }
        return result;
    }

    private int[][] reverse(int[][] mat) {
        int[][] result = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                result[r][c] = mat[r][size - c - 1];
            }
        }
        return result;
    }

    private void addNumber() {
        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
            matrix[cell[0]][cell[1]] = random.nextDouble() < 0.5 ? 4 : 2;
        }
    }

    private void checkState() {
        if (!canMove()) {
            gameTimer.stop();
            JOptionPane.showMessageDialog(this, "GAME OVER");
        }
    }

    private boolean canMove() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (matrix[r][c] == 0) {
                    return true;
                }
                if (c < size - 1 && matrix[r][c] == matrix[r][c + 1]) {
                    return true;
                }
                if (r < size - 1 && matrix[r][c] == matrix[r + 1][c]) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateUIBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int value = matrix[i][j];
                JLabel cell = cells[i][j];

                if (value == 0){
                    cell.setText("");
                } else {
                    cell.setText(String.valueOf(value));
                }
                cell.setBackground(getColorForValue(value));

                if (value == 2 || value == 4) {
                    cell.setForeground(new Color(0x776e65));
                } else {
                    cell.setForeground(new Color(0xf9f6f2));
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int[][] oldMatrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(matrix[i], 0, oldMatrix[i], 0, size);
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                moveUp();
                break;
            case KeyEvent.VK_DOWN:
                moveDown();
                break;
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;
            case KeyEvent.VK_R:
                new App(size);
            default:
                return;
        }

        if (!Arrays.deepEquals(oldMatrix, matrix)) {
            addNumber();
            updateUIBoard();
            checkState();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public static void main(String[] args) {
        int sizeVal = 4;
        try {
            if (args.length == 1) {
                sizeVal = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            sizeVal = 4;
        }
        new App(sizeVal);
    }
}
