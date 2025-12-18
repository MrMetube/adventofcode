package main

import "base:intrinsics"
import "core:fmt"
import "core:os"
import "core:strconv"
import "core:slice"

Day :: struct { using _ : struct { num: int, func: proc(_: string) -> (i64, i64), name, label1, label2: string}, todo1, todo2: bool }

main :: proc() {
    days := [?] Day {
        { num =  1, func = day01, name = "Secret Entrance",     label1 = "zeros ended on",         label2 = "zeros passed"               },
        { num =  2, func = day02, name = "Gift Shop",           label1 = "simple invalid ids",     label2 = "repeated invalid ids"       },
        { num =  3, func = day03, name = "Lobby",               label1 = "total 2 joltage",        label2 = "total 12 joltage"           },
        { num =  4, func = day04, name = "Printing Department", label1 = "accessable paper rolls", label2 = "removable paper rolls"      },
        { num =  5, func = day05, name = "Cafeteria",           label1 = "fresh ingredients",      label2 = "possibly fresh ingridients" },
        { num =  6, func = day06, name = "Trash Compactor",     label1 = "regular grand total",    label2 = "cephalopod grand total"     },
        { num =  7, func = day07, name = "Laboratories",        label1 = "beam splits",            label2 = "timelines"                  },
        { num =  8, func = day08, name = "Playground",          label1 = "top three circuits",     label2 = "fully connected circuit"    },
        { num =  9, func = day09, name = "Movie Theater",       label1 = "red tiles",              label2 = "only green red tiles"       },
        { num = 10, func = day10, name = "Factory",             label1 = "fewest button presses",  label2 = "",                          },
        { num = 11, func = day11, name = "Reactor",             label1 = "",                       label2 = "",                          },
        { num = 12, func = day12, name = "Christmas Tree Farm", label1 = "",                       label2 = "",                          },
    }

    init_qpc()
    
    if len(os.args) >= 2 {
        assert(len(os.args) == 2, "bad argument")
        num, _ := strconv.parse_int(os.args[1])
        do_day(days[num-1])
    } else {
        elapsed: f32
        for day in days {
            elapsed += do_day(day)
        }
        
        fmt.print("Total Time: ")
        if elapsed < 1 {
            fmt.printfln("%.3fms", elapsed * 1000)
        } else {
            fmt.printfln("%.3fs", elapsed)
        }
    }
}

dayXX :: proc(path: string) -> (part1, part2: i64) {
    line := read_file(path)
    lines := read_lines(path)
    
    return
}

////////////////////////////////////////////////

day12 :: dayXX
day11 :: dayXX

day10 :: proc(path: string) -> (part1, part2: i64) {
    lines := read_lines(path)
    
    Lights :: u32
    Machine :: struct {
        target: Lights,
        
        switches: [dynamic] Lights,
        joltages: [dynamic] i64,
    }
    
    machines: [dynamic] Machine
    
    for line in lines {
        m: Machine
        
        rest := line
        eat(&rest, "[")
        target: for shift: u32; rest[0] != ']'; shift += 1 {
            if rest[0] == '#' {
                m.target |= 1 << shift
            }
            rest = rest[1:]
        }
        eat(&rest, "] ")
        
        switches: for {
            if rest[0] != '(' do break switches
            eat(&rest, "(")
            lswitch: Lights
            for {
                n := chop_number(&rest)
                lswitch |= cast(Lights) 1 << cast(u32) n
                if rest[0] == ')' do break
                eat(&rest, ",")
            }
            append(&m.switches, lswitch)
            
            eat(&rest, ") ")
        }
        
        eat(&rest, "{")
        for {
            n := chop_number(&rest)
            append(&m.joltages, n)
            if rest[0] == '}' do break
            eat(&rest, ",")
        }
        eat(&rest, "}")
        
        append(&machines, m)
    }
    
    System :: struct (a, b: i32) {
        rows: [a] [b] f64,
    }
    solve :: proc (system: ^System($N, $M)) -> bool {
        // the result will consist of a NxN Identity matrix followed by a column vector of the scalars
        // i.e. row 0 will solve for col 0
        for i in system.rows do fmt.printf("%v\n", i); fmt.printf("\n")
        for target, target_column in system.rows[:len(system.rows)-1] {
            if target[target_column] == 0 do return false
            for &row in system.rows[target_column+1:] {
                // eliminate the target column from all following rows
                if row[target_column] != 0 {
                    factor := -target[target_column] / row[target_column]
                    row = target + row * factor
                }
            }
            // for i in system.rows do fmt.printf("%v\n", i); fmt.printf("\n")
        }
        
        last := len(system.rows[0])-1
        
        // we have the triangle, now make it the Identity
        #reverse for &row, target_index in system.rows {
            // Multiply out the other columns
            #reverse for single, single_index in system.rows[target_index+1:] {
                column := single_index + target_index + 1
                
                single := system.rows[column]
                
                if single[column] == 0 do return false
                factor := single[last] / single[column]
                
                row[last] -= row[column] * factor
                row[column] = 0
            }
        }
        
        // normalize the rows
        for &row, index in system.rows {
            row /= row[index]
        }
        return true
    }

    when false { // 3x3
        I := System(3, 4) {{
            { 4,-3, 1,-8},
            {-2, 1,-3,-4},
            { 1,-1, 2, 3},
        }}
        
        solve(&I)
        
        for i in I.rows do fmt.printf("%v\n", i); fmt.printf("\n")
    }
    
    when false { // 4x4
        I := System(4, 5) {{
            { 2, 1,-1, 2, 5},
            { 4, 5,-3, 6, 9},
            {-2, 5,-2, 6, 4},
            { 4,11,-4, 8, 2},
        }}
        
        solve(&I)
        
        for i in I.rows do fmt.printf("%v\n", i); fmt.printf("\n")
    }
    
    when false { // 1
        when true {
            when !true {
                N :: 5
                s := [N]f64{7, 5, 12, 7, 2}
                buttons := [?] [N] f64 {
                    {1,0,1,1,1},
                    {0,0,1,1,0},
                    {1,0,0,0,1},
                    {1,1,1,0,0},
                    {0,1,1,1,1},
                }
            } else {
                N :: 6
                s := [N]f64{10, 11, 11, 5, 10, 5}
                buttons := [?] [N] f64 {
                    {1,1,1,1,1,0},
                    {1,0,0,1,1,0},
                    {1,1,1,0,1,1},
                    {0,1,1,0,0,0},
                }
            }
        } else {
            N :: 4
            s := [N]f64{3, 5, 4, 7}
            
            buttons:= [?] [N] f64 {
                {0, 0, 0, 1},
                {0, 1, 0, 1},
                {0, 0, 1, 0},
                {0, 0, 1, 1},
                {1, 0, 1, 0},
                {1, 1, 0, 0},
            }
        }
        
        
        ps: [N][dynamic][N]f64
        for b in buttons {
            for i in 0..<N {
                if b[i] != 0 do append(&ps[i], b)
            }
        }
        
        min := max(i64)
        
        indices: [N] int
        loop: for {
            system: System(N, N+1)
            for &row, row_index in system.rows {
                row[N] = s[row_index]
            }
            for &row, row_index in system.rows {
                for i in 0..<N {
                    index := indices[i]
                    pis   := ps[i]
                    pi    := pis[index]
                    row[i] = pi[row_index]
                }
            }
            
            success := solve(&system)
            
            if success {
                fmt.println("here")
                for i in system.rows do fmt.printf("%v\n", i); fmt.printf("\n")
                                
                valid := true
                for i in system.rows {
                    x := i[N]
                    valid &&= cast(f64) (cast(i64) x) == x
                    valid &&= x >= 0
                }
                
                if valid {
                    sum: i64
                    for i in system.rows {
                        x := i[len(i)-1]
                        sum += cast(i64) x
                    }
                    
                    if min > sum {
                        for i in system.rows do fmt.printf("%v\n", i); fmt.printf("\n")
                        min = sum
                    }
                }
            }
            
            increment: for i := N-1; i >= 0; i -= 1 {
                indices[i] += 1
                if indices[i] < len(ps[i]) {
                    break increment
                }
                indices[i] = 0
                if i == 0 do break loop
            }
        }
        
        if min != max(i64) {
            part2 += min
        } else {
            fmt.println("no solutions found")
        }
    }
    
    LightsStep :: struct { lights: Lights, step: i64 }
    todo: [dynamic] LightsStep
    seen: map[Lights] i64
    for &machine in machines {
        clear(&todo)
        clear(&seen)
        
        append(&todo, LightsStep{ 0, 0 })
        seen[0] = 0
        
        success: bool
        
        search: for len(todo) != 0 {
            current := pop_front(&todo)
            
            // fmt.printfln("%v: current: %6b, %v left", current.step, current.lights, len(todo))
            for lswitch, lindex in machine.switches {
                // fmt.printf("  switch %v %6b: ", lindex, lswitch)
                next := current
                next.lights ~= lswitch
                next.step += 1
                
                if next.lights == machine.target {
                    // fmt.printfln("new state %6b / %6b with delta %v", next.lights, machine.target, intrinsics.count_ones(next.lights ~ machine.target))
                    // fmt.printfln("reached target in %v", next.step)
                    part1 += next.step
                    success = true
                    break search
                }
                
                if next.lights not_in seen {
                    // fmt.printf("new state %6b / %6b with delta %v\n", next.lights, machine.target, intrinsics.count_ones(next.lights ~ machine.target))
                    seen[next.lights] = next.step
                    append(&todo, next)
                }
            }
        }
        
        assert(success)
    }
    
    return
}

day09 :: proc(path: string) -> (largest_area, largest_green_area: i64) {
    lines := read_lines(path)
    
    v2 :: [2] i64
    red_tiles := make([]v2, len(lines))
    
    for line, i in lines {
        rest := line
        t := &red_tiles[i]
        t.x = chop_number(&rest)
        eat(&rest, ",")
        t.y = chop_number(&rest)
    }
    
    only_contains_green :: proc (red_tiles: [] v2, p_min, p_max: v2) -> bool {
        d := red_tiles[len(red_tiles)-1]
        for c in red_tiles {
            defer d = c
            
            t_min := v2{min(c.x, d.x), min(c.y, d.y)}
            t_max := v2{max(c.x, d.x), max(c.y, d.y)}
            
            if t_min.x < p_max.x && t_max.x > p_min.x && t_min.y < p_max.y && t_max.y > p_min.y {
                return false
            }
        }
        
        return true
    }
    
    for a, i in red_tiles {
        for b in red_tiles {
            min_corner := v2{min(a.x, b.x), min(a.y, b.y)}
            max_corner := v2{max(a.x, b.x), max(a.y, b.y)}
            delta := max_corner - min_corner + 1
            area := delta.x * delta.y
            
            if largest_area < area {
                largest_area = area
            }
            
            if largest_green_area < area && only_contains_green(red_tiles, min_corner, max_corner) {
                largest_green_area = area
            }
        }
    }
    
    return
}

day08 :: proc(path: string) -> (top_three_circuits, fully_connected: i64) {
    lines := read_lines(path)
    
    v3 :: [3] f32
    JunctionBox :: struct {
        p: v3,
        circuit: int,
    }
    junctions: [dynamic] ^JunctionBox
    
    for line in lines {
        rest := line
        
        box:= new(JunctionBox)
        
        box.p.x = cast(f32) chop_number(&rest)
        eat(&rest, ",")
        box.p.y = cast(f32) chop_number(&rest)
        eat(&rest, ",")
        box.p.z = cast(f32) chop_number(&rest)
        
        append(&junctions, box)
        box.circuit = len(junctions)
    }
    
    Distance :: struct {a, b: ^JunctionBox, distance_squared: f32 }
    less_distance :: proc(a, b: Distance) -> bool { return a.distance_squared < b.distance_squared }
    
    distances: [dynamic] Distance
    for &a, ai in junctions[:len(junctions)-1] {
        for &b in junctions[ai+1:] {
            d := b.p - a.p
            
            distance := Distance{
                a, b, 
                d.x * d.x + d.y * d.y + d.z * d.z
            }
            
            append(&distances, distance)
        }
    }
    
    slice.sort_by(distances[:], less_distance)
    
    remaining_circuits := len(junctions)
    
    done1, done2: bool
    
    connection_count :: 10 when ODIN_DEBUG else 999
    for d, connection_index in distances {
        if d.a.circuit != d.b.circuit {
            remaining_circuits -= 1
            assert(remaining_circuits > 0)
            if remaining_circuits == 1 {
                done2 = true
                fully_connected = cast(i64) d.a.p.x * cast(i64) d.b.p.x
            }
            
            src_circuit := d.a.circuit
            dst_circuit := d.b.circuit
            for &box in junctions {
                if box.circuit == src_circuit {
                    box.circuit = dst_circuit
                }
            }
        }
        
        if connection_index == connection_count {
            done1 = true
            
            slice.sort_by(junctions[:], proc(a, b: ^JunctionBox) -> bool { return a.circuit < b.circuit })
            
            update_top_three :: proc (top_three: ^[3]i64, count: i64) {
                if top_three[0] < count {
                    top_three[2] = top_three[1]
                    top_three[1] = top_three[0]
                    top_three[0] = count
                } else if top_three[1] < count {
                    top_three[2] = top_three[1]
                    top_three[1] = count
                } else if top_three[2] < count {
                    top_three[2] = count
                }
            }
            
            top_three: [3] i64
            previous: int
            count: i64 = 0
            for box in junctions {
                if previous != box.circuit {
                    previous = box.circuit
                    
                    update_top_three(&top_three, count)
                    count = 1
                } else {
                    count += 1
                }
            }
            update_top_three(&top_three, count)
            
            top_three_circuits = 1
            for top in top_three {
                top_three_circuits *= top
            }
        }
        
        if done1 && done2 {
            break
        }
    }
    
    return
}

day07 :: proc(path: string) -> (part1, part2: i64) {
    lines := read_lines(path)
    CellKind :: enum {
        Empty,
        Splitter,
    }
    Cell :: struct {
        kind: CellKind,
        tachyon_count: i64,
        is_start: bool,
    }
    
    rows := len(lines)
    cols := len(lines[0])
    manifold := make([] Cell, rows * cols)
    
    for line, row in lines {
        for slot, col in line {
            cell: Cell
            switch slot {
            case '.': cell.kind = .Empty
            case 'S': cell.kind = .Empty; cell.is_start = true; cell.tachyon_count = 1
            case '^': cell.kind = .Splitter
            }
            manifold[row * cols + col] = cell
        }
    }
    
    dump_manifold :: proc (manifold: [] Cell, rows, cols: int) {
        for row in 0..<rows {
            for col in 0..<cols {
                cell := manifold[row * cols + col]
                symbol: rune
                switch cell.kind {
                case .Empty:    symbol = cell.is_start ? 'S' : '.'
                case .Splitter: symbol = '^'
                }
                if cell.kind == .Empty && cell.tachyon_count > 0 {
                    symbol = '|'
                }
                fmt.print(symbol)
            }
            fmt.print('\n')
        }
        fmt.print('\n')
    }
    
    {
        for row in 0..<rows {
            for col in 0..<cols {
                cell := &manifold[row * cols + col]
                if cell.kind == .Empty && cell.tachyon_count > 0 {
                    if in_bounds_2D(col, row+1, cols, rows) {
                        down := &manifold[(row+1) * cols + col]
                        down.tachyon_count += cell.tachyon_count
                        
                        switch down.kind {
                        case .Empty: 
                        case .Splitter:
                            if in_bounds_2D(col-1, row+1, rows, cols) {
                                down_l := &manifold[(row+1) * cols + (col-1)]
                                assert(down_l.kind == .Empty)
                                down_l.tachyon_count += cell.tachyon_count
                            } 
                            if in_bounds_2D(col+1, row+1, rows, cols) {
                                down_r := &manifold[(row+1) * cols + (col+1)]
                                assert(down_r.kind == .Empty)
                                down_r.tachyon_count += cell.tachyon_count
                            }
                        }
                    }
                }
            }
        }
    }
    
    for cell in manifold {
        if cell.kind == .Splitter && cell.tachyon_count > 0 {
            part1 += 1
        }
    }
    
    row := rows-1
    for col in 0..<cols {
        cell := manifold[row * cols + col]
        if cell.kind == .Empty {
            part2 += cell.tachyon_count
        }
    }
    
    return
}

day06 :: proc(path: string) -> (grand_total, cephalopod_total: i64) {
    lines := read_lines(path)
    
    { // part 1
        rests := make([]string, len(lines))
        copy(rests[:], lines)
        
        outer: for {
            for &line in rests {
                line = trim_left(line)
                if line == "" do break outer
            }
            
            op := len(rests)-1
            assert(rests[op][0] == '*' || rests[op][0] == '+')
            is_multiply := rests[op][0] == '*'
            rests[op] = rests[op][1:]
            
            result: i64 = is_multiply ? 1 : 0 
            for &line in rests[:op] {
                num: i64
                num, line = chop_number(line)
                result = is_multiply ? result * num : result + num
            }
            grand_total += result
        }
    }
    
    { // part 2
        x := 0
        for x < len(lines[0]) {
            y := len(lines)-1
            assert(lines[y][x] == '*' || lines[y][x] == '+')
            is_multiply := lines[y][x] == '*'
            
            result: i64 = is_multiply ? 1 : 0
            for x < len(lines[0]) {
                factor: i64 = 1
                num: i64
                for y := len(lines)-2; y >= 0; y-=1 {
                    if is_numeric(cast(rune) lines[y][x]) {
                        digit := cast(i64) lines[y][x] - '0'
                        num += factor * digit
                        factor *= 10
                    }
                }
                
                if num == 0 do break
                
                result = is_multiply ? result * num : result + num
                x += 1
            }
            
            cephalopod_total += result
            x += 1
        }
    }
    
    return
}

day05 :: proc(path: string) -> (fresh_count, possibly_fresh_count: i64) {
    lines := read_lines(path)
    
    Range :: struct { begin, end: i64 }
    ranges: [dynamic] Range
    
    contains :: proc (r: Range, n: i64) -> bool { return n >= r.begin && n <= r.end  }
    
    ranges_done := false
    for line in lines {
        if line == "" { ranges_done = true; continue }
        
        if !ranges_done {
            r: Range
            rest := line
            r.begin, rest = chop_number(rest)
            rest = rest[1:] // -
            r.end, _ = chop_number(rest)
            append(&ranges, r)
        } else {
            i, _ := chop_number(line)
            is_fresh := false
        
            for r in ranges {
                if contains(r, i) {
                    is_fresh = true
                    break
                }
            }
            
            if is_fresh {
                fresh_count += 1
            }
        }
    }
    
    second: [dynamic] Range
    for {
        for &a in ranges {
            for &b in ranges {
                overlap := false
                if contains(a, b.begin) && contains(a, b.end) {
                    overlap = true
                } else if contains(a, b.begin) || contains(a, b.end) {
                    overlap = true
                }
                if overlap {
                    a.begin = min(a.begin, b.begin)
                    a.end   = max(a.end, b.end)
                    b = a
                }
            }
        }
        
        for a in ranges {
            unique := true
            for s in second {
                if s == a {
                    unique = false 
                    break
                }
            }
            
            if unique {
                append(&second, a)
            }
        }
        
        if len(second) == len(ranges) do break
        swap(&second, &ranges)
        clear(&second)
    }
    
    for r in second {
        possibly_fresh_count += r.end - r.begin + 1
    }
    
    return
}

day04 :: proc(path: string) -> (part1, part2: i64) {
    lines := read_lines(path)
    
    PaperRoll :: enum {
        Empty, Present, Reachable
    }
    
    cols := len(lines)
    rows := len(lines[0])
    paper_rolls := make([] PaperRoll, cols * rows)
    
    for line, row in lines {
        for slot, col in line {
            paper_rolls[row * cols + col] = slot == '@' ? .Present : .Empty
        }
    }
    
    print_rolls :: proc (paper_rolls: [] PaperRoll, rows, cols: int) {
        for row in 0..<rows {
            for col in 0..<cols {
                switch paper_rolls[row * cols + col] {
                case .Present:   fmt.print('@')
                case .Empty:     fmt.print(' ')
                case .Reachable: fmt.print('x')
                }
            }
            fmt.print('\n')
        }
        fmt.print('\n')
    }
    
    count_neighbours :: proc (paper_rolls: [] PaperRoll, rows, cols: int, row, col: int) -> (result: i64) {
        for dx in -1..=1 {
            for dy in -1..=1 {
                if dx == 0 && dy == 0 do continue
                
                x := row + dx
                y := col + dy
                if in_bounds_2D(x, y, rows, cols) {
                    if paper_rolls[x * cols + y] != .Empty {
                        result += 1
                    }
                }
            }
        }
        return result
    }
    
    first := true
    for {
        defer first = false
        for row in 0..<rows {
            for col in 0..<cols {
                roll := &paper_rolls[row * cols + col]
                
                if roll^ == .Present {
                    neighbour_count := count_neighbours(paper_rolls, rows, cols, row, col)
                    if neighbour_count < 4 {
                        roll ^= .Reachable
                        if first do part1 += 1
                        part2 += 1
                    }
                }
            }
        }
        
        made_progress := false
        for &roll in paper_rolls {
            if roll == .Reachable {
                roll = .Empty
                made_progress = true
            }
        }
        
        if !made_progress do break
    }
    
    return
}

day03 :: proc(path: string) -> (max_2_joltage, max_12_joltage: i64) {
    lines := read_lines(path)
    
    row: [dynamic] i64
    for line in lines {
        if len(line) == 0 do break
        clear(&row)
        
        for r in line {
            assert(r != '\r' && r != '\n')
            append(&row, cast(i64) (r-'0'))
        }
        
        get_highest_digit :: proc (row: [] i64) -> (result: i64, index: i64) {
            for it, it_index in row {
                if result < it {
                    result = it
                    index = auto_cast it_index
                }
                if result == 9 do break
            }
            
            return result, index
        }
        
        max_joltage: i64
        digits: [12] i64
        find_max_joltage :: proc (digits: [] i64, row: [] i64) -> (result: i64) {
            cursor: i64 
            cc: i64
            
            #reverse for &digit, digit_index in digits {
                digit, cc = get_highest_digit(row[cursor:len(row)-digit_index])
                cursor += cc + 1
            }
            
            factor: i64 = 1
            for digit in digits {
                result += digit * factor
                factor *= 10
            }
            
            return result
        }
        
        max_2_joltage += find_max_joltage(digits[:2], row[:])
        max_12_joltage += find_max_joltage(digits[:12], row[:])
    }
    
    return
}

day02 :: proc(path: string) -> (simple_invalid_id_sum, repeated_invalid_id_sum: i64) {
    line := read_file(path)
    
    Sandwich :: struct {
        space, repeat: i64,
        value: i64,
    }
    
    add_space :: proc (s: i64, space: i64) -> i64 {
        result := s
        for _ in 0..<space {
            result *= 10
        }
        return result
    }
    
    make_sandwich :: proc (space, repeat: i64) -> (result: i64) {
        for _ in 0..<repeat {
            result = add_space(result, space)
            result = result*10 + 1 // add slice
        }
        
        return result
    }
    
    max_digit_count := count_digits(max(i64))
    
    sandwiches: [dynamic] Sandwich
    for repeat in cast(i64) 2..<8 {
        for space in cast(i64) 0..=4 {
            total_digit_count := 1 + (space + 1) * (repeat-1)
            if total_digit_count > max_digit_count do continue
            
            sandwich := make_sandwich(space, repeat)
            append(&sandwiches, Sandwich{ space = space, repeat = repeat, value = sandwich })
        }
    }
    
    for len(line) != 0 {
        start, end: i64
        start, line = chop_number(line)
        line = line[1:]
        end, line = chop_number(line)
        line = line[1:]
        
        for i in start..=end {
            digit_count := count_digits(i)
            
            check_loop: for s in sandwiches {
                if i % s.value == 0 {
                    pattern_width := (s.space+1)
                    div, rem := digit_count / pattern_width, digit_count % pattern_width
                    
                    if rem == 0 && div == s.repeat {
                        if s.repeat == 2 {
                            simple_invalid_id_sum += i
                        }
                        repeated_invalid_id_sum += i
                        break check_loop
                    } 
                }
            }
        }
    }
    
    return
}

day01 :: proc(path: string) -> (zeros_ended_on, zeros_passed: i64){
    lines := read_lines(path)
    
    current: i64 =  50
    for line in lines {
        if len(line) == 0 do break
        
        direction := line[0]
        amount, _ := chop_number(line[1:])
        
        turns := amount / 100
        amount = amount % 100
        
        zeros_passed += turns
        
        switch direction {
        case: unreachable()
        case 'L': 
            if current != 0 && current - amount <= 0 {
                new := -(current - amount - 100) / 100
                zeros_passed += new
            }
            current -= amount
        case 'R': 
            new := (amount + current) / 100
            zeros_passed += new
            current += amount
        }
        
        current = proper_mod(current, 100)
        
        if current == 0 {
            zeros_ended_on += 1
        }
    }
    
    return
}

////////////////////////////////////////////////

do_day :: proc(day: Day) -> (elapsed: f32) {
    if day.func == dayXX do return 0
    
    path := fmt.tprintf("./data/%02d.txt", day.num) when !ODIN_DEBUG else fmt.tprintf("./data/%02d_test.txt", day.num)
    
    start := get_wall_clock()
    d1, d2: i64
    {
        context.allocator = context.temp_allocator
        d1, d2 = day.func(path)
    }
    elapsed = get_seconds_elapsed(start, get_wall_clock())
    
    fmt.printfln("Day % 2d: %v", day.num, day.name)
    
    fmt.print("  Part 1: ")
    if day.todo1 {
        fmt.printfln("TODO (%v)", day.label1)
    } else {
        fmt.printfln("%v (%v)", d1, day.label1)
    }
    
    fmt.print("  Part 2: ")
    if day.todo2 {
        fmt.printfln("TODO (%v)", day.label2)
    } else {
        fmt.printfln("%v (%v)", d2, day.label2)
    }
    
    if elapsed < 1 {
        fmt.printfln("  %.3fms", elapsed*1000)
    } else {
        fmt.printfln("  %.3fs", elapsed)
    }
    
    free_all(context.temp_allocator)
    
    return elapsed
}
