import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import util.Util;

public class day11 {
    static Monkey[] monkeys = new Monkey[4];
    static boolean star1 = false;

    public static void solve() throws IOException{
        {// Setup the Monkeys
            BufferedReader br = Util.getInputForDay("11");
            int activeMonkey = 0;
            String line;
            String[] parts;
            // Read the input
            while(br.ready()){
                // Start of Monkey
                br.readLine();
                // Starting Items
                parts = br.readLine().trim().replace(",", "").split(" ");
                long[] startingItems = new long[parts.length-2];
                for (int i = 0; i < startingItems.length; i++)
                    startingItems[i] = Long.parseLong(parts[i+2]);
                // Operation
                line = br.readLine();
                char op = (line.contains("*")) ? '*' : '+';
                // Factor
                parts = line.split(" ");
                int factor = (parts[parts.length-1].equals("old")) ? 0 : Integer.parseInt(parts[parts.length-1]);
                // Test
                parts = br.readLine().split(" ");
                long test = Integer.parseInt(parts[parts.length-1]);
                // Targets
                parts = br.readLine().split(" ");
                int truely = Integer.parseInt(parts[parts.length-1]);
                parts = br.readLine().split(" ");
                int falsely = Integer.parseInt(parts[parts.length-1]);
                // Monkey done
                monkeys[activeMonkey] = new Monkey(activeMonkey,op, factor, test, truely, falsely, startingItems);
                activeMonkey++;
                br.readLine();
            }
        }
        if(star1){
            // Start 1
            // Do the 20 Rounds
            for (int round = 0; round < 20; round++) 
                for (int turn = 0; turn < monkeys.length; turn++) 
                    monkeys[turn].doTurn();
            long monkeyBusiness20;
            {
                long[] top20 = new long[2];
                for (Monkey monkey : monkeys) {
                    long n = monkey.reportBusiness();
                    if(n>top20[0]){
                        top20[1] = top20[0];
                        top20[0] = n;
                    }
                }
                monkeyBusiness20 = top20[0] * top20[1];
            }
            System.out.println("The level of monkey business after 20 rounds is: "+monkeyBusiness20);
        }else{
            // Star 2
            for (int round = 0; round < 10000; round++) 
                for (int turn = 0; turn < monkeys.length; turn++) 
                    monkeys[turn].doTurn();
            long monkeyBusiness10k;
            {// Determine Monkey Business
                long[] top10k = new long[2];
                for (Monkey monkey : monkeys) {
                    long n = monkey.reportBusiness();
                    if(n>top10k[0]){
                        top10k[1] = top10k[0];
                        top10k[0] = n;
                    }
                }
                monkeyBusiness10k = top10k[0] * top10k[1];
            }
            System.out.printf(Locale.GERMANY,"The level of monkey business after 10.000 rounds is: %,d",monkeyBusiness10k);
        }
    }

    static class Monkey{
        int number;
        long monkeyBusiness = 0;

        ArrayList<Long> items;
        long test;
        char operator;
        int factor; // 0 := old
        int ifTrue;
        int ifFalse;

        Monkey(int number, char operator, int factor, long test, int ifTrue, int ifFalse, long[] startingItems){
            this.number = number;
            items = new ArrayList<>();
            for (int i = 0; i < startingItems.length; i++)
                items.add(startingItems[i]);
            this.factor = factor;
            this.test = test;
            this.operator = operator;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
        }

        void doTurn(){
            int size = items.size();
            for (int i = 0; i < size; i++) {
                // operation
                monkeyBusiness++;
                long old = items.remove(0);
                old = operation(old);
                // test and throw
                if(old % test == 0)
                    monkeys[ifTrue].catchItem(old);
                else
                    monkeys[ifFalse].catchItem(old);
            }
        }

        long operation(long old){
            long res = old;
            long factor = (this.factor == 0) ? old : this.factor;
            if(operator=='+'){
                res = old + factor;
            }
            else if(operator=='*'){
                res = old * factor;
            }
            // Star 1: Divide by 3
            if(star1)
                res /= 3;
            // Start 2: Dont
            return res;
        }

        void catchItem(long item){
            items.add(item);
        }

        long reportBusiness(){
            return monkeyBusiness;
        }

        @Override
        public String toString() {
            return "Monkey " + number + ": " + items.toString();
        }
    }
}
