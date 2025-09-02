

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import util.Util;

public class day02{
    public static void solve() throws IOException{
        BufferedReader br = Util.getInputForDay("02");
        String line;

        HashMap<String,Integer> mySign = new HashMap<>();
        mySign.put("B X",1);
        mySign.put("C Y",2);
        mySign.put("A Z",3);
        mySign.put("A X",4);
        mySign.put("B Y",5);
        mySign.put("C Z",6);
        mySign.put("C X",7);
        mySign.put("A Y",8);
        mySign.put("B Z",9);

        HashMap<String,Integer> myResult = new HashMap<>();
        myResult.put("B X",1);
        myResult.put("C X",2);
        myResult.put("A X",3);
        myResult.put("A Y",4);
        myResult.put("B Y",5);
        myResult.put("C Y",6);
        myResult.put("C Z",7);
        myResult.put("A Z",8);
        myResult.put("B Z",9);
        
        int totalIfSign = 0;
        int totalIfResult = 0;
        while(br.ready()){
            /* 
             * Plays:
             * 
             * Opponent
             * A - Rock
             * B - Paper
             * C - Scissors
             * 
             * Sign
             * X - Rock
             * Y - Paper
             * Z - Scissors
             * 
             * Result
             * X - Lose
             * Y - Draw
             * Z - Win
             * 
             * Points: 
             * 
             * Loss - +0
             * Draw - +3
             * Win  - +6
             * 
             * Rock     - +1
             * Paper    - +2
             * Scissors - +3
             * 
             * Outcome if second row means sign:
             * 
             * B X = +0 +1 = 1
             * C Y = +0 +2 = 2
             * A Z = +0 +3 = 3
             * A X = +3 +1 = 4
             * B Y = +3 +2 = 5
             * C Z = +3 +3 = 6
             * C X = +6 +1 = 7
             * A Y = +6 +2 = 8
             * B Z = +6 +3 = 9
             * 
             * Outcome if second row means result:
             * 
             * B X = +0 +1 = 1
             * C X = +0 +2 = 2
             * A X = +0 +3 = 3 
             * C Y = +3 +1 = 4
             * A Y = +3 +2 = 5
             * B Y = +3 +3 = 6
             * A Z = +6 +1 = 7
             * B Z = +6 +2 = 8
             * C Z = +6 +3 = 9
             * 
             * B X = +0 +1 = 1
             * C X = +0 +2 = 2
             * A X = +0 +3 = 3 
             * A Y = +3 +1 = 5
             * C Y = +3 +3 = 4
             * B Y = +3 +2 = 6
             * C Z = +6 +1 = 9
             * A Z = +6 +2 = 7
             * B Z = +6 +3 = 8
             * 
             */

            line = br.readLine();
            totalIfSign += mySign.get(line);
            totalIfResult += myResult.get(line);

        }
        System.out.println("Day 2");
        System.out.println("If the second letter is my sign, my total score would be: "+totalIfSign);
        System.out.println("If the second letter is the result, my total score would be: "+totalIfResult);
    }
}
