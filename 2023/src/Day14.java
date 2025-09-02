import java.util.List;

public class Day14 implements Day {

	static char[][] rows;

	@Override
	public long part1(List<String> lines) {
		rows = lines.stream()
				.map(String::toCharArray)
				.toArray(char[][]::new);

		rollingStonesNorth();
		return getLoad();
	}

	@Override
	public long part2(List<String> lines) {
		rows = lines.stream()
				.map(String::toCharArray)
				.toArray(char[][]::new);


		// String[] last = new String[rows.length];
		// for (int i = 1; i <= 1_000_000_000; i++) {
			// if (i % 1000 == 0)
			// 	System.out.printf("            \r%,d", i);
			// if (i % 1_000 == 0) {
			// 	String[] all = Arrays.stream(rows).map(String::valueOf).toArray(String[]::new);
			// 	boolean same = true;
			// 	for (int j = 0; j < last.length; j++) {
			// 		same &= all[j].equals(last[j]);
			// 	}
			// 	if (same)
			// 		break;
			// 	last = all;
			// }
		for (int i = 1; i <= 1_000; i++) { // Hack that happens to work
			rollingStonesNorth();
			rollingStonesWest();
			rollingStonesSouth();
			rollingStonesEast();
		}

		return getLoad();
	}

	private static void rollingStonesWest() {
		rollingStonesEquatorial(true);
	}

	private static void rollingStonesEast() {
		rollingStonesEquatorial(false);
	}

	private static void rollingStonesNorth() {
		rollingStonesPolar(true);
	}

	private static void rollingStonesSouth() {
		rollingStonesPolar(false);
	}

	private static void rollingStonesEquatorial(boolean west) {
		int dir = west ? 1 : -1;
		int start = west ? 0 : rows.length - 1;

		for (char[] c : rows) {
			int front = start + dir, back = start;
			while (0 <= front && front < c.length) {
				char cfront = c[front];
				char cback = c[back];
				if (cback != '.') {
					back += dir;
				} else if (cfront == '#') {
					back = front + dir;
					front += dir;
				} else if (cfront == 'O') {
					c[front] = cback;
					c[back] = cfront;
					back += dir;
				}
				front += dir;
			}
		}
	}

	private static void rollingStonesPolar(boolean north) {
		int dir = north ? 1 : -1;
		int start = north ? 0 : rows.length - 1;

		for (int c = 0; c < rows.length; c++) {
			int front = start + dir, back = start;
			while (0 <= front && front < rows.length) {
				char cfront = rows[front][c];
				char cback = rows[back][c];
				if (cback != '.') {
					back += dir;
				} else if (cfront == '#') {
					back = front + dir;
					front += dir;
				} else if (cfront == 'O') {
					rows[front][c] = cback;
					rows[back][c] = cfront;
					back += dir;
				}
				front += dir;
			}
		}
	}

	private static long getLoad() {
		long load = 0;
		for (int r = 0; r < rows.length; r++) {
			long weight = rows.length - r;
			// System.out.printf("%2d | ",weight);
			for (int c = 0; c < rows.length; c++) {
				load += rows[r][c] == 'O' ? weight : 0;
				// System.out.printf("%2s ", rows[r][c] == 'O' ? "0" : (rows[r][c] == '#' ? "#" : "."));
			}
			// System.out.println();
		}
		return load;
	}

}
