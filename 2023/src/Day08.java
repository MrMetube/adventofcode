import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day08 implements Day {

	@Override
	public long part1(List<String> lines) {
		int[] input = lines.getFirst().chars().map(c -> c == 'L' ? 0 : 1).toArray();

		HashMap<String, String[]> map = new HashMap<>();

		for (int i = 2; i < lines.size(); i++) {
			String[] parts = lines.get(i)
					.replaceAll("[,=()]+", "")
					.split(" ");
			String key = parts[0];
			String left = parts[2];
			String right = parts[3];
			map.put(key, new String[] { left, right });
		}

		String[] position = map.get("AAA");
		int i = 0;
		int steps = 0;
		while (!position[input[i]].endsWith("Z")) {
			position = map.get(position[input[i]]);
			i++;
			i %= input.length;
			steps++;
		}
		return steps + 1;
	}

	@Override
	public long part2(List<String> lines) {
		int[] input = lines.getFirst().chars().map(c -> c == 'L' ? 0 : 1).toArray();

		// setup all nodes and positions
		HashMap<String, String[]> map = new HashMap<>();
		for (int i = 2; i < lines.size(); i++) {
			String[] parts = lines.get(i)
					.replaceAll("[,=()]+", "")
					.split(" ");
			String key = parts[0];
			String left = parts[2];
			String right = parts[3];
			map.put(key, new String[]{left, right});
		}
		// find all start positions
		String[][] positions = map.entrySet().stream().parallel()
				.filter(entry -> entry.getKey().charAt(2) == 'A')
				.map(Map.Entry::getValue)
				.toArray(String[][]::new);

		long[] steps = new long[positions.length];
		for (int index = 0; index < positions.length; index++) {
			String[] position = positions[index];
			int i = 0;
			int step = 0;
			while (!position[input[i]].endsWith("Z")) {
				position = map.get(position[input[i]]);
				i++;
				i %= input.length;
				step++;
			}
			steps[index] = step+1;
		}
		// find the least common multiple of all step counts
		// then all cycles will have synced so that all ghosts are at the goals
		return lcm(steps);
	}
	// Stolen from https://stackoverflow.com/a/4202114
	private static long gcd(long a, long b) {
		while (b > 0) {
			long temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

	private static long lcm(long a, long b) { return a * (b / gcd(a, b)); }

	private static long lcm(long[] input) {
		long result = input[0];
		for (int i = 1; i < input.length; i++) result = lcm(result, input[i]);
		return result;
	}
}
