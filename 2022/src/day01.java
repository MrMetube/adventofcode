

import java.io.BufferedReader;

import util.Util;

public class day01{
    public static void solve() throws Exception{
        BufferedReader br = Util.getInputForDay("01");
        String line;
        int count = 0;
        int[] max = new int[3];
        while(br.ready()){
            line = br.readLine();
            if(line.length()>0) 
                count += Integer.parseInt(line);
            else{
                if(count > max[0]){
                    int temp = max[0];
                    max[0] = count;
                    count = temp;
                }
                if(count > max[1]){
                    int temp = max[1];
                    max[1] = count;
                    count = temp;
                }
                if(count > max[2]) max[2] = count;
                count = 0;
            }
        }

        System.out.println("Day 1");
        System.out.println("The largest amount carried is: "+max[0]);
        System.out.println("The top three Elves carry a total of: "+ (max[0]+max[1]+max[2]));
    }  
}
