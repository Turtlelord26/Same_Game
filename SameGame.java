package reference;

/* Extra Credit Numbers 1 and 2 implemented
 * Extra Credit 1 features:
 *  Star Game implemented
 *  Same Game implemented
 * Extra Credit 2 features:
 *  Window starting size is dynamic based on the number of rows & columns; buttons are square if possible.
 *  Beveled borders on the JButtons that switch from outward to inward when buttons are grayed
 *  Score system that scales to the number of blocks greyed in one click, with a JLabel to display it
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Class implements versions of the Same Game
 * @author James Talbott
 */
public class SameGame {
  
  /**
   * Field stores the array of JButtons that will form the game board
   */
  private static JButton blocks[][];
  
  /**
   * Field stores the game mode and is set by the main method, determines which block greying method(s) to call
   * 1 - Cross Game, 2 - Star Game, 3 - Same Game
   */
  private static int gameMode = 1;
  
  /**
   * Field stores the score
   */
  private static int score = 0;
  
  /**
   * Field stores the raised border of colored buttons, to avoid generating new ones many times.
   */
  private static BevelBorder colorBorder = new BevelBorder(0);
  
  /**
   * Field stores the lowered border of grey buttons, to avoid generating new ones many times.
   */
  private static BevelBorder greyBorder = new BevelBorder(1);
  
  /**
   * Field stores the score display
   */
  private static JLabel header = new JLabel("", 0);
  
  /**
   * The main method
   * @param args  the command line
   */
  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        gameMode = 1;
        SameGame.gameStart(12, 12, 3);
        return;
      }
      if (args.length == 3 &&
          Integer.parseInt(args[0]) > 0 &&
          Integer.parseInt(args[1]) > 0 &&
          Integer.parseInt(args[2]) > 0) {
        gameMode = 1;
        SameGame.gameStart(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        return;
      }
      if (args.length == 4 &&
          Integer.parseInt(args[1]) > 0 &&
          Integer.parseInt(args[2]) > 0 &&
          Integer.parseInt(args[3]) > 0) {
        if ("cross".equals(args[0]))
          gameMode = 1;
        else if ("star".equals(args[0]))
          gameMode = 2;
        else if ("same".equals(args[0]))
          gameMode = 3;
        else {
          errorMessage();
          return;
        }
        SameGame.gameStart(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        return;
      }
      errorMessage();
    }
    catch (NumberFormatException e) {
      errorMessage();
    }
  }
  
  /**
   * Method stores the generic error message for illegal main method command lines.
   */
  private static void errorMessage() {
    System.out.println("Error: Illegal Arguments");
    System.out.println("Accepted command line argument sets are");
    System.out.println("  null");
    System.out.println("  height width colorNumber");
    System.out.println("  gameMode height width colorNumber");
    System.out.println("");
    System.out.println("Where:");
    System.out.println("gameMode is a String matching any of cross, star, and same");
    System.out.println("height and width are positive integers");
    System.out.println("colorNumber is an integer between 1 and 10 inclusive");
  }
  
  /**
   * Method initializes the game board
   * @param height  The number of rows in the board
   * @param width  The number of columns in the board
   * @param numColors  The number of colors to be included. There is a chance less colors will be included due to random number generation.
   */
  public static void gameStart(int height, int width, int numColors) {
    if (numColors > 10)
      numColors = 10;
    //reset score, as it is a static field
    score = 0;
    header.setText("Click the JButtons!! Your score to be added shortly.");
    //The preset orange doesn't stand out well vs. the preset yellow.
    Color orange = new Color(255, 150, 0);
    //The preset pink doesn't stand out well vs. the preset white.
    Color pink = new Color(205, 125, 125);
    Color[] colors = {Color.red, Color.blue, Color.green, Color.yellow, orange, Color.magenta,
      Color.black, Color.white, Color.cyan, pink};
    //The board
    JFrame frame = new JFrame("The Same Game");
    //The component that will contain the buttons
    JPanel board = new JPanel(new GridLayout(height, width));
    //The button array
    blocks = new JButton[height][width];
    //An ActionListener to call later so that new ActionListeners do not have to be continuously generated
    ActionListener actlis = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SameGame.actionPerformed(e);
      }
    };
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (Exception e) {
    }
    //A single loop generates the board, row by row
    for (int i = 0; i < height * width; i++) {
      int row = i / width;
      int column = i % width;
      blocks[row][column] = new JButton();
      blocks[row][column].setBackground(colors[(int) (Math.random() * numColors)]);
      blocks[row][column].addActionListener(actlis);
      blocks[row][column].setBorder(colorBorder);
      board.add(blocks[row][column]);;
    }
    //final initialization operations
    frame.add(board, "Center");
    frame.add(header, "North");
    frame.setSize(width * 40 - 5, height * 40 + 40);
    frame.setVisible(true);
  }
  
  /**
   * Method triggers on a button click and performs a variety of actions depending on the conditions
   * Method calls other methods to grey blocks based on the gameMode field
   * Method updates the score and calls methods to move blocks downward and left.
   * @param e  the click.
   */
  public static void actionPerformed(ActionEvent e) {
    //Variable stores the button that was pressed
    JButton b = (JButton) e.getSource();
    //row and column serve as placeholding variables to locate b
    int row = 0;
    int column = 0;
    //Loop determines whether each button in the array, in sequence, is equal to b
    //Loop exits when it finds b, (i + 1) statements prevent an off-by-one error.
    for (int i = 0; b != blocks[i / blocks[0].length][i % blocks[0].length]; i++) {
      row = (i + 1) / blocks[0].length;
      column = (i + 1) % blocks[0].length;
    }
    //Variable stores the pressed button's color
    Color color = blocks[row][column].getBackground();
    //Cuts down on useless method calls and loop iterations, and prevents sameMode infinite recursion and score inflation
    if (color == Color.lightGray)
      return;
    int blocksDeleted = 0;
    //calls the button greying out method for vertical and horizontal adjacents
    if (gameMode == 1 || gameMode == 2) {
      blocksDeleted = blocksDeleted + crossMode(row, column, color);
    }
    //calls the button greying out method for diagonal adjacents
    if (gameMode == 2) {
      blocksDeleted = blocksDeleted + starMode(row, column, color, blocksDeleted);
    }
    //calls the button greying out method for contiguous masses of blocks, but only if more than 1 block can be greyed
    if (gameMode == 3 && ((row < blocks.length - 1 && blocks[row + 1][column].getBackground() == color) ||
                          (row > 0 && blocks[row - 1][column].getBackground() == color) ||
                          (column < blocks[row].length - 1 && blocks[row][column + 1].getBackground() == color) ||
                          (column > 0 && blocks[row][column - 1].getBackground() == color))) {
      blocksDeleted = sameMode(row, column, color, 0);
    }
    //updates the score
    if (blocksDeleted != 0 && blocksDeleted != 1)
      score = score + ((blocksDeleted) * (blocksDeleted - 1)) / 2;
    header.setText("Your score: " + score);
    //calls the method to make colors advance downwards
    blockFall();
    //calls the method to make whole columns advance leftwards
    blockSlide();
  }
  
  /**
   * Method causes blocks to appear to fall to "replace" greyed blocks below
   */
  private static void blockFall() {
    //row and column serve as placeholding variables in the loops below
    int row = 0;
    int column = 0;
    //outer loop runs through the grid from the top, searching for grey blocks directly below color blocks.
    for (int i = 0; i < blocks.length * blocks[0].length; i++) {
      row = i / blocks[0].length;
      column = i % blocks[0].length;
      if (row > 0 && blocks[row][column].getBackground() == Color.lightGray &&
          blocks[row - 1][column].getBackground() != Color.lightGray) {
        //inner loop causes grey blocks to move up as far as possible (i.e. the colors slide down)
        for (int rowAdvancer = row;
             rowAdvancer > 0 && blocks[rowAdvancer][column].getBackground() == Color.lightGray &&
             blocks[rowAdvancer - 1][column].getBackground() != Color.lightGray;
             rowAdvancer--) {
          blocks[rowAdvancer][column].setBackground(blocks[rowAdvancer - 1][column].getBackground());
          blocks[rowAdvancer - 1][column].setBackground(Color.lightGray);
          blocks[rowAdvancer][column].setBorder(blocks[rowAdvancer - 1][column].getBorder());
          blocks[rowAdvancer - 1][column].setBorder(greyBorder);
        }
      }
    }
  }
  
  /**
   * Method causes colored columns to slide leftwards if an entire column is greyed
   */
  private static void blockSlide() {
    int column = 0;
    //outer loop searches the bottom row for grey blocks (this is called after blockFall)
    while (column < blocks[0].length - 1) {
      int columnAdditive = 0;
      if (blocks[blocks.length - 1][column].getBackground() == Color.lightGray) {
        //first inner loop determines how far columns must be slid, in case multiple columns are greyed simultaneously
        for (int i = column; blocks[blocks.length - 1][i].getBackground() == Color.lightGray &&
             i < blocks[0].length - 1; i++) {
          columnAdditive++;
        }
        int row = blocks.length - 1;
        //second inner loop "slides" each block in the closest colored column on the right leftwards
        while (row >= 0) {
          blocks[row][column].setBackground(blocks[row][column + columnAdditive].getBackground());
          blocks[row][column].setBorder(blocks[row][column + columnAdditive].getBorder());
          blocks[row][column + columnAdditive].setBackground(Color.lightGray);
          blocks[row][column + columnAdditive].setBorder(greyBorder);
          row--;
        }
        columnAdditive = 1;
      }
      column++;
    }
  }
  
  /**
   * Method greyes blocks according to a cross pattern
   * @param row  the row of the clicked button
   * @param column  the column of the clicked button
   * @param color  the background color of the clicked button
   * @return the number of blocks greyed by this method
   */
  private static int crossMode(int row, int column, Color color) {
    int horizontalBlocks = -1;
    int verticalBlocks = -1;
    //The initial -3 in blocksDeleted accounts for the four methods below all counting the clicked button
    int blocksDeleted = -3;
    int rowFinder = row;
    int columnFinder = column;
    //Loop counts the deletable buttons below the clicked button
    while (rowFinder < blocks.length && blocks[rowFinder][column].getBackground() == color) {
      verticalBlocks++;
      blocksDeleted++;
      rowFinder++;
    }
    rowFinder = row;
    //Loop counts the deletable buttons above the clicked button
    while (rowFinder >= 0 && blocks[rowFinder][column].getBackground() == color) {
      verticalBlocks++;
      blocksDeleted++;
      rowFinder--;
    }
    //Loop counts the deletable buttons to the right of the clicked button
    while (columnFinder < blocks[row].length && blocks[row][columnFinder].getBackground() == color) {
      horizontalBlocks++;
      blocksDeleted++;
      columnFinder++;
    }
    //Loop counts the deletable buttons to the left of the clicked button
    columnFinder = column;
    while (columnFinder >= 0 && blocks[row][columnFinder].getBackground() == color) {
      horizontalBlocks++;
      blocksDeleted++;
      columnFinder--;
    }
    /* ++ lines reset the finders to the negativemost proper block,
     * as they must go one farther than that to exit the above loops */
    rowFinder++;
    columnFinder++;
    //blocks are only deleted if at least two will be
    if (verticalBlocks >= 2) {
      //loop starts at the lowest deletable block and moves upwards, deleting as it goes.
      while (verticalBlocks > 0) {
        blocks[rowFinder][column].setBackground(Color.lightGray);
        blocks[rowFinder][column].setBorder(greyBorder);
        rowFinder++;
        verticalBlocks--;
      }
    }
    //blocks are only deleted if at least two will be
    if (horizontalBlocks >= 2) {
      blocks[row][column].setBackground(color);
      //loop starts at the leftmost deletable block and moves upwards, deleting as it goes.
      while (horizontalBlocks > 0) {
        blocks[row][columnFinder].setBackground(Color.lightGray);
        blocks[row][columnFinder].setBorder(greyBorder);
        columnFinder++;
        horizontalBlocks--;
      }
    }
    return blocksDeleted;
  }
  
  /**
   * Method greys blocks in a diagonal pattern
   * @param row  the row of the clicked button
   * @param column  the column of the clicked button
   * @param color  the background color of the clicked button
   * @param crossedBlocks  the number of blocks greyed by crossMode
   * @return the number of blocks greyed by this method
   */
  private static int starMode(int row, int column, Color color, int crossedBlocks) {
    //needed to reset the clicked block after crossMode greys it, allowing loops to trigger below
    blocks[row][column].setBackground(color);
    //positive and negative refer to slope, and are initially -1 to account for double counting of the clicked button
    int positiveBlocks = -1;
    int negativeBlocks = -1;
    /* initial -4 in blocksDeleted accounts for all four methods below counting the clicked button,
     * which was also counted in crossMode */
    int blocksDeleted = -4;
    int rowFinder1 = row;
    int columnFinder1 = column;
    int rowFinder2 = row;
    int columnFinder2 = column;
    //loop counts the deletable blocks below-right of the clicked button
    while (rowFinder1 < blocks.length && columnFinder1 < blocks[rowFinder1].length &&
           blocks[rowFinder1][columnFinder1].getBackground() == color) {
      negativeBlocks++;
      blocksDeleted++;
      rowFinder1++;
      columnFinder1++;
    }
    rowFinder1 = row;
    columnFinder1 = column;
    //loop counts the deletable blocks above-left of the clicked button
    while (rowFinder1 >= 0 && columnFinder1 >= 0 && blocks[rowFinder1][columnFinder1].getBackground() == color) {
      negativeBlocks++;
      blocksDeleted++;
      rowFinder1--;
      columnFinder1--;
    }
    //loop counts the deletable blocks above-right of the clicked button
    while (rowFinder2 >= 0 && columnFinder2 < blocks[row].length &&
           blocks[rowFinder2][columnFinder2].getBackground() == color) {
      positiveBlocks++;
      blocksDeleted++;
      rowFinder2--;
      columnFinder2++;
    }
    //loop counts the deletable blocks below-left of the clicked button
    rowFinder2 = row;
    columnFinder2 = column;
    while (rowFinder2 < blocks.length && columnFinder2 >= 0 &&
           blocks[rowFinder2][columnFinder2].getBackground() == color) {
      positiveBlocks++;
      blocksDeleted++;
      rowFinder2++;
      columnFinder2--;
    }
    /* ++/-- lines reset the finders to the last proper block,
     * as they must go one farther than that to exit the above loops */
    rowFinder1++;
    columnFinder1++;
    rowFinder2--;
    columnFinder2++;
    //blocks are only greyed if at least two will be
    if (negativeBlocks >= 2) {
      //loop begins at the highest-leftmost block and greyes to the below-right
      while (negativeBlocks > 0) {
        blocks[rowFinder1][columnFinder1].setBackground(Color.lightGray);
        blocks[rowFinder1][columnFinder1].setBorder(greyBorder);
        rowFinder1++;
        columnFinder1++;
        negativeBlocks--;
      }
    }
    //blocks are only greyed if at least two will be
    if (positiveBlocks >= 2) {
      blocks[row][column].setBackground(color);
      //loop begins at the lowest-leftmost block and greyes to the above-right
      while (positiveBlocks > 0) {
        blocks[rowFinder2][columnFinder2].setBackground(Color.lightGray);
        blocks[rowFinder2][columnFinder2].setBorder(greyBorder);
        rowFinder2--;
        columnFinder2++;
        positiveBlocks--;
      }
    }
    //needed in case there are no diagonal blocks to gray, to counteract the first line of this method
    if (blocksDeleted > 1 || crossedBlocks > 1)
      blocks[row][column].setBackground(Color.lightGray);
    return blocksDeleted;
  }
  
  /**
   * Method greys all blocks in a contiguous mass
   * @param row  the row of the clicked button
   * @param column  the column of the clicked button
   * @param color  the background color of the clicked button
   * @param blocksDeleted  a predeclared variable to store scoring information, necessary due to recursion
   * @return the number of blocks greyed by this method.
   */
  private static int sameMode(int row, int column, Color color, int blocksDeleted) {
    blocks[row][column].setBackground(Color.lightGray);
    blocks[row][column].setBorder(greyBorder);
    blocksDeleted++;
    //checks the button below
    if (row < blocks.length - 1 && blocks[row + 1][column].getBackground() == color)
      blocksDeleted = sameMode(row + 1, column, color, blocksDeleted);
    //checks the button above
    if (row > 0 && blocks[row - 1][column].getBackground() == color)
      blocksDeleted = sameMode(row - 1, column, color, blocksDeleted);
    //checks the button on the right
    if (column < blocks[row].length - 1 && blocks[row][column + 1].getBackground() == color)
      blocksDeleted = sameMode(row, column + 1, color, blocksDeleted);
    //checks the button on the left
    if (column > 0 && blocks[row][column - 1].getBackground() == color)
      blocksDeleted = sameMode(row, column - 1, color, blocksDeleted);
    return blocksDeleted;
  }
}