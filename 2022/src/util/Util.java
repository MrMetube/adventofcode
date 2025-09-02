package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Util {
    public static BufferedReader getInputForDay(String day){ // 01,06,13...
        try {
            return new BufferedReader(new FileReader(new File("./input/d"+day+".txt")));
        } catch (FileNotFoundException e) {}
        return null;
    }
}
