import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day09 implements Day{

    @Override
    public long part1(List<String> lines) {
		List<List<Integer>> histories = lines.stream().parallel()
			.map(l -> l.split(" "))
			.map(Arrays::stream)
			.map(s -> s.mapToInt(Integer::parseInt))
			.map(IntStream::boxed)
			.map(s -> s.collect(Collectors.toList()))
			.toList();

		for (List<Integer> history : histories) {
			List<List<Integer>> derivatives = new ArrayList<>();
			derivatives.add(history);
			
			do derivatives.add(derivative(derivatives.getLast()));
			while(derivatives.getLast().stream().anyMatch(i -> i!=0));

			// add new entry extrapolated to the end
			for(int i = derivatives.size()-2; i >= 0; i--){
				var previous = derivatives.get(i+1);
				var current = derivatives.get(i);
				current.add(current.getLast() + previous.getLast());
			}
		}

        return histories.stream().parallel().mapToInt(List::getLast).sum();
    }

	static List<Integer> derivative(List<Integer> input){
		ArrayList<Integer> delta = new ArrayList<>();
		for (int i = 0; i < input.size()-1; i++) delta.add(input.get(i+1)-input.get(i));
		return delta;
	}

    @Override
    public long part2(List<String> lines) {
        List<List<Integer>> histories = lines.stream().parallel()
			.map(l -> l.split(" "))
			.map(Arrays::stream)
			.map(s -> s.mapToInt(Integer::parseInt))
			.map(IntStream::boxed)
			.map(s -> s.collect(Collectors.toList()))
			.toList();

		for (List<Integer> history : histories) {
			List<List<Integer>> derivatives = new ArrayList<>();
			derivatives.add(history);
			
			do derivatives.add(derivative(derivatives.getLast()));
			while(derivatives.getLast().stream().anyMatch(i -> i!=0));
			
			// add new entry extrapolated to the start
			for(int i = derivatives.size()-2; i >= 0; i--){
				var previous = derivatives.get(i+1);
				var current = derivatives.get(i);
				current.addFirst(current.getFirst() - previous.getFirst()); // changed sign as direction is now inverted
			}
		}
        return histories.stream().parallel().mapToInt(List::getFirst).sum();
    }
    
}
