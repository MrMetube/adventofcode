package main

import "base:intrinsics"
import "core:fmt"
import "core:math"
import "core:math/linalg"
import "core:os"
import "core:slice"
import "core:strings"
import "core:strconv"
import "core:time"

Completed :: struct { num: int, func: proc(_,_:string)->(i64,i64), name, label1, label2: string }
Todo      :: struct { using _: Completed, done1, done2: bool }
Day       :: union{ Completed, Todo }

main :: proc() {
    days := [?]Day{
        Completed{ 1, day01, "Historian Hysteria", "distance", "similarity"},
        Completed{ 2, day02, "Red-Nosed Reports", "safe reports", "safe reports with tolerance"},
        Completed{ 3, day03, "Mull It Over", "uncorrupted mul instructions", "enabled mul instructions"},
        Completed{ 4, day04, "Ceres Search", "XMAS count", "X-MAS count"},
        Todo{ {5, day05, "Print Queue", "correct update middle page number sum", "fixed incorrect update middle page number sum"}, true, false},
        Todo{ {6, day06, "Guard Gallivant", "visited positions", "obstruction positions causing a loop"}, true, false},
        Completed{ 7, day07, "Bridge Repair", "total calibration result", "total calibration result with concatenation"},
        Completed{ 8, day08, "Resonant Collinearity", "antinode locations", "antinode locations with resonant harmonics"},
        Completed{ 9, day09, "Disk Fragmenter", "filesystem checksum by file block", "filesystem checksum by whole file"},
        Completed{10, day10, "Hoof It", "total trail score", "total trail ratings"},
        Completed{11, day11, "Plutonian Pebbles", "stones after 25 blinks", "stones after 75 blinks"},
        Completed{12, day12, "Garden Groups", "total fence price", "bulk order fence price"},
        // Todo{{13, day13, "Claw Contraption", "", ""}, false, false},
        Completed{14, day14, "Restroom Redoubt", "safety factor", "easter egg time"},
        Todo{{15, day15, "Warehouse Woes", "box gps coordinate sum", "wide box gps coordinate sum"}, true, false},
        Todo{{16, day16, "Reindeer Maze", "", ""}, false, false},
        Todo{{17, day17, "Chronospatial Computer", "", ""}, false, false},
        Todo{{18, day18, "", "", ""}, false, false},
        Todo{{19, day19, "", "", ""}, false, false},
        Todo{{20, day20, "", "", ""}, false, false},
        Todo{{21, day21, "", "", ""}, false, false},
        Todo{{22, day22, "", "", ""}, false, false},
        Todo{{23, day23, "", "", ""}, false, false},
        Todo{{24, day24, "", "", ""}, false, false},
        Todo{{25, day25, "", "", ""}, false, false},
    }

    init_qpc()
    
    if len(os.args) >= 2 {
        assert(len(os.args) == 2, "bad argument")
        num := strconv.atoi(os.args[1])
        do_day(days[num-1])
    } else {
        start := get_wall_clock()
        for day in days {
            do_day(day)
        }
        elapsed := get_seconds_elapsed(start, get_wall_clock())
        fmt.print("Total Time: ")
        if elapsed < 1 {
            fmt.printfln("%.3fms", elapsed * 1000)
        } else {
            fmt.printfln("%.3fs", elapsed)
        }
    }
}

dayXX :: proc(path, test_path: string) -> (part1, part2: i64) {
    line, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    return
}

day25 :: dayXX
day24 :: dayXX
day23 :: dayXX
day22 :: dayXX
day21 :: dayXX
day20 :: dayXX
day19 :: dayXX
day18 :: dayXX
day17 :: dayXX
day16 :: dayXX

day15 :: proc(path, test_path: string) -> (box_gps_coordinate_sum, wide_box_gps_coordinate_sum: i64) {
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    v2 :: [2]i64
    deltas   := [Direction]v2 { .North = {-1, 0}, .East = {0, 1}, .South = {1, 0}, .West = {0, -1}}
    Direction :: enum {East, North, West, South}
    moves: [dynamic]Direction
    
    WALLS :: 2
    rows, cols: i64 = ---, auto_cast len(lines[0]) - WALLS

    for line, i in lines {
        if line == "" {
            rows = auto_cast i - WALLS
            break
        }
    }
    Cell :: enum { Empty, Wall, Box}
    WideCell :: enum { Empty, Wall, LeftBox, RightBox}

    robot, wide_robot: v2
    warehouse := make([]Cell, rows * cols)
    wide_warehouse := make([]WideCell, rows * cols*2)

    for line, row in lines[1:][:rows] {
        for r, col in line[1:][:cols] {
            switch r {
            case 'O': 
                warehouse[row * auto_cast cols + col] = .Box
                wide_warehouse[row * auto_cast cols*2 + col*2+0] = .LeftBox
                wide_warehouse[row * auto_cast cols*2 + col*2+1] = .RightBox
            case '#': 
                warehouse[row * auto_cast cols + col] = .Wall
                wide_warehouse[row * auto_cast cols*2 + col*2+0] = .Wall
                wide_warehouse[row * auto_cast cols*2 + col*2+1] = .Wall
            case '@': 
                robot = { auto_cast row, auto_cast col }
                wide_robot = { auto_cast row, auto_cast col*2 }
            }
        }
    }
    
    for line in lines[rows+WALLS+1:] {
        for r in line {
            switch r {
            case '>': append(&moves, Direction.East)
            case '^': append(&moves, Direction.North)
            case '<': append(&moves, Direction.West)
            case 'v': append(&moves, Direction.South)
            }
        }
    }

    display :: proc { display_wide, display_normal }
    display_wide :: proc(warehouse: []WideCell, robot: v2, rows, cols: i64) {
        width := (cols+WALLS)*2 
        for col in 0..<width do fmt.print("#")
        fmt.println()
    
        for row in 0..<rows {
            fmt.print("##")
    
            for col in 0..<cols*2 {
                if robot == {auto_cast row, auto_cast col} {
                    fmt.print("@")
                } else {
                    switch warehouse[row * cols*2 + col] {
                        case .Empty: fmt.print(".")
                        case .Wall: fmt.print("#")
                        case .LeftBox: fmt.print("[")
                        case .RightBox: fmt.print("]")
                    }
                }
            }
    
            fmt.println("##")
        }
    
        for col in 0..<width do fmt.print("#")
        fmt.println()
        fmt.println()
    }
    display_normal :: proc(warehouse: []Cell, robot: v2, rows, cols: i64) {
        width := cols+WALLS 
        for col in 0..<width do fmt.print("#")
        fmt.println()
    
        for row in 0..<rows {
            fmt.print("#")
    
            for col in 0..<cols {
                if robot == {auto_cast row, auto_cast col} {
                    fmt.print("@")
                } else {
                    switch warehouse[row * cols + col] {
                        case .Empty: fmt.print(".")
                        case .Wall: fmt.print("#")
                        case .Box: fmt.print("O")
                    }
                }
            }
    
            fmt.println("#")
        }
    
        for col in 0..<width do fmt.print("#")
        fmt.println()
        fmt.println()
    }

    in_bounds :: proc(vec: v2, rows, cols: i64) -> bool {
        return vec.x >= 0 && vec.x < rows && vec.y >= 0 && vec.y < cols 
    }


    for move in moves {
        delta := deltas[move]

        {
            p := robot + delta
            if in_bounds(p, rows, cols) {
                cell := &warehouse[p.x * cols + p.y]

                switch cell^ {
                case .Empty: robot = p
                case .Wall: //Nothing
                case .Box:
                    push_loop: for push := p; in_bounds(push, rows, cols); push += delta {
                        cell_push := &warehouse[push.x * cols + push.y]

                        switch cell_push^ {
                        case .Box:  // keep looking
                        case .Wall: // can not push
                            break push_loop
                        case .Empty:
                            cell_push^ = .Box
                            cell^ = .Empty
                            robot = p

                            break push_loop
                        }
                    }
                }
            }
        }

        {
            can_move :: proc(wide_warehouse: []WideCell, p, dp: v2, move: Direction, rows, cols: i64) -> bool {
                next := p + dp
                if !in_bounds(next, rows, cols) {
                    return false
                }

                cell := wide_warehouse[next.x * cols + next.y]
                switch cell {
                case .Wall:  return false
                case .Empty: return true
                case .LeftBox, .RightBox: 
                    other_half := next + (cell == .LeftBox ? {0,1} : {0,-1})
                    switch move {
                    case .North, .South: 
                        if  can_move(wide_warehouse, next,       dp, move, rows, cols) &&
                            can_move(wide_warehouse, other_half, dp, move, rows, cols) {
                            do_move(wide_warehouse, next,        dp, move, rows, cols)
                            do_move(wide_warehouse, other_half,  dp, move, rows, cols)
                            return true
                        } else {
                            return false
                        }
                    case .West, .East: 
                        return do_move(wide_warehouse, next, dp, move, rows, cols)
                    }
                }
                unreachable()
            }
            
            do_move :: proc(wide_warehouse: []WideCell, p, dp: v2, move: Direction, rows, cols: i64) -> bool {
                cell := &wide_warehouse[p.x * cols + p.y]
                next := p + dp
                if !in_bounds(next, rows, cols) {
                    return false
                }
                cell_next := &wide_warehouse[next.x * cols + next.y]
                if can_move(wide_warehouse, p, dp, move, rows, cols) {
                    assert(cell^ != .Wall)
                    cell_next^ = cell^
                    cell^ = .Empty
                    return true
                }

                return false
            }

            if can_move(wide_warehouse, wide_robot, delta, move, rows, cols * 2) {
                do_move(wide_warehouse, wide_robot, delta, move, rows, cols * 2)
                wide_robot = wide_robot + delta
            }
        }
        
        when false {
            fmt.print("Move ")
            switch move {
                case Direction.East  : fmt.print('>')
                case Direction.North : fmt.print('^')
                case Direction.West  : fmt.print('<')
                case Direction.South : fmt.print('v')
            }
            fmt.println(":")
            display(wide_warehouse, wide_robot, rows, cols)
        }
    }
    
    calculate_coordinate_sum :: proc(target: $T, warehouse:[]T, rows, cols: i64, walls: v2) -> (result: i64) {
        for row in 0..<rows {
            for col in 0..<cols {
                c := warehouse[row * cols + col]
                if c == target {
                    result += (row+walls.x) * 100 + (col+walls.y)
                }
            }
        }
        return
    }

    box_gps_coordinate_sum      = calculate_coordinate_sum(Cell.Box, warehouse, rows, cols, {1,1})
    wide_box_gps_coordinate_sum = calculate_coordinate_sum(WideCell.LeftBox, wide_warehouse, rows, cols*2, {1,2})

    return
}

day14 :: proc(path, test_path: string) -> (safety_factor, easter_egg_time: i64) {
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    v2 :: [2]i64
    Robot :: struct {
        p, dp: v2
    }

    robots := make([]Robot, len(lines))
    for line, i in lines {
        rest := line
        r := &robots[i]
        r.p.x = trim_until_number(&rest)
        r.p.y = trim_until_number(&rest)
        r.dp.x = trim_until_number(&rest)
        r.dp.y = trim_until_number(&rest)
    }

    dim := v2{11, 7} when ODIN_DEBUG else v2{101, 103}

    display :: proc(robots: []Robot, dim: v2) {
        for row in 0..<dim.y {
            for col in 0..<dim.x {
                count: i64
                for robot in robots {
                    if robot.p == {col, row} {
                        count += 1
                    }
                }
                if count != 0 {
                    fmt.print(count)
                } else {
                    fmt.print('.')
                }
            }
            fmt.println()
        }
        fmt.println()
    }
    
    quadrants: [4]i64
    for r in robots {
        r := r
        r.p += r.dp * 100
        for r.p.x < 0 {
            r.p.x += dim.x
        }
        for r.p.x >= dim.x {
            r.p.x -= dim.x
        }
        for r.p.y < 0 {
            r.p.y += dim.y
        }
        for r.p.y >= dim.y {
            r.p.y -= dim.y
        }

        if r.p.x < dim.x/2 {
            if r.p.y < dim.y/2 {
                quadrants[0] += 1
            } else if r.p.y > dim.y/2 {
                quadrants[1] += 1
            }
        } else if r.p.x > dim.x/2 {
            if r.p.y < dim.y/2 {
                quadrants[2] += 1
            } else if r.p.y > dim.y/2 {
                quadrants[3] += 1
            }
        }
    }

    start :: 18
    for &r in robots {
        r.p += r.dp * start
        for r.p.x < 0 {
            r.p.x += dim.x
        }
        for r.p.x >= dim.x {
            r.p.x -= dim.x
        }
        for r.p.y < 0 {
            r.p.y += dim.y
        }
        for r.p.y >= dim.y {
            r.p.y -= dim.y
        }
    }

    // done manually, find the start of the pattern, then the increment, then wait
    easter_egg_time = 7492
    step_size :: 101
    for second := start; second < auto_cast easter_egg_time; second += step_size {
        when false {
            fmt.println(start + second * step_size, "seconds")
            display(robots, dim)
        }

        for &r in robots {
            r.p += r.dp * step_size
            for r.p.x < 0 {
                r.p.x += dim.x
            }
            for r.p.x >= dim.x {
                r.p.x -= dim.x
            }
            for r.p.y < 0 {
                r.p.y += dim.y
            }
            for r.p.y >= dim.y {
                r.p.y -= dim.y
            }
         }
    }

    safety_factor = quadrants[0] * quadrants[1] * quadrants[2] * quadrants[3]
    return 
}

day13 :: proc(path, test_path: string) -> (part1, part2: i64) {
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    Machine :: struct {
        a, b, prize: [2]i64,
    }

    machines: [dynamic]Machine
    for index := 0; index < len(lines); index += 4 {
        line_a     := lines[index+0]
        line_b     := lines[index+1]
        line_prize := lines[index+2]

        machine: Machine
        rest := line_a
        _, machine.a.x, rest = trim_until_number(rest)
        _, machine.a.y, rest = trim_until_number(rest)

        rest = line_b
        _, machine.b.x, rest = trim_until_number(rest)
        _, machine.b.y, rest = trim_until_number(rest)
        
        rest = line_prize
        _, machine.prize.x, rest = trim_until_number(rest)
        _, machine.prize.y, rest = trim_until_number(rest)

        append(&machines, machine)
    }

    for machine in machines {
        using machine
        m := matrix[2,2] f32 {
            auto_cast a.x, auto_cast b.x,
            auto_cast a.y, auto_cast b.y,
        }
        p := [2]f32 {auto_cast prize.x, auto_cast prize.y}
        det := linalg.determinant(m)
        fmt.println(det)
    }

    return
}

day12 :: proc(path, test_path: string) -> (total_fence_price, bulk_fence_price: i64) {
    file, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    Position  :: [2]int
    Direction :: enum {North, East, South, West}

    opposite := [Direction]Direction { .North=.South, .East=.West, .South=.North, .West=.East }
    deltas   := [Direction]Position { .North = {-1, 0}, .East = {0, 1}, .South = {1, 0}, .West = {0, -1}}

    Plant :: rune
    Plot :: struct {
        plant: Plant,
        fences: bit_set[Direction],
        visited: bool,
        region: [dynamic]Position,
    }

    rows, cols := dimensions(file)
    garden := make([]Plot, rows*cols)
    { 
        index: int
        for r in file {
            if r != '\n' {
                garden[index] = { plant = r, fences = {.North, .South, .East, .West} }
                index += 1
            }
        }
    }

    // Plan fences
    for row in 0..<rows {
        for col in 0..<cols {
            plot := &garden[row * cols + col]

            for dir in Direction {
                next := deltas[dir] + {row, col}
            
                if next.x >= 0 && next.x < rows && next.y >= 0 && next.y < cols {
                    next_plot := &garden[next.x * cols + next.y]
            
                    if next_plot.plant == plot.plant {
                        plot.fences      -= { dir }
                        next_plot.fences -= { opposite[dir] }
                    }
                }
            }
        }
    }

    // Identify regions
    for row in 0..<rows {
        for col in 0..<cols {
            pos  := Position{row, col}
            plot := &garden[pos.x * cols + pos.y]
            
            if !plot.visited {
                plot.region = {}

                candidates := [dynamic]Position{ pos }
                for len(candidates) > 0 {
                    candidate_pos := pop(&candidates)
                    candidate := &garden[candidate_pos.x * cols + candidate_pos.y]
                    
                    if !candidate.visited {
                        assert(candidate.plant == plot.plant)
                        candidate.visited = true
                        
                        append(&plot.region, candidate_pos)

                        for dir in Direction {
                            if dir not_in candidate.fences {
                                next := deltas[dir] + candidate_pos

                                if next.x >= 0 && next.x < rows && next.y >= 0 && next.y < cols {
                                    next_plot := &garden[next.x * cols + next.y]
                                    assert(candidate.plant == next_plot.plant)

                                    append(&candidates, next)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    for origin in garden {
        if origin.region != nil {
            area := len(origin.region)

            perimeter: int
            for pos in origin.region {
                plot := &garden[pos.x * cols + pos.y]
                perimeter += card(plot.fences)
            }
            
            sides: int
            {
                count_sides :: proc(side, step: Direction, origin: Plot, garden:[]Plot, rows, cols: int, deltas:[Direction]Position) -> int {
                    sides: map[Position]bool
                    for pos in origin.region {
                        pos := pos
                        plot := &garden[pos.x * cols + pos.y]
                        
                        if side in plot.fences {
                            for (step not_in plot.fences) {
                                next := deltas[step] + pos

                                assert(next.x >= 0 && next.x < rows && next.y >= 0 && next.y < cols)
                                {
                                    next_plot := &garden[next.x * cols + next.y]
                                    if side not_in next_plot.fences {
                                        break
                                    } else {
                                        plot = next_plot
                                        pos = next
                                    }
                                }
                            }
                            sides[pos] = true
                        }
                    }
                    return len(sides)
                }
                sides += count_sides(.West, .South, origin, garden, rows, cols, deltas)
                sides += count_sides(.East, .South, origin, garden, rows, cols, deltas)
                sides += count_sides(.North, .West, origin, garden, rows, cols, deltas)
                sides += count_sides(.South, .West, origin, garden, rows, cols, deltas)
            }

            total_fence_price += auto_cast (area * perimeter)
            bulk_fence_price  += auto_cast (area * sides)
        }
    }

    return
}

day11 :: proc(path, test_path: string) -> (stones_after_25_blinks, stones_after_75_blinks: i64) {
    line, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    count_digits :: proc(x: i64) -> (digits: i64 = 1) {
        x := x
        for x >= 10 {
            digits += 1
            x /= 10
        }

        return
    }

    Stones :: map[i64]i64
    current, next: Stones
    rest := line
    for rest != "" {
        num: i64
        num, rest = chop_number(rest)
        current[num] = 1
    }

    for blink in 1..=75 {
        next = {}
        
        for stone, count in current {
            digits := count_digits(stone)

            if stone == 0 {
                next[1] += count
            } else if digits % 2 == 0 {
                shift := cast(i64) math.pow10(cast(f64) (digits / 2))
                upper := stone / shift
                lower := stone - upper * shift
                
                next[upper] += count
                next[lower] += count
            } else {
                next[stone * 2024] += count
            }
        }
        
        current = next

        if blink == 25 {
            for stone, count in current {
                stones_after_25_blinks += count
            }
        }
    }
    
    for stone, count in current {
        stones_after_75_blinks += count
    }

    return 
}

day10 :: proc(path, test_path: string) -> (trailhead_scores, trailhead_ratings: i64) {
    file, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    rows, cols := dimensions(file)
    
    Step :: struct{
        height: i64,
        step_count: i64,
    }
    topo_map := make([]Step, rows * cols)
    Position :: [2]int
    trailheads: map[Position]struct{
        ends: map[Position]bool,
        splits: int,
    }
    {
        row, col: int
        rest := &file
        for rest^ != "" {
            if rest[0] == '\n' {
                rest^ = rest[1:]
                row += 1
                col = 0
            } else if rest[0] == '.' {
                rest^ = rest[1:]
                topo_map[row * cols + col] = {-1, 0}
                col += 1
            } else {
                num := chop_digit(rest)
                if num == 0 {
                    trailheads[Position{row, col}] = {}
                }
                topo_map[row * cols + col] = {num, 0}

                col += 1
            }
        }
    }

    Trail :: struct {
        start, current: Position
    }

    trails: [dynamic]Trail
    for key, _ in trailheads {
        append(&trails, Trail{start = key, current = key})
    }

    for len(trails) > 0 {
        using trail := pop_front(&trails)
        deltas := [4]Position{ { -1,  0}, {  1,  0}, {  0,  1}, {  0, -1} }
        current_step := topo_map[current.x * cols + current.y]
        if current_step.height == 9 {
            head := &trailheads[start]
            head.ends[current] = true
        } else {
            for delta in deltas {
                next := current + delta

                if next.x >= 0 && next.x < rows && next.y >= 0 && next.y < cols {
                    next_step := &topo_map[next.x * cols + next.y]
                    if next_step.height == current_step.height+1 {
                        append(&trails, Trail{ start, next })
                        next_step.step_count += 1
                    }
                }
            }
        }
    }
    
    all_ends : map[Position]Step
    for _, paths in trailheads {
        trailhead_scores += auto_cast len(paths.ends)
        for end, _ in paths.ends {
            step := topo_map[end.x * cols + end.y]
            if end not_in all_ends {
                all_ends[end] =  step
            }
        }
    }

    for _, step in all_ends {
        trailhead_ratings += step.step_count
    }

    return
}

day09 :: proc(path, test_path: string) -> (filesystem_checksum_fragmented, filesystem_checksum_whole_file: i64) {
    line, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    File :: struct {
        index, size, space: int,
    }
    DiskMap :: [dynamic]File

    diskmap_fragmented : DiskMap
    diskmap_whole_file : DiskMap
    {
        rest := line
        for rest != "" {
            size: int
            size, rest = strconv.atoi(rest[:1]), rest[1:]
            space: int
            if rest != "" {
                space, rest = strconv.atoi(rest[:1]), rest[1:]
            }
            append(&diskmap_fragmented, File{ len(diskmap_fragmented), size, space })
            append(&diskmap_whole_file, File{ len(diskmap_whole_file), size, space })
        }
    }
    
    display :: proc(diskmap: DiskMap) {
        when false {
            for file in diskmap {
                for i in 0..<file.size {
                    fmt.print(file.index)
                }
                for i in 0..<file.space {
                    fmt.print(".")
                }
            }
            fmt.println()
        }
    }

    // by file block
    for head, tail := 0, len(diskmap_fragmented)-1; head < tail; head += 1 {
        head_file := &diskmap_fragmented[head]
        tail_file := &diskmap_fragmented[tail]
        for head_file.space > 0 {
            if tail_file.size <= head_file.space {
                if head+1 == tail {
                    tail_file.space += head_file.space - tail_file.size
                    tail_file.space += tail_file.size
                    head_file.space = 0
                    break
                } else {
                    diskmap_fragmented[tail-1].space += tail_file.size + tail_file.space
                    tail_file.space = head_file.space - tail_file.size
                    head_file.space = 0
                    shift_left(&diskmap_fragmented, tail, head+1)
                }
            } else {
                tail_file.size         -= head_file.space
                tail_file.space += head_file.space
                insert(&diskmap_fragmented, File{ tail_file.index, head_file.space, 0 }, head+1)
                head_file.space = 0
                tail += 1
            }
        }
    }

    // by whole file
    expected_file_index := len(diskmap_whole_file) -1
    for tail := len(diskmap_whole_file)-1; tail >= 0; {
        tail_file := &diskmap_whole_file[tail]
        if tail_file.index > expected_file_index {
            tail -= 1
            continue
        }
        
        moved: b32
        for &head_file, head in diskmap_whole_file[0:tail] {
            if head_file.space >= tail_file.size {
                if head+1 == tail {
                    tail_file.space += head_file.space - tail_file.size
                    tail_file.space += tail_file.size
                    head_file.space = 0
                    moved = true
                    break
                } else {
                    diskmap_whole_file[tail-1].space += tail_file.size + tail_file.space
                    tail_file.space = head_file.space - tail_file.size
                    head_file.space = 0
                    shift_left(&diskmap_whole_file, tail, head+1)
                    moved = true
                    break
                }
            }
        }

        if !moved {
            tail -= 1
        }
        expected_file_index -= 1

        display(diskmap_whole_file)
    }
    
    // Chechsum
    {
        multiplier: i64
        for file in diskmap_fragmented {
            for _ in 0..<file.size {
                filesystem_checksum_fragmented += multiplier * auto_cast file.index
                multiplier += 1
            }
        }
    }
    {
        multiplier: i64
        for file in diskmap_whole_file {
            for _ in 0..<file.size {
                filesystem_checksum_whole_file += multiplier * auto_cast file.index
                multiplier += 1
            }
            multiplier += auto_cast file.space
        }
    }

    return
}

day08 :: proc(path, test_path: string) -> (unique_antinode_locations, unique_antinode_locations_with_resonant_harmonics: i64) {
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)
    
    Antenna :: rune
    Position :: [2]int
    antennas: map[Antenna][dynamic]Position

    size := Position{len(lines), len(lines[0])}
    for line, row in lines {
        for frequency, col in line {
            if frequency != '.' && frequency != '#' {
                if position, ok := &antennas[frequency]; ok {
                    append(position, [2]int{row, col})
                } else {
                    antennas[frequency] = { {row, col} }
                }
            }
        }
    }

    Antinodes :: map[Position]b32
    antinodes: map[Antenna]Antinodes
    antinodes_with_resonant_harmonics: map[Antenna]Antinodes
    for frequency, positions in antennas {
        antinodes[frequency] = {}
        nodes := &antinodes[frequency]
        nodes^ = {}
        
        antinodes_with_resonant_harmonics[frequency] = {}
        nodes_with_resonant_harmonics := &antinodes_with_resonant_harmonics[frequency]
        nodes_with_resonant_harmonics^ = {}

        for p1, i in positions {
            for p2 in positions[i+1:] {
                delta := p1 - p2

                in_bounds :: proc(size, point: Position) -> b32 {
                    return point.x < size.x && point.x >= 0 && point.y < size.y && point.y >= 0
                }
                
                a_without_resonant_harmonics := p1 + delta
                if in_bounds(size, a_without_resonant_harmonics) {
                    nodes[a_without_resonant_harmonics] = true
                }
                b_without_resonant_harmonics := p2 - delta
                if in_bounds(size, b_without_resonant_harmonics) {
                    nodes[b_without_resonant_harmonics] = true
                }
                
                for a := p2 + delta; in_bounds(size, a); a += delta {
                    nodes_with_resonant_harmonics[a] = true
                }
                for b := p1 - delta; in_bounds(size, b); b -= delta {
                    nodes_with_resonant_harmonics[b] = true
                }
            }
        }
    }

    all_antinodes: Antinodes
    for _, nodes in antinodes {
        for node, _ in nodes {
            all_antinodes[node] = true
        }
    }
    all_antinodes_with_resonant_harmonics: Antinodes
    for _, nodes in antinodes_with_resonant_harmonics {
        for node, _ in nodes {
            all_antinodes_with_resonant_harmonics[node] = true
        }
    }


    return auto_cast len(all_antinodes), auto_cast len(all_antinodes_with_resonant_harmonics)
}

day07 :: proc(path, test_path: string) -> (total_calibration_result, total_calibration_result_with_concatenation: i64) {
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)
    
    concat :: proc(a, b:i64) -> (result: i64) {
        digits_in_b := 1 if b < 2 else math.log10(f64(b))
        result = a
        for _ in 0..<digits_in_b {
            result *= 10
        }
        result += b
        return 
    }

    next_operation :: proc(numbers: [dynamic]i64, test_value: i64, current:i64, index:i64, allow_concatenation: b32 ) -> b32 {
        if index == auto_cast len(numbers) && current == test_value {
            return true
        } else if current > test_value {
            return false
        } else {
            if index == auto_cast len(numbers) {
                return false
            } else {
                number := numbers[index]
                if next_operation(numbers, test_value, current * number, index+1, allow_concatenation) {
                    return true
                }
                if next_operation(numbers, test_value, current + number, index+1, allow_concatenation) {
                    return true
                }
                if allow_concatenation && next_operation(numbers, test_value, concat(current, number), index+1, allow_concatenation) {
                    return true
                }
                return false
            }

        }
    }

    for line in lines {
        rest := line
        test_value: i64
        numbers: [dynamic]i64
        test_value, rest = chop_number(rest)
        ok: bool
        if ok, rest = expect(rest, ":"); ok {
            for rest != "" {
                num: i64
                num, rest = chop_number(rest)
                append(&numbers, num)
            }
        }

        if next_operation(numbers, test_value, numbers[0], 1, false) {
            total_calibration_result += test_value
        }
        if next_operation(numbers, test_value, numbers[0], 1, true) {
            total_calibration_result_with_concatenation += test_value
        }
    }

    return
}

day06 :: proc(path, test_path: string) -> (visited_positions, loop_possibilities: i64) {
    line, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    CellFlag :: enum{ Right, Up, Left, Down, Obstacle } 
    Cell :: bit_set[ CellFlag ]
    GuardState :: enum { Alive, OffBoard, Looped }
    Position :: struct{
        pos:[2]int, 
        dir: CellFlag
    }
    Guard :: struct {
        state: GuardState,
        using position: Position,
        start: [2]int,
        start_dir: CellFlag,
        history: map[Position]b32,
    }

    cols: int
    for r, i in line {
        if r == '\n' {
            cols = i
            break
        }
    }
    rows := len(line) / cols
    grid := make([]Cell, rows * cols)
    
    guard: Guard
    {
        row, col: int
        for r in line {
            switch r {
            case '#': grid[row * cols + col] = {.Obstacle}
            case '^', 'v', '<', '>':
                guard = {
                    pos = {row, col},
                    start = {row, col},
                }
                switch r {
                case '^': guard.dir = .Up
                case 'v': guard.dir = .Down
                case '<': guard.dir = .Left
                case '>': guard.dir = .Right
                }
                guard.start_dir = guard.dir
            case '\n':
                row += 1
                col = 0
                continue
            }

            col += 1
        }
    }

    in_bounds :: #force_inline  proc(pos:[2] int, rows, cols: int) -> b32 {
        return pos.x >= 0 && pos.x < rows && pos.y >= 0 && pos.y < cols
    }

    turn_right :: #force_inline proc(dir: CellFlag) -> (result: CellFlag) {
        #partial switch dir {
            case .Right: result = .Down
            case .Up:    result = .Right
            case .Left:  result = .Up
            case .Down:  result = .Left
        }
        return result
    }

    print_grid :: proc(rows, cols: int, guard: ^Guard, grid:[]Cell, phantoms:[dynamic]Guard) {
        when true {
            for row in 0 ..< rows {
                for col in 0 ..< cols {
                    phantom_present: b32
                    for phantom in phantoms {
                        if phantom.state == .Looped && row == phantom.start.x && col == phantom.start.y {
                            phantom_present = true
                            break
                        }
                    }

                    if phantom_present {
                        fmt.print('"')
                    } else if row == guard.pos.x && col == guard.pos.y {
                        #partial switch guard.dir {
                        case .Up:    fmt.print('^')
                        case .Down:  fmt.print('v')
                        case .Left:  fmt.print('<')
                        case .Right: fmt.print('>')
                        }
                    } else {
                        switch grid[row * cols + col] {
                        case {}: fmt.print(".")
                        case {.Obstacle}: fmt.print("#")
                        case: fmt.print("X")
                        }
                    }
                }
                fmt.println()
            }
            fmt.println()
            fmt.println("--------------------")
            fmt.println()
        }
    }
    
    update_guard :: #force_inline proc(grid: []Cell, rows, cols: int, guard: ^Guard, is_phantom: b32) {
        delta: [2]int
        #partial switch guard.dir {
          case .Right: delta = {  0,  1 }
          case .Up:    delta = { -1,  0 }
          case .Left:  delta = {  0, -1 }
          case .Down:  delta = {  1,  0 }
        }
        step := guard.pos + delta
        
        if is_phantom {
            looped: b32
            if guard.pos == guard.start && turn_right(guard.dir) == guard.start_dir {
                looped = true
            }
            if guard.position in guard.history {
                looped = true
            }
            if looped {
                guard.pos = step
                guard.state = .Looped
                return
            }
        }
        guard.history[ guard.position ] = true

        if !in_bounds(step, rows, cols) {
            guard.pos = step
            guard.state = .OffBoard
        } else {
            if grid[step.x * cols + step.y] == {.Obstacle} {
                guard.dir = turn_right(guard.dir)
            } else {
                guard.pos = step
            }

        }
    }

    phantoms: [dynamic]Guard
    spawns: map[Position]b32
    path: map[[2]int]b32
    for {
        if guard.state == .Alive {
            position := &grid[guard.pos.x * cols + guard.pos.y]
            Unvisited  :: Cell{}
            if position^ == Unvisited {
                visited_positions += 1
                path[guard.pos] = true
            }

            prev := guard
            update_guard(grid, rows, cols, &guard, false)
            if prev.dir not_in position {
                if guard.pos != guard.start {
                    phantom_dir := turn_right(prev.dir)
                    if (Position{ prev.pos, phantom_dir }) not_in spawns {
                        would_have_blocked_guards_path:= guard.pos in path
                        if !would_have_blocked_guards_path {
                            phantom := Guard{
                                pos        = prev.pos,
                                dir        = phantom_dir,
                                start      = prev.pos,
                                start_dir  = phantom_dir,
                            }
                            spawns[phantom.position] = true
                            
                            append(&phantoms, phantom)
                        }
                    }
                }
                position^ += { prev.dir }
            }
        }

        alive_count := guard.state == .Alive ? 1 : 0
        #reverse for &it in phantoms {
            for it.state == .Alive {
                alive_count += 1
                update_guard(grid, rows, cols, &it, true)
                if it.state == .Looped {
                    loop_possibilities += 1
                }
            }
        }
        
        // print_grid(rows, cols, &guard, grid, phantoms)
        if alive_count == 0 {
            break
        }

    }


    return
}

day05 :: proc(path, test_path: string) -> (correct_middle_page_sum, incorrect_middle_page_sum: i64) {
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    PageOrdering :: map[i64]^PageOrdering // befores:  [before]link
    orderings: map[i64]PageOrdering       // ordeing: [after]befores
    
    Update :: [dynamic]i64
    updates: [dynamic]Update

    // ---------------------- PARSING
    parse_orderings := true
    for line in lines {
        if line == "" {
            parse_orderings = false
            continue
        }

        if parse_orderings {
            rest := line
            before, after: i64
            before, rest = chop_number(rest)
            rest = eat(rest, "|")
            after, rest = chop_number(rest)

            if ordering, ok := &orderings[after]; ok {
                ordering[before] = nil
            } else {
                orderings[after] = PageOrdering{ before = nil }
            }
        } else {
            rest := line
            update: Update
            for rest != "" {
                a: i64
                a, rest = chop_number(rest)
                append(&update, a)
                if rest == "" {
                    break
                }
                rest = eat(rest, ",")
            }
            append(&updates, update)
        }
    }
    
    // ---------------------- Build a "tree"
    // for _, ordering in orderings {
    //     for before, &link in ordering {
    //         if linked, ok := &orderings[before]; ok {
    //             link = linked
    //         }
    //     }
    // }
    // fmt.printfln("%#v", orderings)

    // ---------------------- EVALUATING
    for update in updates {
        
        is_valid := true
        for page, page_index in update {
            if ordering, ok := orderings[page]; ok {
                for page_after in update[page_index:] {
                    if page_after in ordering {
                        is_valid = false
                        break
                    }
                }
            }
        }

        if is_valid {
            middle_page_number := update[len(update) / 2]
            correct_middle_page_sum += middle_page_number
        } else {
            // TODO(viktor): sort
            // fix the update ordering
            

            middle_page_number := update[len(update) / 2]
            incorrect_middle_page_sum += middle_page_number
        }
    }

    return
}

day04 :: proc(path, test_path: string) -> (xmas_count, x_mas_count: i64) {
    line, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)
    
    width: i64
    for r, i in line {
        if r == '\n' && width == 0 {
            width = auto_cast i+1
        }
    }
    
    length := i64(len(line))
    for i in 0 ..< length {
        is_xmas :: #force_inline proc(text: string, p0, p1, p2, p3: i64) -> (result: b32) {
            max_index := max(p0, p1, p2, p3)
            if max_index < auto_cast len(text) {
                if  text[p0] == 'X' &&
                    text[p1] == 'M' &&
                    text[p2] == 'A' &&
                    text[p3] == 'S' {
                    result = true
                }

                if  text[p0] == 'S' &&
                    text[p1] == 'A' &&
                    text[p2] == 'M' &&
                    text[p3] == 'X' {
                    result = true
                }
            }
            return result
        }

        if is_xmas(line, i+0, i+1, i+2, i+3) { // Horizontal -
            xmas_count += 1
        }

        if is_xmas(line, i+width*0, i+width*1, i+width*2, i+width*3) { // Vertical |
            xmas_count += 1
        }
        
        if is_xmas(line, i+0+width*0, i+1+width*1, i+2+width*2, i+3+width*3) { // Diagonal \
            xmas_count += 1
        }
        
        if is_xmas(line, i-0+width*0, i-1+width*1, i-2+width*2, i-3+width*3) { // Diagonal /
            xmas_count += 1
        }
        
        is_x_mas :: #force_inline proc(text: string, p0: i64, width: i64) -> (result: b32) {
            p1 := p0 + width*0 + 2
            p2 := p0 + width*1 + 1
            p3 := p0 + width*2 + 0
            p4 := p0 + width*2 + 2
            max_index := p4

            if max_index < auto_cast len(text) {
                if  text[p0] == 'M' &&
                    text[p1] == 'S' &&
                    text[p2] == 'A' &&
                    text[p3] == 'M' &&
                    text[p4] == 'S' {
                    result = true
                }
                if  text[p0] == 'M' &&
                    text[p1] == 'M' &&
                    text[p2] == 'A' &&
                    text[p3] == 'S' &&
                    text[p4] == 'S' {
                    result = true
                }
                if  text[p0] == 'S' &&
                    text[p1] == 'S' &&
                    text[p2] == 'A' &&
                    text[p3] == 'M' &&
                    text[p4] == 'M' {
                    result = true
                }
                if  text[p0] == 'S' &&
                    text[p1] == 'M' &&
                    text[p2] == 'A' &&
                    text[p3] == 'S' &&
                    text[p4] == 'M' {
                    result = true
                }

            }
            return result
        }


        if is_x_mas(line, i, width) {
            x_mas_count += 1
        }
    }

    return
}

day03 :: proc(path, test_path: string) -> (uncorrupted_sum, enabled_uncorrupted_sum: i64) {
    line, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    enabled: b32 = true
    rest:= line
    for rest != "" {
        a,b: i64
        
        mul_ok, mul_cut := find(rest, "mul")
        do_ok, do_cut := find(rest, "do")
        if mul_ok && do_ok {
            if mul_cut < do_cut {
                do_ok = false
            } else {
                mul_ok = false
            }
        } 
        
        if mul_ok {
            rest = rest[mul_cut:]
            if ok, rest = expect(rest, "("); ok {
                if ok, a, rest = trim_until_number(rest); ok {
                    if ok, rest = expect(rest, ","); ok {
                        if ok, b, rest = trim_until_number(rest); ok {
                            if ok, rest = expect(rest, ")"); ok {
                                uncorrupted_sum += a*b
                                if enabled {
                                    enabled_uncorrupted_sum += a*b
                                }
                            }
                        }
                    }
                }
            }
        } else if do_ok {
            rest = rest[do_cut:]
            if ok, rest = expect(rest, "n't()"); ok {
                enabled = false
            } else if ok, rest = expect(rest, "()"); ok {
                enabled = true
            }
        } else {
            break
        }
    }

    return
}

day02 :: proc(path, test_path: string) -> (safe_reports, safe_reports_with_dampening: i64) {
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    for line in lines {
        Report :: [dynamic]i64
        chop_report :: proc(view: string) -> (result: Report, rest: string) {
            rest = view
            for rest != "" {
                rest = trim_left(rest)
                r: i64
                r, rest = chop_number(rest)
                append(&result, r)
            }

            return
        }

        report, _ := chop_report(line)

        is_report_safe :: proc(report: Report, skipped_index: i64 = min(i64)) -> (is_safe: b32 = true) {
            descending: b32
            for i in 1..<len(report) {
                if auto_cast i == skipped_index {
                    continue
                }
                last, current := report[i-1], report[i]
                if auto_cast (i-1) == skipped_index {
                    if i == 1 {
                        continue
                    } else {
                        last = report[i-2]
                    }
                }

                if last != current {
                    descending = last > current
                    break
                }
            }

            for i in 1..<len(report) {
                if auto_cast i == skipped_index {
                    continue
                }
                last, current := report[i-1], report[i]
                if auto_cast (i-1) == skipped_index {
                    if i == 1 {
                        continue
                    } else {
                        last = report[i-2]
                    }
                }

                distance: i64
                if descending {
                    distance = last - current
                    is_safe &= last > current
                } else {
                    distance = current - last
                    is_safe &= last < current
                }
                is_safe &= distance >= 1 && distance <= 3 

                if !is_safe {
                    continue
                }
            }

            return
        }

        if is_report_safe(report) {
            safe_reports += 1
            safe_reports_with_dampening += 1
        } else {
            for dampended_index in 0..<len(report) {
                if is_report_safe(report, auto_cast dampended_index) {
                    safe_reports_with_dampening += 1
                    break
                }
            }
        }
    }


    
    return
}

day01 :: proc(path, test_path:string) -> (total_distance, similarity: i64){
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)
    
    as, bs: [dynamic]i64
    for line in lines {
        a, b: i64
        rest := line
        a, rest = chop_number(rest)
        b, rest = chop_number(rest)
        
        append(&as, a)
        append(&bs, b)
    }

    slice.sort(as[:])
    slice.sort(bs[:])

    for i in 0..<len(as) {
        distance := abs(as[i] - bs[i])
        total_distance += distance
    }

    a_last, b_count, bi: i64
    for a in as {
        defer a_last = a

        if a_last != a {
            b_count = 0
            for b, i in bs[bi:] {
                if b == a {
                    b_count += 1
                } else if b > a {
                    bi = auto_cast i
                    break
                }
            }
        }

        similarity += a * b_count
    }

    return total_distance, similarity
}

do_day :: proc{ do_day_switch, do_day_raw }
do_day_switch :: proc(day: Day) {
    switch v in day {
        case Completed: do_day(v.num, v.func, v.name, v.label1, v.label2)
        case Todo:      do_day(v.num, v.func, v.name, v.label1, v.label2, v.done1, v.done2)
    }
}
do_day_raw :: proc(num:int, day_func: proc(path, test_path: string) -> (i64, i64), name, label1, label2: string, solved1 := true, solved2 := true) {
    if day_func != dayXX {
        path      := fmt.tprintf("./data/%02d.txt", num)
        test_path := fmt.tprintf("./data/%02d_test.txt", num)
        
        start := get_wall_clock()
        d01_one, d01_two := day_func(path, test_path)
        elapsed := get_seconds_elapsed(start, get_wall_clock())
        
        fmt.printfln("Day % 2d: %v", num, name)
       
        if !solved1 {
            fmt.print("  TODO:")
        }
        fmt.printfln("  Part 1: %v (%v)", d01_one, label1)
        
        if !solved2 {
            fmt.print("  TODO:")
        }
        fmt.printfln("  Part 2: %v (%v)", d01_two, label2)

        if elapsed < 1 {
            fmt.printfln("  %.3fms", elapsed*1000)
        } else {
            fmt.printfln("  %.3fs", elapsed)
        }
    }
}
