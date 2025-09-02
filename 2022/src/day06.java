import java.io.BufferedReader;
import java.io.IOException;

import util.Util;

public class day06 {
    public static void solve() throws IOException{
        BufferedReader br = Util.getInputForDay("06");

        String line = br.readLine();
        char[] input = line.toCharArray();
        char[] buffer4 = {input[0],input[0],input[0],input[0]};
        char[] buffer14 = {input[0],input[0],input[0],input[0],input[0],input[0],input[0],input[0],input[0],input[0],input[0],input[0],input[0],input[0]};
        int pos4 = 0, pos14 = 0;
        boolean found4 = false, found14 = false; 
        for (int i = 1; i < input.length; i++) {
            //check Buffer
            if(!found4 && isDifferent(buffer4)){
                pos4 = i;
                found4 = true;
            }
            if(!found14 && isDifferent(buffer14)) {
                pos14=i;
                found14 = true;
            }
            if(found4 && found14) break;
            //shift buffer 4
            buffer4[0] = buffer4[1];
            buffer4[1] = buffer4[2];
            buffer4[2] = buffer4[3];
            buffer4[3] = input[i];
            //shift buffer 14
            buffer14[0] = buffer14[1];
            buffer14[1] = buffer14[2];
            buffer14[2] = buffer14[3];
            buffer14[3] = buffer14[4];
            buffer14[4] = buffer14[5];
            buffer14[5] = buffer14[6];
            buffer14[6] = buffer14[7];
            buffer14[7] = buffer14[8];
            buffer14[8] = buffer14[9];
            buffer14[9] = buffer14[10];
            buffer14[10] = buffer14[11];
            buffer14[11] = buffer14[12];
            buffer14[12] = buffer14[13];
            buffer14[13] = input[i];
        }
        


        System.out.println("Day 6");
        System.out.println("The first  4 char start-of-packet marker is detected at position: "+pos4);
        System.out.println("The first 14 char start-of-packet marker is detected at position: "+pos14);
    }

    private static boolean isDifferent(char[] b){
        boolean isDifferent = true;
        for (int i = 0; i < b.length-1; i++) for (int j = i+1; j < b.length; j++) {
            if(b[i]==b[j]) isDifferent = false;   
        }
        return isDifferent;
    }
}
