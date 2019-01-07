package tomn114.com.game;

/* Yoinked from the original Knight's Journey source and changed to a static utility class */


import android.graphics.Rect;

public class BoardUtilities {

    static boolean[][] visited, board;
    static Rect[][] rects;
    static int endX, endY;
    static int length;
    static int width;
    static int temp;
    static int min;
    static int solutions;

    //Coordinator will pass in a potential board, will check if the (endX,endY) can be reached
    //Returns the minimum moves
    public static int checkBoard(boolean[][] board, int startX, int startY, int endX, int endY, int length, int width){
        BoardUtilities.board = board;
        BoardUtilities.endX = endX;
        BoardUtilities.endY = endY;
        BoardUtilities.length = length;
        BoardUtilities.width = width;

        solutions = 0;
        visited = new boolean[length][width];
        min = length * width;
        findPossible(startX, startY);
        return min;
    }

    //Checks if board is playable(player can reach the end point) using floodfill recursion. Also finds the minimum number of moves.
    public static void findPossible(int x, int y){
        //if the end position is reached, then update minRequiredMoves
        if(x == endX && y == endY){
            if(temp<=min){
                min = temp;
                solutions++;
            }
            return;
        }
        // Checks if any of the eight possible "knight" moves are possible, then executes them in an attempt to reach (endX,endY)
        else{
            // The two moves in which the knight moves to the right
            if(x+2<width && y+1<length && !visited[x+2][y+1] && board[x+2][y+1]){
                visited[x+2][y+1] = true;
                temp++;
                findPossible(x+2,y+1);
                temp--;
                visited[x+2][y+1] = false;
            }
            if(x+2<width && y-1>=0 && !visited[x+2][y-1] && board[x+2][y-1]){
                visited[x+2][y-1] = true;
                temp++;
                findPossible(x+2,y-1);
                temp--;
                visited[x+2][y-1] = false;
            }

            // The two moves in which the knight moves upwards
            if(x+1<width && y+2<length && !visited[x+1][y+2] && board[x+1][y+2]){
                visited[x+1][y+2] = true;
                temp++;
                findPossible(x+1,y+2);
                temp--;
                visited[x+1][y+2] = false;
            }
            if(x-1>=0 && y+2<length && !visited[x-1][y+2] && board[x-1][y+2]){
                visited[x-1][y+2] = true;
                temp++;
                findPossible(x-1,y+2);
                temp--;
                visited[x-1][y+2] = false;
            }

            // The two moves in which the knight moves to the left
            if(x-2>=0 && y+1<length && !visited[x-2][y+1] && board[x-2][y+1]){
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
            if(x+1<width && y-2>=0 && !visited[x+1][y-2] && board[x+1][y-2]){
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

    //Create rects method
    public static void createRects(int length, int width){
        rects = new Rect[length][width];

        int x = 0;
        int y = GamePanel.boardOffset;

        for(int i = 0; i<length; i++){
            for(int j = 0; j<width; j++){
                rects[i][j] = new Rect(x, y, x + GamePanel.tileSize, y + GamePanel.tileSize);
                x += GamePanel.tileSize;
            }
            y += GamePanel.tileSize;
            x = 0;
        }
    }

    public static int[] whichTileClicked(int touchX, int touchY){
        for(int i = 0; i < length; i++){
            for(int j = 0; j < width; j++){
                if(rects[i][j].contains(touchX, touchY)) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}