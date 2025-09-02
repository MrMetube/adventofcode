import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Day10 implements Day{

    @Override
    public long part1(List<String> lines) {
		Pipe[][] tiles = lines.stream()
			.map(l -> l.chars()
				.mapToObj(i -> (char) i)
				.map(Pipe::of)
				.toArray(Pipe[]::new))
			.toArray(Pipe[][]::new);

		final int[][] steps = new int[tiles.length][tiles[0].length];
		for (int[] pipes : steps) Arrays.fill(pipes, Integer.MAX_VALUE);

		int startRow = -1, startCol = -1;
		startSearch: for (int r = 0; r < tiles.length; r++) for (int c = 0; c < tiles[0].length; c++) {
			if(tiles[r][c]==Pipe.Start){
				startRow = r;
				startCol = c;
				steps[r][c] = 0;
				break startSearch;
			}
		}

		List<int[]> next = new ArrayList<>();
		next.add(new int[]{startRow, startCol});
		while(!next.isEmpty()) markNeighbourghs(tiles, steps, next);
		// Print the loop
		long max = Long.MIN_VALUE;

        for (int[] step : steps)
            for (int c = 0; c < steps[0].length; c++) {
                int s = step[c];
                max = s == Integer.MAX_VALUE ? max : Math.max(max, step[c]);
            }

        return max;
    }

	static final Dir[] nextDirs = new Dir[]{Dir.North, Dir.East, Dir.South, Dir.West };
	static void markNeighbourghs(Pipe[][] tiles, int[][] steps, List<int[]> next){
		final int[] nextPipe = next.removeFirst();
		final int r = nextPipe[0], c = nextPipe[1]; 
		final int count = steps[r][c];
		final Pipe p = tiles[r][c];
		final int[][] nextOptions = new int[][]{{r-1,c},{r,c+1},{r+1,c},{r,c-1}};
		for (int i = 0; i<nextOptions.length; i++) {
			int[] o = nextOptions[i];
			int or = o[0], oc = o[1];
			if(inBounds(or, oc, tiles) && p.flow(tiles[or][oc], nextDirs[i]))
				mark(or, oc, steps, count).ifPresent(next::add);
		}
	}

	static boolean inBounds(int r, int c, Pipe[][] tiles){ return r >= 0 && r < tiles.length && c >= 0 && c < tiles[0].length;}

	static Optional<int[]> mark(int r, int c, int[][] steps, int count){
		final int previous = steps[r][c];
		steps[r][c] = Math.min(previous, count+1);
		return previous >= count+1 ? Optional.of(new int[]{r,c}) : Optional.empty();
	}

    @Override
    public long part2(List<String> lines) {
        Pipe[][] tiles = lines.stream()
			.map(l -> l.chars()
				.mapToObj(i -> (char) i)
				.map(Pipe::of)
				.toArray(Pipe[]::new))
			.toArray(Pipe[][]::new);

		final int[][] steps = new int[tiles.length][tiles[0].length];
		for (int[] pipes : steps) Arrays.fill(pipes, Integer.MAX_VALUE);

		int startRow = -1, startCol = -1;
		startSearch: for (int r = 0; r < tiles.length; r++) for (int c = 0; c < tiles[0].length; c++) {
			if(tiles[r][c]==Pipe.Start){
				startRow = r;
				startCol = c;
				steps[r][c] = 0;
				break startSearch;
			}
		}

		List<int[]> next = new ArrayList<>();
		next.add(new int[]{startRow, startCol});
		while(!next.isEmpty()) markNeighbourghs(tiles, steps, next);

		for (int r = 0; r < tiles.length; r++) for (int c = 0; c < tiles[0].length; c++)
			tiles[r][c] = steps[r][c] != Integer.MAX_VALUE ? tiles[r][c] : Pipe.Ground;

		// replace the Start to remove that edgecase
		for (int r = 0; r < tiles.length; r++) for (int c = 0; c < tiles[0].length; c++) {
			if(tiles[r][c]==Pipe.Start){
				boolean south = inBounds(r+1, c, tiles) && steps[r+1][c] == 1;
				boolean north = inBounds(r-1,c,tiles) && steps[r-1][c] == 1;
				boolean west  = inBounds(r,c-1, tiles) && steps[r][c-1] ==  1;
				boolean east  = inBounds(r,c+1,tiles) && steps[r][c+1] == 1;

				if(south){
					if(east) 		tiles[r][c] = Pipe.SEBend;
					else if(west) 	tiles[r][c] = Pipe.SWBend;
					else 			tiles[r][c] = Pipe.Vertical;
				}else if(north){
					if(east)		tiles[r][c] = Pipe.NEBend;
					else if(west) 	tiles[r][c] = Pipe.NWBend;
					else 			tiles[r][c] = Pipe.Vertical;
				}else 				tiles[r][c] = Pipe.Horizontal;
			}
		}

		long insideCount = 0;
		for (int r = 0; r < tiles.length; r++) {
			int edges = 0;
			for (int c = 0; c < tiles[0].length; c++) {
				Pipe p = tiles[r][c];
				switch(p){
					case Vertical, SWBend, SEBend -> edges++;
					case Ground -> {
						if(edges%2 == 1){
							insideCount++;
							tiles[r][c] = Pipe.InLoop;
						}else tiles[r][c] = Pipe.OutLoop;
					}
					default -> {}
				}
			}
		}

		// for (Pipe[] pipes : loop) {
		// 	for (Pipe pipe : pipes) System.out.print(pipe);
		// 	System.out.println();
		// }

        return insideCount;
    }
    
	enum Dir{North, East, South, West}

	enum Pipe{
		Ground(' '),
		Vertical('║'),
		Horizontal('═'),
		NEBend('╚'),
		NWBend('╝'),
		SWBend('╗'),
		SEBend('╔'),
		InLoop('I'),
		OutLoop('O'),
		Start('#');

		final char sign;
		Pipe(char sign){this.sign = sign;}

		private boolean flowInto(Dir in){
			return switch(in){
				case East  -> switch(this) { case Horizontal, NWBend, SWBend -> true; default -> false;};
				case North -> switch(this) { case Vertical,   SWBend, SEBend -> true; default -> false;};
				case South -> switch(this) { case Vertical,   NEBend, NWBend -> true; default -> false;};
				case West  -> switch(this) { case Horizontal, NEBend, SEBend -> true; default -> false;};
			};
		}

		private boolean flowOut(Dir out){
			if(this == Start) return true;
			if(this == Ground) return false;
			return switch(out){
				case East  -> this.flowInto(Dir.West);
				case North -> this.flowInto(Dir.South);
				case South -> this.flowInto(Dir.North);
				case West  -> this.flowInto(Dir.East);
			};
		}

		boolean flow(Pipe next, Dir dir){ return flowOut(dir) && next.flowInto(dir); }

		public String toString(){return String.valueOf(sign);}

		static Pipe of(char c){
			return switch(c){
				case '.' -> Ground;
				case '|' -> Vertical;
				case '-' -> Horizontal;
				case 'L' -> NEBend;
				case 'J' -> NWBend;
				case '7' -> SWBend;
				case 'F' -> SEBend;
				case 'S' -> Start;
				default -> throw new RuntimeException("Bad char: "+c);
			};
		}
	}

}
