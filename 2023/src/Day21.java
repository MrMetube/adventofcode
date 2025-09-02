import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Day21 implements Day {

	@Override
	public long part1(List<String> lines) {
		char[][] map = lines.stream().map(String::toCharArray).toArray(char[][]::new);
		int sr = 0, sc = 0;
		findS: for (sr = 0; sr < map.length; sr++)
			for (sc = 0; sc < map[0].length; sc++)
				if (map[sr][sc] == 'S')
					break findS;

		int maxSteps = 64;
		char[][] next;
		for (int i = 0; i < maxSteps; i++) {
			next = new char[map.length][map[0].length];
			for (int r = 0; r < map.length; r++) {
				for (int c = 0; c < map[0].length; c++) {
					if (map[r][c] == 'O' || i == 0 && map[r][c] == 'S') {
						for (int[] delta : new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }) {
							int dr = r + delta[0];
							int dc = c + delta[1];
							if (dr >= 0 && dr < map.length && dc >= 0 && dc < map[0].length && map[dr][dc] == '.')
								next[dr][dc] = 'O';
						}
						next[r][c] = '.';
					} else {
						next[r][c] = next[r][c] != 0 ? next[r][c] : map[r][c];
					}
				}
			}
			map = next;

		}
		int count = 0;
		for (int r = 0; r < map.length; r++)
			for (int c = 0; c < map[0].length; c++)
				if(map[r][c] == 'O') count++;

		return count;
	}

	@Override
	public long part2(List<String> lines) {
		return 0;
		// char[][] map = lines.stream().map(String::toCharArray).toArray(char[][]::new);
		// int sr, sc = 0;
		// findS: for (sr = 0; sr < map.length; sr++)
		// 	for (sc = 0; sc < map[0].length; sc++)
		// 		if (map[sr][sc] == 'S')
		// 			break findS;

		// int maxSteps = 26501365;
		// LinkedList<Long> endPoints = new LinkedList<>();
		// HashSet<Long> next = new HashSet<>();
		// endPoints.add((long)sr << 32 | sc);
		// for (int i = 0; i < maxSteps; i++) {
		// 	while(!endPoints.isEmpty()){
		// 		long current = endPoints.removeFirst();
		// 		int r = (int) (current >> 32), c = (int) current;
		// 		for (int[] delta : new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }) {
		// 			int dr = r + delta[0];
		// 			int dc = c + delta[1];
		// 			int inr = (dr % map.length + map.length) % map.length;
		// 			int inc = (dc % map[0].length + map[0].length) % map[0].length;
		// 			assert inr >= 0 && inr < map.length && inc >= 0 && inc < map[0].length : ""+inr+" : " + inc ;

		// 			if (map[inr][inc] == '.' || map[inr][inc] == 'S') {
		// 				long res = (long)dr << 32 | dc;
		// 				if(!next.contains(res))
		// 					next.add(res);
		// 			}
		// 		}
		// 	}
		// 	endPoints.addAll(next);
		// 	next.clear();
		// }

		// return endPoints.size();
	}

}
