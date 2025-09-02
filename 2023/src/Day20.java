import java.util.*;

public class Day20 implements Day{

    @Override
    public long part1(List<String> lines) {
		var modules = parseModules(lines);
        Module button = modules.get("button");
		int[] count = {0,0};
		int index, back;
		Module[] todoModules  = new Module[16];
		boolean[] todoPulse   = new boolean[todoModules.length];
		Module[] todoPrevious = new Module[todoModules.length];

		int presses = 0;
		boolean done;
		do{
			index = 0;
			back  = 0;
			todoModules[0]  = button;
			todoPulse[0]    = false;
			todoPrevious[0] = null;

			Module current, previous;
			boolean pulse;
			while((current  = todoModules[index]) != null) {
				previous = todoPrevious[index];
				pulse   = todoPulse[index];

				if(current.type == Type.FlipFlop && pulse) continue;
				boolean out = switch (current.type) {
					case FlipFlop -> {
						current.isOff = !current.isOff;
						yield !current.isOff;
					}
					case Conjunction -> {
						current.inputs.replace(previous, pulse);
						yield !pulse || current.inputs.containsValue(false);
					}
					default -> false;
				};
				todoModules[index] = null;
				index = (index + 1) % todoModules.length;

				for (Module m : current.targets)
					if (m.type != Type.Output && !(out && m.type == Type.FlipFlop))
						back = insertTodo(todoModules, todoPulse, todoPrevious, back, m, out, current);

				count[out ? 1 : 0] += current.targets.length;
			}
			presses++;
			done = true;
			for(Module module : modules.values()){
				done &= switch(module.type){
					case FlipFlop    -> module.isOff;
					case Conjunction -> module.inputs.containsValue(true);
					default -> true;
				};
			}
		}while(!done && presses < 1000);

		long factor = 1000L / presses;
        return count[0]*factor * count[1]*factor;
    }

    @Override
    public long part2(List<String> lines) {
		var modules = parseModules(lines);
        Module button = modules.get("button");

		boolean done = false;
		int presses = 0;
		int index, back;
		Module[] todoModules  = new Module[16];
		boolean[] todoPulse   = new boolean[todoModules.length];
		Module[] todoPrevious = new Module[todoModules.length];

		do{
			index = 0;
			back  = 0;
			todoModules[0]  = button;
			todoPulse[0]    = false;
			todoPrevious[0] = null;

			Module current, previous;
			boolean pulse;
			while((current  = todoModules[index]) != null) {
				previous = todoPrevious[index];
				pulse    = todoPulse[index];

				if(current.type == Type.FlipFlop && pulse) continue;
				boolean out = switch (current.type) {
					case FlipFlop -> {
						current.isOff = !current.isOff;
						yield !current.isOff;
					}
					case Conjunction -> {
						current.inputs.replace(previous, pulse);
						yield !pulse || current.inputs.containsValue(false);
					}
					default -> false;
				};
				todoModules[index] = null;

				for (Module m : current.targets) {
					done = !out && "rx".equals(m.name);
					if(m.type != Type.Output && !(out && m.type == Type.FlipFlop))
						back = insertTodo(todoModules, todoPulse, todoPrevious, back, m, out, current);
				}
				index = (index + 1) % todoModules.length;
			}
			presses++;

			if(presses % 100_000 == 0) System.out.printf("               \r  %,d",presses);
		}while(!done);

        return presses;
    }

	private static int insertTodo(Module[] todoModules, boolean[] todoPulses, Module[] todoPrevious, int back, Module next, boolean out, Module current){
		back = (back + 1) % todoModules.length;
		todoModules[back]  = next;
		todoPulses[back]    = out;
		todoPrevious[back] = current;
		return back;
	}

	private static HashMap<String, Module> parseModules(List<String> lines){
		List<Module> moduleList = lines.stream()
			.map(l -> {
				String[] parts = l.split(" -> ");
				var t = parts[0].charAt(0);
				var name = parts[0].substring(1);
				var targets = parts[1].split(", ");
				Type type = switch(t){
					case '%' -> Type.FlipFlop;
					case '&' -> Type.Conjunction;
					case 'b' -> {
						name = "broadcaster";
						yield Type.Broadcast;
					}
					default -> throw new Error("Bad sign: "+t);
				};
				return new Module(type, name, targets);
			})
			.toList();
		
		HashMap<String, Module> modules = new HashMap<>(moduleList.size());
		// add all modules
		for (Module module : moduleList)
			modules.put(module.name, module);
		
		Module button = new Module(Type.Button, "button", "broadcaster");
		modules.put("button", button);
			
		// add all outputs
		for (Module module : moduleList) 
			for (String target : module.targetNames)
				modules.putIfAbsent(target, new Module(Type.Output, target));
		// initialize all conjunctions
		for (Module module : modules.values())
			if(module.type == Type.Conjunction){
				modules.values()
					.stream()
					.filter(m -> m.targetNames != null)
					.filter(m -> m.targetNames.contains(module.name))
					.forEach(mm -> module.inputs.put(mm, false));
			}
		// initialize targets
		for(Module module : modules.values()){
			if(module.targetNames != null){
				module.targets = new Module[module.targetNames.size()];
				for (int i = 0; i < module.targetNames.size(); i++) {
					module.targets[i] = modules.get(module.targetNames.get(i));
				}
			}
			module.targetNames = null;
		}
		
		return modules;
	}

	enum Type{FlipFlop, Conjunction, Broadcast, Button, Output}

	static class Module{
		final Type type;
		final String name;
		List<String> targetNames;
		Module[] targets;

		boolean isOff = true;
		HashMap<Module,Boolean> inputs;

		public Module(Type type, String name) {
			this.type = type;
			this.name = name;
		}

		public Module(Type type, String name, String... targets) {
			this.type = type;
			this.name = name;
			this.targetNames = new ArrayList<>();
			this.targetNames.addAll(Arrays.asList(targets));
			if(type == Type.Conjunction) inputs = new HashMap<>();
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			return switch(obj){
				case Module other -> name.equals(other.name);
				default -> false;
			};
		}
	}
}
