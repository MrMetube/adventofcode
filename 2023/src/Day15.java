import java.util.Arrays;
import java.util.List;

public class Day15 implements Day{

    @Override
    public long part1(List<String> lines) {
		String[] parts = lines.getFirst().stripTrailing().split(",");
		return Arrays.stream(parts)
			.map(String::toCharArray)
			.mapToInt(Day15::holidayAsciiStringHelper)
			.sum();
    }

    @Override
    public long part2(List<String> lines) {
		char[] line = lines.getFirst().stripTrailing().concat(",").toCharArray(); // add a ',' to the end to handle the last lens
		Lens[][] boxes = new Lens[256][9];

		Lens current = new Lens();
		for (int i = 0; i < line.length; i++) {
			char c = line[i];
			switch(c){
				case '-' -> current.focalLength = 0;
				case '=' -> current.focalLength = line[++i] - '0';
				case ',' -> {
					Lens[] box = boxes[current.hashCode()];
					if(current.focalLength != 0){
						for (int l = 0; l < box.length; l++) {
							if(box[l] == null) {
								box[l] = current;
								break;
							}else if(current.label.equals(box[l].label)){
								box[l] = current;
								break;
							}
						}
					}else{
						boolean removed = false;
						for (int l = 0; l < box.length; l++) {
							if(!removed){
								if(box[l] == null) break;
								if(current.label.equals(box[l].label)) {
									box[l] = null;
									removed = true;
								}
							}else box[l-1] = box[l];
						}
					}
					current = new Lens();
				}
				default -> current.label += c;
			}
		}

		long totalPower = 0;

		for (int b = 0; b < boxes.length; b++) {
			Lens[] box = boxes[b];
			for (int l = 0; l < box.length; l++) {
				Lens lens = box[l];
				if(lens == null) break;
				int focusingPower = (b+1) * (l+1) * lens.focalLength;
				totalPower += focusingPower;
			}
		}

		return totalPower;
    }

	

	static class Lens{
		String label = "";
		int focalLength;
		@Override public int hashCode() { return holidayAsciiStringHelper(label.toCharArray()); }
		@Override public String toString() { return label + (focalLength != 0 ? focalLength : ""); }
	}

	private static int holidayAsciiStringHelper(char[] s){
		int value = 0;
		for (char c : s) {
			value += c;
			value *= 17;
			value %= 256;
		}
		return value;
	}
}
