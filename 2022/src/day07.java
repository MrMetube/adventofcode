import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import util.Util;

public class day07 {
    public static ArrayList<Dir> DIRS = new ArrayList<>();
    public static Dir HEAD = new Dir(null,"/");

    public static void solve() throws IOException{
        BufferedReader br = Util.getInputForDay("07");
        Dir currentDir = HEAD;
        boolean mayReadNewLine = true;
        String line = "";
        //Create the Directories
        while(br.ready()){
            //parse line
            if(mayReadNewLine) line = br.readLine();
            mayReadNewLine = true;
            String[] parts = line.split(" ");
            if(parts[1].equals("cd")){
                currentDir = currentDir.cd(parts[2]);
            }else if(parts[1].equals("ls")){
                // Collect the Output
                ArrayList<String> output = new ArrayList<>();
                while(true){
                    line = br.readLine();
                    if(line.charAt(0) == '$') {
                        mayReadNewLine = false;
                        break;
                    }
                    output.add(line);
                    if(!br.ready()) break;
                }
                currentDir.ls(output);
            }
        }
        // Star 1
        int sum100000 = 0;
        for (Dir dir : DIRS) if(dir.size() < 100_000) sum100000 += dir.size();
        // Star 2
        int toBeFreed = 30_000_000 - (70_000_000 - HEAD.size());
        Dir willBeFreed = null;
        for (Dir dir : DIRS) {
            int size = dir.size();
            if(size>= toBeFreed && (willBeFreed == null || size < willBeFreed.size())) willBeFreed = dir;
        }

        System.out.printf(Locale.GERMAN, "The sum of all directories under the size of 100.000 is: %,d%n", sum100000);
        System.out.printf(Locale.GERMAN, "The size of the directory that must be freed is: %,d%n", willBeFreed.size());
    }

    static class Node{
        Dir parent;
        String name;

        public Node(Dir parent,String name){
            this.parent = parent;
            this.name = name;
        }

        public int size() { return 0; }
        public String name() { return name; }
    }

    static class Dir extends Node{
        ArrayList<Node> children = new ArrayList<>();
        
        public Dir(Dir parent,String name){
            super(parent,name);
            DIRS.add(this);
        }
        public void ls(ArrayList<String> input) { 
            for (String line : input) addChild(line);
        }
        public Dir cd(String path){
            if(path.equals("..")) return parent;
            if(path.equals("/")) return HEAD;
            // Navigate the child
            Node child = null;
            for (Node node : children) if(node.name().equals(path)) child = node;
            if(child!=null) return (Dir) child;
            // create nonexistent child
            return (Dir) addChild("dir "+path);
        }
        public int size(){
            int sum = 0;
            for (Node node : children) sum += node.size();
            return sum;
        }

        private Node addChild(String input){
            String[] parts = input.split(" ");
            String name = parts[1];
            Node child = (parts[0].equals("dir")) ? new Dir(this, name) : new File(this, name, Integer.parseInt(parts[0])) ;
            children.add(child);
            return child;
        }
    }

    static class File extends Node{
        int size;
        public File(Dir parent, String name, int size){
            super(parent,name);
            this.size = size;
        }
        public int size() { return size; }
    }

}
