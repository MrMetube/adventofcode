import java.util.HashMap;
import java.util.List;

public class Day03 implements Day {
    enum Cell{Empty, Digit, Symbol, PartNumber, Gear}

    @Override
    public long part1(List<String> lines) {
        // pad edges with . to not join separate lines
        final int width = lines.getFirst().length()+1;
        var res = String.join(".", lines).toCharArray();

        // parse into Cell[]
        Cell[] cells = new Cell[res.length];
        for (int i = 0; i < res.length; i++){
            cells[i] = switch(res[i]){
                case '.' -> Cell.Empty;
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Cell.Digit;
                default -> Cell.Symbol;
            };
        }
        // mark all parts around a symbol
        for (int i = 0; i < cells.length; i++)
            if(cells[i] == Cell.Symbol)
                markPartNumber(cells, i, width);
        // Sum all PartNumbers
        int i = 0;
        int num = 0;
        int sum = 0;
        while(i<res.length){
            if(cells[i] == Cell.PartNumber)
                num = num*10 + res[i] - '0';
            else if(num != 0){
                sum += num;
                    num = 0;
            }
            i++;
        }

        return sum;
    }

    static void markPartNumber(Cell[] cells, int i, int width){
        if(!(i >= 0 && i < cells.length)) return;
        switch(cells[i]){
            case Empty, PartNumber, Gear: return;
            case Digit: 
                cells[i] = Cell.PartNumber;
            case Symbol:
                markPartNumber(cells, i-width-1, width);    // top left
                markPartNumber(cells, i-width, width);      // top 
                markPartNumber(cells, i-width+1, width);    // top right

                markPartNumber(cells, i-1, width); // left
                markPartNumber(cells, i+1, width); // right

                markPartNumber(cells, i+width-1, width);    // bottom left
                markPartNumber(cells, i+width, width);      // bottom 
                markPartNumber(cells, i+width+1, width);    // bottom right
            
        }
    }

    @Override
    public long part2(List<String> lines) {
        // pad edges with . to not join separate lines
        final int width = lines.getFirst().length()+1;
        var res = String.join(".", lines).toCharArray();

        // parse into Cell[]
        Cell[] cells = new Cell[res.length];
        int[] ids = new int[res.length];
        for (int i = 0; i < res.length; i++){
            cells[i] = switch(res[i]){
                case '*' -> Cell.Gear;
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Cell.Digit;
                default -> Cell.Empty;
            };
        }
        // mark all parts around a symbol
        for (int i = 0; i < cells.length; i++) if(cells[i] == Cell.Gear) markGearParts(cells, ids, i, i, width);
        
        // Sum all Gear Parts
        int i = 0;
        int num = 0;
        HashMap<Integer, int[]> ratios = new HashMap<>();
        
        while(i<cells.length){
            if(cells[i] == Cell.PartNumber)
                num = num*10 + res[i] - '0';
            else if(num != 0){
                int gear = ids[i-1];
                int[] ratio = ratios.putIfAbsent(gear, new int[]{1, num});
                if(ratio!=null){
                    ratio[0]++;
                    ratio[1]*=num;
                }
                num = 0;
            }
            i++;
        }

        int sum = 0;
        for (var ratio : ratios.values()) if(ratio[0]==2) sum += ratio[1];

        return sum;
    }

    static void markGearParts(Cell[] cells, int[] ids, int id, int i, int width){
        if(!(i >= 0 && i < cells.length)) return;
        switch(cells[i]){
            case Empty, PartNumber, Symbol: return;
            case Digit: 
                cells[i] = Cell.PartNumber;
                ids[i] = id;
            case Gear:
                markGearParts(cells, ids, id, i-width-1, width);     // top left
                markGearParts(cells, ids, id, i-width, width);       // top 
                markGearParts(cells, ids, id, i-width+1, width);     // top right
                markGearParts(cells, ids, id, i-1, width);           // left
                markGearParts(cells, ids, id, i+1, width);           // right
                markGearParts(cells, ids, id, i+width-1, width);     // bottom left
                markGearParts(cells, ids, id, i+width, width);       // bottom 
                markGearParts(cells, ids, id, i+width+1, width);     // bottom right
            
        }
    }

}
