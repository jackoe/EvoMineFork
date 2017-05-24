/*
 * Represents a square on a board.
 * A value of -1 represents a mine.
 * Other values represent what the square would show if clicked.
 * Shown is true if square has been clicked.
 */
public class Square  {
    public boolean shown;
    public boolean flagged;
    public int value;

    /*
     * constructor, pass -1 for mine, otherwise pass value
     */
    Square(int sqNumber)  {
        value = sqNumber;
        shown = false;
        flagged = false;
    }

    /*
     * Alternate toString method
     * @param ignoreShown reveal even if hasn't been clicked
     */
    public String toString(boolean ignoreShown)  {
        boolean show = shown || ignoreShown;
        
        if(flagged)
            return "F";
        else if(shown && value == -1)
            return "#";
        else if(show && value == -1)
            return "*";
        else if(show)
            return value + "";
        else 
            return " ";
    }
    /*
     * prints * if mine
     * prints blank space if not revealed
     * prints number if revealed
     */
    public String toString()  {
        return toString(false);
    }
}
