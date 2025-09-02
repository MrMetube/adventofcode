import java.util.ArrayList;
import java.util.List;

public class Day11 implements Day{

    @Override
    public long part1(List<String> lines) {
		return galaxyDistanceForExpansion(lines, 2);
    }

    @Override
    public long part2(List<String> lines) {
		return galaxyDistanceForExpansion(lines, 1_000_000);
    }

	static long galaxyDistanceForExpansion(List<String> universe, int expansionRate){
		final int[] rowGaps, colGaps;
		{
			// find empty rows
			final List<Integer> emptyRows = new ArrayList<>();
			for (int i = 0; i<universe.size(); i++){
				String line = universe.get(i);
				if(!line.contains("#")) emptyRows.add(i);
			}
			rowGaps = emptyRows.stream().mapToInt(i -> i).toArray();
			// find empty columns
			final List<Integer> emptyColumns = new ArrayList<>();
			for (int col = 0; col < universe.getFirst().length(); col++) {
				boolean hasGalaxy = false;
				for (String line : universe) hasGalaxy |= line.charAt(col) == '#';
				if(!hasGalaxy) emptyColumns.add(col);
			}
			colGaps = emptyColumns.stream().mapToInt(i -> i).toArray();
		}

		// list all galaxy coords 
		final List<int[]> galaxies = new ArrayList<>();
		for (int i = 0; i < universe.size(); i++) {
			var line = universe.get(i);
			for (int j = 0; j < universe.getFirst().length(); j++) if(line.charAt(j) == '#') galaxies.add(new int[]{i,j});
		}

		// calculate manhatten distance for every galaxy pair
		// sum distances
		long sum = 0;
		for (int i = 0; i < galaxies.size(); i++) {
			int[] galaxy = galaxies.get(i);
			for (int j = i+1; j < galaxies.size(); j++) {
				int[] other = galaxies.get(j);
				sum += distance(galaxy, other, rowGaps, colGaps, expansionRate);
			}
		}

        return sum;
	}

	static long distance(int[] galaxy, int[] other, int[] rowGaps, int[] colGaps, int expansionRate){
		long minRow = Math.min(galaxy[0],other[0]);
		long maxRow = Math.max(galaxy[0],other[0]);
		long minCol = Math.min(galaxy[1],other[1]);
		long maxCol = Math.max(galaxy[1],other[1]);

		long deltaRow = Math.abs(galaxy[0]-other[0]);
		long deltaCol = Math.abs(galaxy[1]-other[1]);

		long distance = deltaCol + deltaRow;

		for (int i : rowGaps) if(minRow < i && i < maxRow) distance += expansionRate-1;
		for (int i : colGaps) if(minCol < i && i < maxCol) distance += expansionRate-1;

		return distance;
	}
    
}
