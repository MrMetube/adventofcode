import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day05 implements Day {
    @Override
    public long part1(List<String> lines) {
        // Range   := long[destStart, srcStart, length]
        // Map     := Range[..]
        // Mapping := Map[..]

        List<ArrayList<long[]>> maps = IntStream.range(0, 7).mapToObj(ArrayList<long[]>::new).toList();
        {// populate maps
         // filter seed line and map titles
            String[] mapLines = lines.stream().filter(l -> !l.contains("map") && !l.startsWith("seeds"))
                    .toArray(String[]::new);
            int mapIndex = -1;
            ArrayList<long[]> currentMap = null;
            for (String line : mapLines) {
                if (line.isEmpty())
                    currentMap = maps.get(++mapIndex); // next map
                else {
                    long[] range = parseLine(line);
                    assert range.length == 3 : "Bad range. " + Arrays.toString(range);
                    assert currentMap != null : "Bad Map. Map was null.";
                    currentMap.add(range);
                }
            }
        }
        long min;
        {// map seeds over all maps
            long[] seeds = Arrays.stream(lines.getFirst().replace("seeds: ", "").trim().split(" ")).mapToLong(Long::parseLong).toArray();
            min = Arrays.stream(seeds).map(s -> {
                with_next_map: for (var map : maps)
                    for (long[] range : map)
                        if (range_inside(range, s)) {
                            s = range_map(range, s);
                            continue with_next_map;
                        }
                return s;
            }).min().orElse(-1);
        }
        return min;
    }

    @Override
    public long part2(List<String> lines) {
        // Range   := long[destStart, srcStart, length]
        // Map     := Range[..50]
        // Almanac := Map[7]

        long[][][] almanac = new long[7][50][3];
        {// populate maps
         // filter seed line and map titles
            String[] mapLines = lines.stream()
				.filter(l -> !l.endsWith("map:") && !l.startsWith("seeds"))
				.toArray(String[]::new);
            int mapIndex = -1;
            long[][] currentMap = null;
            int cursor = 0;
            for (String line : mapLines) {
                if (line.isEmpty()) {
                    if (mapIndex >= 0) almanac[mapIndex] = removeEmpties(currentMap, cursor);
                    currentMap = almanac[++mapIndex]; // next map
                    cursor = 0;
                } else {
                    long[] range = parseLine(line);
                    assert range.length == 3 : "Bad range.";
                    assert currentMap != null : "Bad Map. Map was null.";
                    almanac[mapIndex][cursor] = range;
                    cursor++;
                }
            }
            almanac[mapIndex] = removeEmpties(currentMap, cursor);
        }

		final long[] rawSeeds = Arrays.stream(lines.getFirst().replace("seeds: ", "").trim().split(" ")).mapToLong(Long::parseLong).toArray();
		final long[][] seeds = new long[rawSeeds.length/2][];
        // parse seeds into ranges
		for (int i = 0; i < seeds.length; i++) {
			int j = i * 2;
			long srcStart = rawSeeds[j];
			long length = rawSeeds[j + 1];
			seeds[i] = new long[] { srcStart, srcStart + length, 0 };
		}
        

        long start = System.nanoTime();
        long min = Arrays.stream(seeds).parallel()
                .mapToLong(seed -> LongStream.range(seed[0], seed[1] - 1)
                        .parallel()
                        .map(s -> mapSeed(s, almanac))
						.min().orElse(Long.MAX_VALUE))
                .min().orElse(-1);

        long total = System.nanoTime() - start;
        System.out.printf("%.6fs\n", total / 1e9);

        return min;
    }

	private static long mapSeed(long s, long[][][] almanac){
		with_next_map: for (long[][] map : almanac) {
			for (long[] range : map)
				if (s >= range[0] && s < range[1]) {
					s += range[2];
					continue with_next_map;
				}
		}
		return s;
	}

    private static boolean range_inside(long[] range, long s) {
        return s >= range[0] && s < range[1];
    }

    private static long range_map(long[] range, long s) {
        return s + range[2];
    }

    private static long[] parseLine(String line) {
		// return Arrays.stream(line.trim().split(" ")).mapToLong(Long::parseLong).toArray();
		var r = Arrays.stream(line.trim().split(" ")).mapToLong(Long::parseLong).toArray();
        return new long[]{r[1], r[1]+r[2],r[0]-r[1]};
    }

    private static long[][] removeEmpties(long[][] current, int length) {
        var shortened = new long[length][];
        System.arraycopy(current, 0, shortened, 0, shortened.length);
        for (long[] ms : shortened)
            assert !(ms[0] == 0 && ms[1] == 0 && ms[2] == 0) : "Bad range : " + Arrays.toString(ms);
        return shortened;
    }
}
