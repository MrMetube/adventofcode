import java.util.List;

public class Day02 implements Day{

    @Override
    public long part1(List<String> lines) {
        final int[] max = {12, 13, 14};

        return lines.stream()
            .map(line -> line.replaceAll("[:,]",""))
            .map(line -> line + ";")
            .map(line -> line.split(" "))
            .mapToInt(words -> {
                int game = Integer.parseInt(words[1]);
                boolean isPossible = true;
                int[] cubes = {0,0,0};
                int amount = -1;
                for (int i = 2; i < words.length; i++) {
                    String word = words[i];
                    boolean reset = false;
                    if(word.contains(";")){
                        reset = true;
                        word = word.substring(0, word.length()-1);
                    }
                    switch (word) {
                        case "red","red;"     -> cubes[0] += amount;
                        case "green","green;" -> cubes[1] += amount;
                        case "blue","blue;"   -> cubes[2] += amount;
                        default -> amount = Integer.parseInt(word);
                    }
                    if(reset){
                        for (int j = 0; j < max.length; j++)
                            isPossible &= cubes[j] <= max[j];
                        
                        cubes = new int[]{0,0,0};
                    }
                }
                return isPossible ? game : 0;
            }).sum();
    }

    @Override
    public long part2(List<String> lines) {
        return lines.stream()
            .map(line -> line.replaceAll("[:,]",""))
            .map(line -> line + ";")
            .map(line -> line.split(" "))
            .mapToInt(words -> {
                int[] max = {0,0,0};

                int amount = -1;
                for (int i = 2; i < words.length; i++) {
                    String word = words[i];
                    switch (word) {
                        case "red","red;"     -> max[0] = Math.max(max[0], amount);
                        case "green","green;" -> max[1] = Math.max(max[1], amount);
                        case "blue","blue;"   -> max[2] = Math.max(max[2], amount);
                        default -> amount = Integer.parseInt(word);
                    }
                }
                return max[0] * max[1] * max[2];
            }).sum();
    }

}
