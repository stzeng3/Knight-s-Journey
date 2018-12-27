package tomn114.com.game;

/* Yoinked from the original Knight's Journey source and changed to a static utility class */

public class BoardUtilities {

    static boolean[][] visited, board;
    static int startX,startY;
    static int finalX,finalY;
    static int size; //Board length or width
    static int temp;
    static int solutions;
    static int minRequiredMoves;

    //Coordinator will pass in a potential board, will check if the (finalX,finalY) can be reached
    //Returns the minimum moves
    public static int checkBoard(boolean[][] board, int startX, int startY, int finalX, int finalY, int size){
        BoardUtilities.board = board;
        BoardUtilities.startX = startX;
        BoardUtilities.startY = startY;
        BoardUtilities.finalX = finalX;
        BoardUtilities.finalY = finalY;

        BoardUtilities.size = size;

        solutions = 0;
        visited = new boolean[size][size];
        minRequiredMoves = size * size;
        findPossible(startX, startY);
        return minRequiredMoves;
    }

    //Returns the number of solutions
    public static int numOfSolutions(){
        return solutions;
    }

    //Checks if board is playable(player can reach the end point) using floodfill recursion. Also finds the minimum number of moves.
    public static void findPossible(int x,int y){
        //if the end position is reached, then update minRequiredMoves
        if(x == finalX && y == finalY){
            if(temp<=minRequiredMoves){
                minRequiredMoves = temp;
                solutions++;
            }
            return;
        }
        // Checks if any of the eight possible "knight" moves are possible, then executes them in an attempt to reach (finalX,finalY)
        else{
            // The two moves in which the knight moves to the right
            if(x+2<size && y+1<size && !visited[x+2][y+1] && board[x+2][y+1]){
                visited[x+2][y+1] = true;
                temp++;
                findPossible(x+2,y+1);
                temp--;
                visited[x+2][y+1] = false;
            }
            if(x+2<size && y-1>=0 && !visited[x+2][y-1] && board[x+2][y-1]){
                visited[x+2][y-1] = true;
                temp++;
                findPossible(x+2,y-1);
                temp--;
                visited[x+2][y-1] = false;
            }

            // The two moves in which the knight moves upwards
            if(x+1<size && y+2<size && !visited[x+1][y+2] && board[x+1][y+2]){
                visited[x+1][y+2] = true;
                temp++;
                findPossible(x+1,y+2);
                temp--;
                visited[x+1][y+2] = false;
            }
            if(x-1>=0 && y+2<size && !visited[x-1][y+2] && board[x-1][y+2]){
                visited[x-1][y+2] = true;
                temp++;
                findPossible(x-1,y+2);
                temp--;
                visited[x-1][y+2] = false;
            }

            // The two moves in which the knight moves to the left
            if(x-2>=0 && y+1<size && !visited[x-2][y+1] && board[x-2][y+1]){
                visited[x-2][y+1] = true;
                temp++;
                findPossible(x-2,y+1);
                temp--;
                visited[x-2][y+1] = false;
            }
            if(x-2>=0 && y-1>=0 && !visited[x-2][y-1] && board[x-2][y-1]){
                visited[x-2][y-1] = true;
                temp++;
                findPossible(x-2,y-1);
                temp--;
                visited[x-2][y-1] = false;
            }

            // The two moves in which the knight moves downwards
            if(x+1<size && y-2>=0 && !visited[x+1][y-2] && board[x+1][y-2]){
                visited[x+1][y-2] = true;
                temp++;
                findPossible(x+1,y-2);
                temp--;
                visited[x+1][y-2] = false;
            }
            if(x-1>=0 && y-2>=0 && !visited[x-1][y-2] && board[x-1][y-2]){
                visited[x-1][y-2] = true;
                temp++;
                findPossible(x-1,y-2);
                temp--;
                visited[x-1][y-2] = false;
            }
        }
    }
}