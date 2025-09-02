import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Day04 implements Day{

    @Override
    public long part1(List<String> lines) {
        return lines.stream()
            .map(line -> line.split("[:|]"))
            .map(card -> new String[][]{ card[1].trim().split("[ ]+"), card[2].trim().split("[ ]+")})
            .mapToInt(card -> {
                Set<String> winners = Set.of(card[0]);
                long count = Arrays.stream(card[1]).filter(winners::contains).count();
                return count == 0 ? 0 : 1 << count-1;
            }).sum();
    }

    @Override
    public long part2(List<String> lines) {
        int[] cardCount = new int[lines.size()];
        long sum = cardCount.length;
        Arrays.fill(cardCount, 1);
        
        long[] wins = lines.stream()
            .map(line -> line.split("[:|]"))
            .map(card -> new String[][]{ card[1].trim().split("[ ]+"), card[2].trim().split("[ ]+")})
            .mapToLong(card -> Arrays.stream(card[1]).filter(Set.of(card[0])::contains).count())
            .toArray();

        for (int i = 0; i < wins.length; i++) {
            sum += wins[i] * cardCount[i];
            
            for (int offset = 0; offset < wins[i]; offset++)
                cardCount[i+offset+1] += cardCount[i];
        }

        return sum;
    }
    
}
