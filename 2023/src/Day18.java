import java.util.List;

public class Day18 implements Day{

    @Override
    public long part1(List<String> lines) {
		Order[] orders = lines.stream()
			.map(l -> l.replaceAll("[()#]",""))
			.map(l -> l.split(" "))
			.map(l -> {
				Dir dir = Dir.valueOf(l[0]);
				int len = Integer.parseInt(l[1]);
				return new Order(dir, len);
			})
			.toArray(Order[]::new);

		return getShoelace(orders);
    }

    @Override
    public long part2(List<String> lines) {
        Order[] orders = lines.stream()
			.map(l -> l.replaceAll("[()#]",""))
			.map(l -> l.split(" "))
			.map(l -> l[2])
			.map(l -> {
				String length = l.substring(0,5);
				String direction = l.substring(5);
				Dir dir = Dir.values()[Integer.parseInt(direction)];
				int len = Integer.parseInt(length,16);
				return new Order(dir, len);
			})
			.toArray(Order[]::new);

		return getShoelace(orders);
    }

	static long getShoelace(Order[] orders){
		// interior calculated with shoelace method
		long shoelace = 0;
		long border = 0;
		long x = 0, y = 0, lastX = 0, lastY = 0;
		for(Order order : orders){
			switch(order.dir){
				case U -> x-=order.len;
				case D -> x+=order.len;
				case L -> y-=order.len;
				case R -> y+=order.len;
			}
			border   += order.len;
			shoelace += x*lastY - lastX*y;
			lastX = x;
			lastY = y;
		}
		shoelace = Math.abs(shoelace)/2;
		// add border, don't double count it though
		border /= 2;
		return shoelace + 1 + border;
	}

	static record Order(Dir dir, int len){}
    enum Dir{R,D,L,U}
}