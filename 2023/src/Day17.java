import java.util.*;

public class Day17 implements Day{

	static final Dir[] dirs      = {Dir.South, Dir.East, Dir.North, Dir.West};
	static final Dir[] opposites = {Dir.North, Dir.West, Dir.South, Dir.East};
	static final int[][] deltas  = {{1,0},{0,1},{-1,0},{0,-1}};
	static int[][] distance;

    @Override
    public long part1(List<String> lines) {
		int[][] city = lines.stream()
			.map(l -> l.chars().map(c -> (c - '0')).toArray())
			.toArray(int[][]::new);

		return search(city, 1, 3);
    }

	@Override
    public long part2(List<String> lines) {
        int[][] city = lines.stream()
			.map(l -> l.chars().map(c -> (c - '0')).toArray())
			.toArray(int[][]::new);

		return search(city, 4, 10);
    }

	static long search(int[][] city, int minLength, int maxLength){
		int rows = city.length, cols = city[0].length;

		distance = new int[rows][cols]; // manhattan distance
		for (int x = 0; x < rows; x++)
			for (int y = 0; y < cols; y++)
				distance[x][y] = rows-1-x + cols-1-y;

		Queue<State> todo = new PriorityQueue<>(Comparator.comparingInt(State::h));
		HashSet<State> checked = new HashSet<>();

		int[][] cost = new int[rows][cols];
		todo.add(new State(Dir.East));
		todo.add(new State(Dir.South));

		while(!todo.isEmpty()){
			State s = todo.poll();
			cost[s.x][s.y] = s.f + distance[s.x][s.y];
			if(s.x == rows-1 && s.y == cols-1 && !(s.count < minLength || s.count > maxLength)) break;

			directions: for (int i = 0; i < dirs.length; i++) {
				if(s.last == opposites[i]) continue;
				if(s.count < minLength && s.last != dirs[i]) continue;

				State last = s;
				for (int j = 1; j <= minLength; j++) {
					int x = s.x+deltas[i][0]*j, y = s.y+deltas[i][1]*j;
					if(y >= cols || y < 0 || x >= rows || x < 0) continue directions;
					
					State t = new State(x, y, last, city[x][y], dirs[i]);
					if(t.count > maxLength) continue directions;
					if(checked.add(t)) todo.add(t);
					last = t;
				}
			}
		}

        return cost[rows-1][cols-1];
	}

	static class State{
		final int x;
		final int y;
		final int f;
        final Dir last;
		final int count;

		private State(Dir dir){
			this.x = 0;
			this.y = 0;
			this.f = 0;
			count = 1;
            last = dir;
		}
		private State(int x, int y, State s, int f, Dir dir) {
			this.x = x;
			this.y = y;
			this.f = s.f+f;
            last = dir;
			count = dir == s.last ? s.count+1 : 1;
		}
		private int h(){ return f+distance[x][y]; }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + count;
			result = prime * result + ((last == null) ? 0 : last.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			return switch(obj){
                case State that -> that.x == x && that.y == y && that.last == last;
                case null, default -> false;
            };
		}
	}
	enum Dir{North, East, South, West}
}
