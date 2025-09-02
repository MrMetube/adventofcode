import java.util.ArrayList;
import java.util.List;

public class Day13 implements Day{

    @Override
    public long part1(List<String> lines) {
		List<List<String>> valleys = new ArrayList<>();
		valleys.add(new ArrayList<>());
		for (String cs : lines) {
			if(!cs.isEmpty()) valleys.getLast().add(cs);
			else valleys.add(new ArrayList<>());
		}

		return valleys.stream().parallel()
			.map(List::stream)
			.map(valley -> valley.map(String::toCharArray))
			.map(valley -> valley.toArray(char[][]::new))
			.map(valley -> findRelections(valley, 0))
			.mapToLong(List::getFirst)
			.sum();
    }

    @Override
    public long part2(List<String> lines) {
		List<List<String>> valleys = new ArrayList<>();
		valleys.add(new ArrayList<>());
		for (String cs : lines) {
			if(!cs.isEmpty()) valleys.getLast().add(cs);
			else valleys.add(new ArrayList<>());
		}

		return valleys.stream().parallel()
			.map(List::stream)
			.map(valley -> valley.map(String::toCharArray))
			.map(valley -> valley.toArray(char[][]::new))
			.mapToLong(valley -> {
				List<Long> normal = findRelections(valley, 0);
				List<Long> results = findRelections(valley, 1);
				return results.stream().filter(r -> !normal.contains(r)).findFirst().orElse(-1L);
			})
			.sum();
    }
    
	private static List<Long> findRelections(char[][] valley, int targetDistance){
 		// 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 - index
		// # | . | # | # | . | . | # | # | . - chars
		//   0   1   2   3   4   5   6   7   - reflection
		int rows = valley.length;
		int cols = valley[0].length;
		List<Long> results = new ArrayList<>();
		// Horizontal
		for (int mirror = 0; mirror < rows-1; mirror++) { 
			int distance = 0; // distance between perfect reflections
			int before = mirror;
			int after = mirror+1;
			while(before>=0 && after < rows){
				for (int i = 0; distance <= targetDistance && i < cols; i++) distance += valley[before][i] != valley[after][i] ? 1 : 0;
				before--;
				after++;
			}
			if(distance == targetDistance) results.add((mirror+1L)*100);
		}
		
		// Vertical
		for (int mirror = 0; mirror < cols-1; mirror++) {
			int distance = 0; // distance between perfect reflections
			int before = mirror;
			int after = mirror+1;
			while(before>=0 && after < cols){
				for (int i = 0; distance <= targetDistance && i < rows; i++) distance += valley[i][before] != valley[i][after] ? 1 : 0;
				before--;
				after++;
			}
			if(distance == targetDistance) results.add(mirror+1L);
		}

        return results;
	}

}
