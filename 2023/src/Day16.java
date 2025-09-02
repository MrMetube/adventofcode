import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Day16 implements Day{

    @Override
    public long part1(List<String> lines) {
		char[][] contraption = lines.stream()
			.map(String::toCharArray)
			.toArray(char[][]::new);

		List<Beam> beams = new ArrayList<>();
		beams.add(new Beam(Dir.Right, 0, -1));

		return energizeTiles(contraption, beams);
    }

    @Override
    public long part2(List<String> lines) {
        char[][] contraption = lines.stream()
			.map(String::toCharArray)
			.toArray(char[][]::new);

		long max = 0;
		List<Beam> beams = new ArrayList<>();

		for(int row=0; row<contraption.length; row++){
			beams.add(new Beam(Dir.Right, row, -1));
			max = Math.max(max, energizeTiles(contraption, beams));
		}
		for(int row=0; row<contraption.length; row++){
			beams.add(new Beam(Dir.Left, row, contraption[0].length));
			max = Math.max(max, energizeTiles(contraption, beams));
		}

		for(int col=0; col<contraption[0].length; col++){
			beams.add(new Beam(Dir.Down, -1, col));
			max = Math.max(max, energizeTiles(contraption, beams));
		}
		for(int col=0; col<contraption[0].length; col++){
			beams.add(new Beam(Dir.Up, contraption.length, col));
			max = Math.max(max, energizeTiles(contraption, beams));
		}

		return max;
    }

	static long energizeTiles(char[][] contraption, List<Beam> beams){
		List<Beam> toAdd = new ArrayList<>();
		List<Beam> toRemove = new ArrayList<>();
		Dir[][] energized = new Dir[contraption.length][contraption[0].length];

		while (!beams.isEmpty()) {
			for (Beam beam : beams) {
				switch(beam.dir){ // step the beam
					case Up 	->  beam.x -= 1;
					case Right 	->  beam.y += 1;
					case Down 	->  beam.x += 1;
					case Left 	->  beam.y -= 1;
				}
				
				boolean outOfBounds = beam.x < 0 || beam.x >= contraption.length || beam.y < 0 || beam.y >= contraption[0].length;
				if( outOfBounds ) { // remove outside beams
					toRemove.add(beam);
					continue;
				}
				// interact with contraption
				beam.dir = switch(contraption[beam.x][beam.y]){
					case '\\' -> switch(beam.dir){
						case Right 	-> Dir.Down;
						case Down 	-> Dir.Right;
						case Left 	-> Dir.Up;
						case Up 	-> Dir.Left;
					};
					case '/' -> switch(beam.dir){
						case Right 	-> Dir.Up;
						case Up 	-> Dir.Right;
						case Left 	-> Dir.Down;
						case Down 	-> Dir.Left;
					};
					case '-' -> { // split the beam
						if(beam.dir != Dir.Down && beam.dir != Dir.Up) yield beam.dir;
						
						toAdd.add(new Beam(Dir.Right, beam.x, beam.y));
						yield Dir.Left;
					}
					case '|' -> { // split the beam
						if(beam.dir != Dir.Right && beam.dir != Dir.Left) yield beam.dir;

						toAdd.add(new Beam(Dir.Up, beam.x, beam.y));
						yield Dir.Down;
					}
                    default  -> beam.dir;
				};

				// was a beam already on this tile?
				if(energized[beam.x][beam.y] == beam.dir) toRemove.add(beam);
				else energized[beam.x][beam.y] = beam.dir;
			}

			beams.removeAll(toRemove);
			beams.addAll(toAdd);
			toRemove.clear();
			toAdd.clear();
		}

		return Arrays.stream(energized).mapToLong(row -> Arrays.stream(row).filter(Objects::nonNull).count()).sum();
	}

	enum Dir{Up, Right, Down, Left}
	static class Beam{
		Dir dir;
		int x;
		int y;
		public Beam(Dir dir, int x, int y) {
			this.dir = dir;
			this.x = x;
			this.y = y;
		}
		
	}

}
