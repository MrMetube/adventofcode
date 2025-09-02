

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import util.Util;

public class day03{
    public static void solve() throws IOException{
        int totalItem = 0;

        //First star
        BufferedReader br = Util.getInputForDay("03");
        String line;
        char[] front, back;
        while(br.ready()){
            HashSet<Character> seenItem = new HashSet<>();
            line = br.readLine();
            //split string
            int separator = (int) Math.floor((line.length()+.5)/2);
            front = line.substring(0,separator).toCharArray();
            back = line.substring(separator).toCharArray();
            //compare string
            for (char c : front) seenItem.add(c);
            for (char c : back) if(seenItem.contains(c)){
                totalItem += c < (int)'a' ? c-'A'+27 : c-'a'+1;
                break;
            }
        }

        //Second star
        br = Util.getInputForDay("03");
        char[] first, second, third;   
        int totalBadge = 0;     
        while(br.ready()){
            HashMap<Character,Integer> seenBadge = new HashMap<>();
            first  = br.readLine().toCharArray();
            second = br.readLine().toCharArray();
            third  = br.readLine().toCharArray();
            //tally all items
            for (Character c : first)  seenBadge.put(c, 1);
            for (Character c : second) if(seenBadge.getOrDefault(c,0) == 1) seenBadge.put(c,2);
            for (Character c : third)  if(seenBadge.getOrDefault(c,0) == 2) seenBadge.put(c,3);
            //find badge
            for (Character c : seenBadge.keySet()) 
                if(seenBadge.getOrDefault(c,0) == 3){
                totalBadge += c < (int)'a' ? c-'A'+27 : c-'a'+1;
                break;
            }
        }
        System.out.println("Day 3");
        System.out.println("The sum of priorities of items is: "+totalItem);
        System.out.println("The sum of priorities of badges is: "+totalBadge);
    }
}
