import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import util.Util;

public class day08 {
    public static void solve() throws IOException{
        int[][] trees;
        { //Convert input to 2D int Array of tree sizes
            BufferedReader br = Util.getInputForDay("08");
            ArrayList<String> sLines = new ArrayList<>();        
            
            while(br.ready()) sLines.add(br.readLine());
            trees = new int[sLines.size()][sLines.get(0).length()];
            for (int x = 0; x < sLines.size(); x++) {
                char[] temp = sLines.get(x).toCharArray();
                for (int y = 0; y < temp.length; y++) 
                    trees[x][y] = Integer.parseInt(""+temp[y]);
            }
        }

        int treeCount = 0;
        { //Find all visible
            boolean[][] visible = new boolean[trees.length][trees[0].length];
            int max;
            // All rows
            for (int x = 0; x < trees.length; x++) {
                // Left to Right
                max = -1;
                for (int y = 0; y < trees[0].length; y++) 
                    if(trees[x][y]>max){
                        max = trees[x][y];
                        visible[x][y] = true;
                    }
                //Right to Left
                max = -1;
                for (int y = trees[0].length-1; y >= 0; y--)
                    if(trees[x][y]>max){
                        max = trees[x][y];
                        visible[x][y] = true;
                    }
            }
            // All Columns
            for (int y = 0; y < trees.length; y++) {
                // Top to Bottom
                max = -1;
                for (int x = 0; x < trees[0].length; x++)
                    if(trees[x][y]>max){
                        max = trees[x][y];
                        visible[x][y] = true;
                    }
                //Bottom to Top
                max = -1;
                for (int x = trees[0].length-1; x >= 0; x--)
                    if(trees[x][y]>max){
                        max = trees[x][y];
                        visible[x][y] = true;
                    }
            }
            //Count up all visible
            for (int i = 0; i < visible.length; i++) for (int j = 0; j < visible[0].length; j++)
                if(visible[i][j]) treeCount++;
        }

        int topScore = 0;
        { //Find the Scenic Score
            int[][] scenicScore = new int[trees.length][trees[0].length];
            int[][][] views = new int[trees.length][trees[0].length][4]; // x, y, viewlengths - up, right, down, left
            // All Trees
            for (int row = 0; row < trees.length; row++) for (int column = 0; column < trees[0].length; column++){
                int size = trees[row][column];
                int[] viewlength = views[row][column];
                // Right
                for (int y = column+1; y < trees[0].length; y++){
                    viewlength[1]++;
                    if(trees[row][y]>=size) break;
                } 
                // Left
                for (int y = column-1; y >= 0; y--){
                    viewlength[3]++;
                    if(trees[row][y]>=size) break;
                }
                // Down
                for (int x = row+1; x < trees.length; x++){
                    viewlength[2]++;
                    if(trees[x][column]>=size) break;
                }
                // Up
                for (int x = row-1; x >= 0; x--){
                    viewlength[0]++;
                    if(trees[x][column]>=size) break;
                }

                scenicScore[row][column] = viewlength[0] * viewlength[1] * viewlength[2] * viewlength[3];
            }

            for (int x = 0; x < scenicScore.length; x++) for (int y = 0; y < scenicScore[0].length; y++)
                topScore = Math.max(topScore, scenicScore[x][y]);   
        }

        System.out.println("The number of trees visible is from the outside is: "+treeCount);
        System.out.println("The maximum scenic score is: "+topScore);
    }
}
