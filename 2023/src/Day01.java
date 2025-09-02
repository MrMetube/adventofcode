import java.util.List;

public class Day01 implements Day {
    public long part1(List<String> lines) {
        return lines.stream()
            .map(String::toCharArray)
            .mapToInt(line ->{
            byte first = -1;
            byte last = -1;
            for (char c : line) {
                if (Character.isDigit(c)) {
                    byte num = (byte) (c - '0');
                    first = first == -1 ? num : first;
                    last = num;
                }
            }
            return first*10 + last;
        }).sum();
    }

    public long part2(List<String> lines) {
        final String[] digits = { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
        final String[] numberDigits = {"o1e", "t2o", "t3e", "f4r", "f5e", "s6x", "s7n", "e8t", "n9e"};
        return lines.stream()
            .map(line -> {
                for (int i = 0; i<digits.length; i++)
                    line = line.replaceAll(digits[i],numberDigits[i]);
                return line;
            })
            .map(String::toCharArray)
            .mapToInt(line -> {
                byte first = -1;
                byte last = -1;
                for (char c : line) {
                    if (Character.isDigit(c)) {
                        byte num = (byte) (c - '0');
                        first = first == -1 ? num : first;
                        last = num;
                    }
                }
                return first*10 + last;
            }).sum();
    }
}
