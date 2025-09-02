import java.io.BufferedReader;
import java.io.IOException;
import java.util.Stack;

import util.Util;

public class day05 {
    @SuppressWarnings("unchecked")
    public static void solve() throws IOException{
        BufferedReader br = Util.getInputForDay("05");        
        Stack<Character>[] stacksSingle = new Stack[9];
        for (int i=0; i<stacksSingle.length; i++)  stacksSingle[i] = new Stack<Character>();

        String line;
        while(br.ready()){
            line =  br.readLine();
            if (line.charAt(1) == '1') break;
            //Generate the stacks
            for (int i = 0; i < stacksSingle.length; i++){
                int index = i*4+1;
                char c = ' ';
                if(line.length()>= index) c = line.charAt(index);
                if(c != ' ') stacksSingle[i].add(0, c);
            }
        }
        //Skip the empty line
        br.readLine();

        Stack<Character>[] stacksMulti = new Stack[9];
        for (int i=0; i<stacksMulti.length; i++) stacksMulti[i] = (Stack<Character>) stacksSingle[i].clone();

        while(br.ready()){
            line = br.readLine();
            String[] parts = line.split(" ");
            int count = Integer.parseInt(parts[1]);
            int start = Integer.parseInt(parts[3])-1;
            int end   = Integer.parseInt(parts[5])-1;
            { //Single Moves, just pop and push each crate
                for (int i = 0; i < count; i++) {
                    char c = stacksSingle[start].pop();
                    stacksSingle[end].add(c);
                }
            }
            { // Multi moves, pop all crates into a buffer(reverses order) and push the in reversed order
                Character[] buffer = new Character[count];
                for (int i = 0; i < count; i++) buffer[i] = stacksMulti[start].pop();
                for (int i = 0; i < count; i++) stacksMulti[end].add(buffer[count-i-1]);
            }
        }

        String outSingle = "", outMulti = "";
        for (Stack<Character> stack : stacksSingle) outSingle += stack.pop();
        for (Stack<Character> stack : stacksMulti)  outMulti  += stack.pop();
        System.out.println("Day 5");
        System.out.println("After all single moves the crates on top are: "+outSingle);
        System.out.println("After all multi  moves the crates on top are: "+outMulti);
    }
}
