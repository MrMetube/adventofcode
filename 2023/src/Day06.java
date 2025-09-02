import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day06 implements Day{

    @Override
    public long part1(List<String> lines) {
        Function<String, int[]> parseLine = (line) -> Arrays.stream(line.trim().split(" ")).filter(n -> !n.isBlank()).map(String::trim).mapToInt(Integer::parseInt).toArray();
        int[] times     = parseLine.apply(lines.get(0).replace("Time: ",""));
        int[] distances = parseLine.apply(lines.get(1).replace("Distance: ",""));

        int[][] races = IntStream.range(0, times.length).mapToObj(i -> new int[]{times[i], distances[i]}).toArray(int[][]::new);

        return Arrays.stream(races)
            .mapToInt(race -> {
                int maxTime = race[0];
                int goal    = race[1];
                return (int) IntStream.range(0, maxTime)
                    .map(i -> i * (maxTime - i))
                    .filter(dist -> dist > goal)
                    .count();
            }).reduce(1, (a, b) -> a * b);
    }

    @Override
    public long part2(List<String> lines) {
        long time = Long.parseLong(lines.get(0).replace("Time: ","").replaceAll(" ", ""));
        long goal = Long.parseLong(lines.get(1).replace("Distance: ","").replaceAll(" ", ""));

        return (int) LongStream.range(0, time)
            .map(i -> i * (time - i))
            .filter(dist -> dist > goal)
            .count();
    }
}
