import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class Day23 implements Day{

	static final Dir[] dirs      = {Dir.South, Dir.East, Dir.North, Dir.West};
	static final Dir[] opposites = {Dir.North, Dir.West, Dir.South, Dir.East};
	static final int[][] deltas  = {{1,0},{0,1},{-1,0},{0,-1}};
	static int[][] distance;

    @Override
    public long part1(List<String> lines) {
		char[][] map = lines.stream().map(String::toCharArray).toArray(char[][]::new);

		int rows = map.length, cols = map[0].length;

		distance = new int[rows][cols]; // manhattan distance
		for (int x = 0; x < rows; x++)
			for (int y = 0; y < cols; y++)
				distance[x][y] = rows-1-x + cols-1-y;

		PriorityQueue<State> todo = new PriorityQueue<>(Comparator.comparingInt(State::h).reversed());
		HashSet<State> checked = new HashSet<>();

		int[][] cost = new int[rows][cols];
		todo.add(new State(0,1));

		List<Integer> pathLengths = new ArrayList<>();
		while(!todo.isEmpty()){
			State s = todo.poll();
			cost[s.x][s.y] = s.f + distance[s.x][s.y];

			if(s.x == rows-1 && s.y == cols-2){
				pathLengths.add(s.f);
				continue;
			}
				

			for (int i = 0; i < dirs.length; i++) {
				int x = s.x+deltas[i][0], y = s.y+deltas[i][1];
				if(y >= cols || y < 0 || x >= rows || x < 0) continue;
				if(map[x][y] == '.'){
					State t = new State(x, y, s);
					if(s.history.contains(t)) continue;
					if(checked.add(t)) todo.add(t);
				} 
			}
		}



        return cost[rows-1][cols-2];
    }

    @Override
    public long part2(List<String> lines) {
        return 0;
    }

	static class State{
		int x;
		int y;
		int f;
		List<State> history = new ArrayList<>();

		private State(int x, int y){
			this.x = 0;
			this.y = 0;
		}
		private State(int x, int y, State s) {
			this.x = x;
			this.y = y;
			this.f = s.f+1;
			history.addAll(s.history);
			history.add(s);
		}
		private int h(){ return f+distance[x][y]; }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			return switch(obj){
                case State that -> that.x == x && that.y == y;
                case null, default -> false;
            };
		}
	}

	enum Dir{North, East, South, West}
    
}
