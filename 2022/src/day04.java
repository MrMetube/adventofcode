import java.io.BufferedReader;
import java.io.IOException;

import util.Util;

public class day04 {

    public static void solve() throws IOException {
        BufferedReader br = Util.getInputForDay("04");
        int totalOverlap = 0,
            partialOverlap = 0;
        //PartOne
        while(br.ready()){
            String[] temp1 = br.readLine().split(","),
                     temp2 = temp1[0].split("-"),
                     temp3 = temp1[1].split("-");
            int a = Integer.parseInt(temp2[0]),
                b = Integer.parseInt(temp2[1]),
                x = Integer.parseInt(temp3[0]),
                y = Integer.parseInt(temp3[1]);
            if( a<=x && b>=y || x<=a && y>=b ) totalOverlap ++;
        }
        // Part Two
        br = Util.getInputForDay("04");
        while(br.ready()){
            String[] temp1 = br.readLine().split(","),
                     temp2 = temp1[0].split("-"),
                     temp3 = temp1[1].split("-");
            int a = Integer.parseInt(temp2[0]),
                b = Integer.parseInt(temp2[1]),
                x = Integer.parseInt(temp3[0]),
                y = Integer.parseInt(temp3[1]);
            if( a<=x && b>=y ||         // a-x-y-b - xy inside ab
                x<=a && y>=b ||         // x-a-b-y - ab inside xy
                a<=x && b>=x && b<=y || // a-x-b-y - x-b overlaps
                x<=a && y>=a && y<=b)   // x-a-y-b - a-y overlaps
                partialOverlap ++;
        }
        System.out.println("Day 4");
        System.out.println("The total count of assignment pairs which fully contain each other are: "+totalOverlap);
        System.out.println("The total count of assignment pairs which partially contain each other are: "+partialOverlap);
    }
    
}
