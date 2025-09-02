import java.io.BufferedReader;
import java.io.IOException;

import util.Util;

public class day10 {
    public static void solve() throws IOException{
        BufferedReader br = Util.getInputForDay("10");
        StringBuilder sb = new StringBuilder();

        int cycle = 0;
        int register = 1;
        int memory = 0;
        boolean idle = true;

        String[] parts = new String[0];

        int sum = 0;
        while(br.ready() || !idle){
            cycle++;
            //Evaluate register - Star 1
            if((cycle-20)%40==0)
                sum += cycle * register;

            if(idle) {
                // Read next command
                parts = br.readLine().split(" ");
                if(parts[0].equals("addx")){
                    idle = false;
                    memory = Integer.parseInt(parts[1]);
                }
                //else if(parts[0].equals("noop")); // No Op
            }else{
                // Complete the command
                register += memory;
                idle = true;
            }

            // Draw the screen - Star 2
            sb.append( (Math.abs((cycle%40)-register)<=1) ? "#" : "." );
            if(cycle%40==0) sb.append("\n");
        }
        // Note: The linebreak is slightly of but the answer can be seen clearly enough.
        // Shifting the linebreak around fixes one side but makes the other less clear.
        System.out.println("The sum of the 20th and every 40 after that is: "+sum);
        System.out.println("The screen shows: \n"+sb.toString());
    }
}
