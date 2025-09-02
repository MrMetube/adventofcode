import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day12 implements Day {

	@Override
	public long part1(List<String> lines) {
		long arrangements = lines.stream().mapToLong(l -> {
			String[] record = l.split(" ");
			List<Integer> conditions = Arrays.stream(record[1].split(",")).mapToInt(Integer::parseInt).boxed()
					.collect(Collectors.toList());
			// String springs = record[0].replaceAll("\\?","I").replaceAll("[.]","o");
			String springs = reduceOperationalSprings(record[0]);

			return solve(springs, conditions);
		}).sum();
		System.out.println();
		return arrangements;
	}

	@Override
	public long part2(List<String> lines) {
		return 0;
	}

	private long solve(String springs, List<Integer> conditions) {
		{
			int minLength = -1;
			for (int i : conditions)
				minLength += i + 1;
			if (springs.length() < minLength)
				return 0;
		}

		final String original = springs;
		String[] blocks = springs.split("\\.");
		if (blocks.length != conditions.size()) {
			for (int condition : conditions) {
				springs = tryMark(springs, condition);
			}
			// TODO recursion with substring and sublist?!??
			springs = reduceOperationalSprings(springs);
			blocks = springs.split("\\.");
		}

		if (blocks.length != conditions.size()) {
			return solve(original.substring(1, original.length()), conditions);
		}

		long arrangements = 1;
		for (int i = 0; i < blocks.length; i++) {
			arrangements *= arrangements(blocks[i], conditions.get(i));
		}

		System.out.printf("%-20s : %12s : %d\n".formatted(springs, conditions, arrangements));
		// TODO collect all solutions in a set to filter duplicates
		// but also just calculate arrangements to not need to generate all versions
		return arrangements + solve(original.substring(1, original.length()), conditions);
	}

	private String tryMark(String springs, int condition) {
		String edge = "[\\?\\.]";
		String replaced = springs;
		if (condition != 1) {
			String target = "(#|\\?){" + condition + "}";
			String replacement = "#".repeat(condition);
			replaced = springs.replaceFirst(edge + target + edge, "." + replacement + ".");
			if (replaced.equals(springs)) {
				replaced = springs.replaceFirst(edge + target, "." + replacement);
				if (replaced.equals(springs)) {
					replaced = springs.replaceFirst(target + edge, replacement + ".");
					if (replaced.equals(springs)) {
						replaced = springs.replaceFirst(target, replacement);
					}
				}
			}
		} else {
			String[] regeces      = {"^\\?\\.", "^\\?\\?", "\\.\\?", "\\.\\?\\.", "\\.#\\?", "\\?#\\?"};
			String[] replacements = {"#.",      "#.",      ".#",     ".#.",       ".#.",     ".#."};
			for (int i = 0; i < replacements.length; i++) {
				replaced = springs.replaceFirst(regeces[i], replacements[i]);
				if(!replaced.equals(springs)) break;
			}
		}
		// assert !replaced.equals(springs) : "didnt Mark replacement";
		return replaced;
	}

	private static long arrangements(String block, int condition) {
		if (!block.contains("?"))
			return 1;
		if (!block.contains("#"))
			return nChooseK(block.length(), condition);
		if (block.length() == condition)
			return 1;
		String target = "#".repeat(condition);
		if(block.contains(target)) return 1;
		// assert false : "unimplemented: "+block.length()+" "+condition;
		return 0;
	}

	private static String reduceOperationalSprings(String s) {
		return s.replaceAll("\\.+", ".").replaceAll("^\\.", "").replaceAll("\\.$", "");
	}

	private static long nChooseK(long n, long k) {
		return factorial(n) / (factorial(k) * factorial(n - k));
	}

	private static long factorial(long a) {
		long fac = 1;
		for (long i = a; i > 1; i--)
			fac *= i;
		return fac;
	}
}
