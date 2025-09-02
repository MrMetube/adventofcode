import java.io.BufferedReader;
import java.io.IOException;

import util.Util;

public class day09{

    public static void solve() throws IOException{
        int size = 514;
        // int size = 30;
        boolean[][] visited2 = new boolean[size][size];
        boolean[][] visited9 = new boolean[size][size];
        // Position {x,y}
        int[] head = {size/2,size/2};
        int[] tail = {size/2,size/2};
        int[][] ninetail = { 
            {size/2,size/2}, // Head
            {size/2,size/2},{size/2,size/2},{size/2,size/2}, // Tails
            {size/2,size/2},{size/2,size/2},{size/2,size/2},
            {size/2,size/2},{size/2,size/2},{size/2,size/2}
        };
        { // Make the moves
            BufferedReader br = Util.getInputForDay("09");
            while(br.ready()){
                String line = br.readLine();
                char dir = line.charAt(0);
                int amount = Integer.parseInt(line.substring(2));
                for (int i = 0; i < amount; i++) {
                    // Star 1             
                    head = moveHead(head, dir);
                    tail = maybeMove(head, tail);
                    visited2[tail[0]][tail[1]] = true;

                    // Star 2
                    ninetail[0] = moveHead(ninetail[0], dir);
                    // printNinetail(ninetail, size);
                    for(int j = 1; j<ninetail.length; j++){
                        ninetail[j] = maybeMove(ninetail[j-1], ninetail[j]);
                        // printNinetail(ninetail, size); 

                    }
                    visited9[ninetail[9][0]][ninetail[9][1]] = true;
                }

            }
        }

        int count2 = 0, count9 = 0;
        // Count the visited fields
        for (boolean[] row : visited2) for (boolean field : row)
            if (field) count2++;
        for (boolean[] row : visited9) for (boolean field : row)
            if (field) count9++;
        
        System.out.println("The amount of fields visited by the tail of the short rope is: "+count2);
        System.out.println("The amount of fields visited by the tail of the long rope is: "+count9);
    }

    private static int[] moveHead(int[] head, char dir){
        int[] result = head;
             if(dir=='U') result[0]++;
        else if(dir=='D') result[0]--;
        else if(dir=='R') result[1]++;
        else if(dir=='L') result[1]--;
        return result;
    }

    private static int[] maybeMove(int[] head, int[] tail){
        /* 
         * T moves to .
         * each side to the closest
         * 
         * |   | T | T | T |   | 
         * +---+---+-v-+---+---+-
         * | T |   | . |   | T | 
         * +---+---+---+---+---+-
         * | T > . | H | . < T | 
         * +---+---+---+---+---+-
         * | T |   | . |   | T | 
         * +---+---+-^-+---+---+-
         * |   | T | T | T |   | 
         * 
         */
        int[] result = tail;
        if( Math.abs(head[0]-tail[0]) >  1) {
            result[0] = head[0]-((head[0]-tail[0])/2);
            result[1] = head[1];
        }
        if( Math.abs(head[1]-tail[1]) >  1) {
            result[0] = head[0];
            result[1] = head[1]-((head[1]-tail[1])/2);
        }
        return result;
    }

    @SuppressWarnings("unused")
    private static void printNinetail(int[][] ninetail, int size){
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                String mark = ".";
                for (int i = 9; i >= 0; i--) {
                    if(x==ninetail[i][0] && y==ninetail[i][1]) mark = i+"";
                }
                sb.append(mark);
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}