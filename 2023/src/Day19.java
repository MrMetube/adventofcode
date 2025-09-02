import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Day19 implements Day {

	@Override
	public long part1(List<String> lines) {
		HashMap<String, Workflow> workflows = parseWorkflows(lines);
		List<Part> entries = new ArrayList<>();

		boolean workflowsParsed = false;
		for (String line : lines) {
			if (!workflowsParsed && line.isEmpty()) {
				workflowsParsed = true;
			} else if (workflowsParsed) {
				String[] lineParts = line.replaceAll("[{}]", "").split(",");
				int[] values = new int[4];
				for (int i = 0; i < lineParts.length; i++) {
					String part = lineParts[i];
					int value = Integer.parseInt(part.substring(2));
					values[i] = value;
				}
				entries.add(new Part(values));
			}
		}

		long sum = 0;
		while (!entries.isEmpty()) {
			Part e = entries.getFirst();
			loop: while (true) {
				switch (e.target) {
					case "A": sum += e.xmas[0] + e.xmas[1] + e.xmas[2] + e.xmas[3];
					case "R":
						entries.removeFirst();
						break loop;
					default: e.target = workflows.get(e.target).consider(e.xmas);
				}
			}
		}

		return sum;
	}

	@Override
	public long part2(List<String> lines) {
		HashMap<String, Workflow> workflows = parseWorkflows(lines);
		List<ComplexPart> accepted = new ArrayList<>();
		List<ComplexPart> todo = new ArrayList<>();
		todo.add(new ComplexPart());

		while(!todo.isEmpty()){
			ComplexPart r = todo.removeFirst();
			switch(r.target){
				case "A" : accepted.add(r);
				case "R" : continue;
				default : {
					Workflow w = workflows.get(r.target);
					List<ComplexPart> mapped = w.consider(r);
					todo.addAll(mapped);
				}
			}
		}

		return accepted.stream()
			.mapToLong(p ->
				Arrays.stream(Category.values())
					.map(c -> p.xmas[c.ordinal()])
					.mapToLong(r -> r[1]-r[0]+1)
					.reduce(1, (v, vs) -> vs*v)
			)
			.sum();
	}

	private static HashMap<String, Workflow> parseWorkflows(List<String> lines){
		HashMap<String, Workflow> workflows = new HashMap<>();

		for (String line : lines) {
			if (line.isEmpty()) break;

			String[] lineParts = line.replace("}", "").split("\\{");
			String key = lineParts[0];
			String[] rules = lineParts[1].split(",");
			Workflow w = new Workflow(key);
			String last = null;
			for (String rule : rules) {
				if (!rule.contains(":")) {
					last = rule;
					break;
				}

				String[] ruleParts = rule.splitWithDelimiters("[\\<\\>:]", 0);
				Category cat = Category.valueOf(ruleParts[0]);
				Comparator comp = Comparator.of(ruleParts[1]);
				int value = Integer.parseInt(ruleParts[2]);
				String target = ruleParts[4];
				w.add(new Rule(cat, comp, value, target));
			}
			assert last != null : "default rule wasn't set";
			w.defaultTarget = last;
			workflows.put(key, w);
		}
		return workflows;
	}

	private static class Part {
		final int[] xmas;
		String target;

		Part(int[] x) {
			xmas = x;
			target = "in";
		}
	}

	private static class ComplexPart{
		long[][] xmas = new long[Category.values().length][2];
		final String target;
		ComplexPart(){
			Arrays.fill(xmas, new long[]{1,4000});
			target = "in";
		}

		ComplexPart(ComplexPart p, Category cat, long lower, long upper, String target){
			for (Category c : Category.values())
				xmas[c.ordinal()] = c == cat ? new long[]{lower, upper} : new long[]{p.c(c)[0], p.c(c)[1]};
			this.target = target;
		}

		ComplexPart(ComplexPart p, String target){
			xmas = new long[][]{
				new long[]{p.x()[0], p.x()[1]},
				new long[]{p.m()[0], p.m()[1]},
				new long[]{p.a()[0], p.a()[1]},
				new long[]{p.s()[0], p.s()[1]}
			};
			this.target = target;
		}

		private long[] c(Category c) { return xmas[c.ordinal()]; }

		private long[] x() { return xmas[Category.x.ordinal()]; }
		private long[] m() { return xmas[Category.m.ordinal()]; }
		private long[] a() { return xmas[Category.a.ordinal()]; }
		private long[] s() { return xmas[Category.s.ordinal()]; }

		@Override public String toString() { return String.format("[x:[%4d:%4d], m:[%4d:%4d], a:[%4d:%4d], s:[%4d:%4d]] -> %s", x()[0], x()[1], m()[0], m()[1], a()[0], a()[1], s()[0], s()[1], target); }
	}

	private enum Category {x, m, a, s}

	private enum Comparator {
		GreaterThan, LessThan;

		static Comparator of(String s) {
			return switch (s) {
				case ">" -> GreaterThan;
				case "<" -> LessThan;
				default -> throw new Error("Bad comparator sign: " + s);
			};
		}
		@Override
		public String toString() {
			return switch (this) {
				case GreaterThan -> ">";
				case LessThan -> "<";
			};
		}
	}

	private record Rule(Category cat, Comparator comp, int value, String target) {
		public String toString(){ return "%s %s %d -> %s".formatted(cat, comp, value, target); }

		boolean apply(int[] part) {
			return switch (comp) {
				case GreaterThan -> part[cat.ordinal()] > value;
				case LessThan    -> part[cat.ordinal()] < value;
			};
		}

		List<ComplexPart> apply(ComplexPart part){
			List<ComplexPart> res = new ArrayList<>();
			long[] range = part.xmas[cat.ordinal()];
			boolean splitInLowerBound = value > range[0];
			boolean splitInUpperBound = value < range[1];

			if( splitInLowerBound && !splitInUpperBound && comp == Comparator.LessThan ||
				splitInUpperBound && !splitInLowerBound && comp == Comparator.GreaterThan)
					res.add(new ComplexPart(part, target));
			else if(splitInUpperBound && splitInLowerBound){
				switch(comp){
					case GreaterThan -> {
						res.add(new ComplexPart(part, cat, range[0], value, part.target)); // lower
						res.add(new ComplexPart(part, cat, value+1, range[1], target)); // upper
					}
					case LessThan -> {
						res.add(new ComplexPart(part, cat, range[0], value-1, target)); // lower
						res.add(new ComplexPart(part, cat, value, range[1], part.target)); // upper
					}
				}
			}

			return res;
		}
	}

	private static class Workflow extends ArrayList<Rule> {
		final String name;
		String defaultTarget;

		Workflow(String name){ this.name = name; }

		String consider(int[] part) {
			for (Rule r : this) if (r.apply(part)) return r.target;
			return defaultTarget;
		}

		List<ComplexPart> consider(ComplexPart part){
			List<ComplexPart> todo = new ArrayList<>();
			List<ComplexPart> next = new ArrayList<>();
			List<ComplexPart> done = new ArrayList<>();
			todo.add(part);

			for (Rule rule : this){
				while(!todo.isEmpty()){
					ComplexPart p = todo.getFirst();
					var list = rule.apply(p);
					if(!list.isEmpty()){
						for (ComplexPart lp : list) (lp.target.equals(name) ? next : done).add(lp);
						todo.removeFirst();
					}
				}
				todo.addAll(next);
				next.clear();
			}
			todo.replaceAll(p -> p.target.equals(name) ? new ComplexPart(p, defaultTarget) : p);
			done.addAll(todo);
			return done;
		}
	}

}
