import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;

public class MineSweeper  {

    // the board
    private Square[][] board;
    public int numMinesLeft;

    /*
     * Retuns the square object if it's accessible:
     * null otherwise
     */
    public Square get(int x, int y)  {
        return inBounds(x,y) ? board[x][y]: null;
    }

    /*
     * Returns true if x and y are in bounds.
     * false otherwise
     */
    public boolean inBounds(int x, int y)  {
        return x < board.length
            && y < board.length
            && x >= 0
            && y >= 0;
    }
    /*
     * Increments a square at an index if it's in bounds
     */
    private void safeIncBoard(int x, int y)  {
        if(inBounds(x, y) && board[x][y].value >= 0)  {
            board[x][y].value++;
        }
    }
    
    /*
     * Reveales the blob of zeros
     * Also revealed the nonzeros around said blob.
     */
    private void revealZeros(int x, int y) {
        // catch the out of bounds
        // // if it isn't already shown or flagged
        if(!inBounds(x, y)
        && !board[x][y].shown
        && !board[x][y].flagged)  {
            return;
        }
        
        board[x][y].shown = true;
        // we reveal but don't recurse on nonzeros
        if(board[x][y].value != 0)  {
            return;
        }
        
        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
                revealZeros(x + i, y + j);
            }
        }
    }

    /* 
     * Click on a square
     */
    public int peek(int x, int y)  {
        if(!inBounds(x, y))  {
            return 0;
        }
        // We don't allow clicking on flagged squares
        if(board[x][y].flagged)  {
            return -2;
        }

        Square peekingAt = board[x][y];
        if(!peekingAt.shown)  {
            revealZeros(x, y);
        }
        return peekingAt.value;
     }
    /*
     * flags a mine
     * @param x the x coord
     * @param y the y coord
     */
    public void flag(int x, int y)  {
        if(!inBounds(x, y))  {
            return;
        }
        // We don't allow flagging revealed squares
        if(board[x][y].shown)  {
            return;
        }
        Square curr = board[x][y];
        //curr.flagged = !curr.flagged;
        curr.flagged = true;
        numMinesLeft += curr.flagged? 1: -1;
    }

    /*
     * Make a minesweeper board
     * @param sideLen The length of a given side.
     * @param numMines The number of mines.
     */
    MineSweeper(int sideLen, int numMines)  {
        board = new Square[sideLen][sideLen];
        Random mineLocGen = new Random();
        TreeSet<Pair> coords = new TreeSet<Pair>();
        numMinesLeft = numMines;
        
        while(coords.size() < numMines)  {
            coords.add(new Pair(mineLocGen.nextInt(sideLen),
                                mineLocGen.nextInt(sideLen)));
        }
        
        // fill every board with a blank square
        for(int i = 0; i < sideLen; i++)  {
            for(int j = 0; j < sideLen; j++)  {
                board[i][j] = new Square(0);
            }
        }

        // fills each bomb square with a -1
        // increments all squares around them
        for(Pair p: coords)  {
            int x = p.fst;
            int y = p.snd;
            board[x][y].value = -1;
            for(int i = -1; i < 2; i++)  {
                for(int j = -1; j < 2; j++)  {
                    safeIncBoard(x + i, y + j);
                }
            }
        }
    }

    /* 
     * prints the board
     */
    public void printBoard(boolean show)  {
        for(int i = 0; i < board.length; i++)  {
            for(int j = 0; j < board.length; j++)  {
                Square curr = board[i][j];
                System.out.print(curr.toString(show));
            }
            System.out.println("");
        }
    }
    /*
     * Calculates fitness by #true flags - #false flags
     */
    public int fitnessCalc(){
        int fitness = 0;
        for(int i = 0; i < board.length; i++)  {
            for(int j = 0; j < board.length; j++)  {
                Square curr = board[i][j];
                if (board[i][j].flagged) {
                    if (board[i][j].value == -1) {
                        fitness++;
                    }
                    else {
                        fitness--;
                    }    
                }
            }
        }
        return fitness;
    }
   
    /*
    * Plays the game.
    */
    public static void play()  {
        MineSweeper game = new MineSweeper(8, 10);
        int lastClick = 0;
        Scanner in = new Scanner(System.in);
        int x,y;
        
        while(lastClick != -1)  {
            
            // get input from user
            System.out.println(game.numMinesLeft + " Mines Remaining");
            game.printBoard(false);
            System.out.println("Click:1 Flag:2");
            int type = in.nextInt();
            System.out.print("First Coordinate: ");
            x = in.nextInt();
            System.out.print("Second Coordinate: ");
            y = in.nextInt();
            if(!game.inBounds(x, y))  {
                System.out.println("That's out of bounds");
                continue;
            }
            if(type == 1)  {
                System.err.println("Clicking at (" + x + ", " + y + ")");
                lastClick = game.peek(x, y);
            }  else  {
                System.err.println("Flagging at (" + x + ", " + y + ")");
                game.flag(x, y);
            }
        }
        System.out.println("you LOST");
        game.printBoard(true);
        System.out.println("Fitness: " + game.fitnessCalc());
    }
    /*

    public static void play2() {
    MineSweeper game = new MineSweeper(8, 10);
    int lastClick = 0;
    //Pair coords = new Pair(0,0);
   
        
    while(lastClick != -1)  {
        boolean frontier[]= new boolean[64] ;
      
        
        int a = 0;
              
        int x = 0;
        int y= 0;

        int d=0;
        int e=0;

        System.out.println(game.numMinesLeft + " Mines Remaining");
        game.printBoard(false);
       
        game.peek(x,y);
        lastClick = game.board[x][y].value;
            for (int q=0; q<8; q++){
                for (int r=0; r<8; r++){
        if(game.board[q][r].shown)  {
                boolean breakbool = true;
                for(int i = -1; i < 2 && breakbool; i++)  {
                    for(int j = -1; j < 2 && breakbool; j++)  {
                        if(game.inBounds(x+i, y+j) && !game.board[x+i][y+j].shown)  {
                            frontier[a] = true;
                            a++;
                            breakbool = false;
                        }
                    }
                    
                }

                
                //coords =  new Pair(x, y);
                if(a!=0)  {
                    System.out.println(frontier[a-1] + " Last Frontier ");
                }

            for(int c=0; c<64;c++)
                if(frontier[c]){
                    d = c%8;
                    e = (c-d)/8;



                    //Strategy Methods
                    game.check1(d,e);
                }
            }

            }
            


            }
            System.out.println("you LOST");
            game.printBoard(true);
            System.out.println("Fitness: " + game.fitnessCalc());
        }
}


    public boolean check1(int d, int e) {
        int b = 0;
        
        for (Square sq : getNeighBors(d,e)) {
            if(sq.shown) {
                b++;

            }
        }
        
                            }
        }

        if (board[d][e].value == b)  {
            for (Square sq : getNeighBors(d,e)) {
            if(sq.shown) {
                    flag(sq[i],sq[j]);

                }
            }
        }  
    
    */
    public List<Square>getNeighBors(int x, int y)  {
        List<Square> neighbors = new LinkedList<Square>();
        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
                if(inBounds(i,j))  {
                    neighbors.add(board[i][j]);
                }
            }
        }
        return neighbors;
    }

    

    public static void main(String[] args)  {
        play();
    }


    /*
     * Represents a pair of points
     */
    private class Pair implements Comparable<Pair>  {
        public int fst;
        public int snd;
        
        /*
         * make a pair
         */
        Pair(int fst, int snd)  {
            this.fst = fst;
            this.snd = snd;
        }

        /*
         * Here so we can put pairs in a set
         */
        public int compareTo(Pair othr)  {
            if(this.fst > othr.fst)  {
                return 1;
            }
            if(this.fst > othr.fst)  {
                return -1;
            }
            if(this.snd > othr.snd)  {
                return 1;
            }
            
            if(this.snd < othr.snd)  {
                return -1;
            }
            return 0;

        }

        public String toString()  {
            return "(" + fst + ", " + snd + ")";
        }
    }
}

