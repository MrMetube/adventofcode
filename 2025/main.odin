package main

import "base:intrinsics"
import "core:fmt"
import "core:os"
import "core:strconv"

Completed :: struct { num: int, func: proc(_,_:string)->(i64,i64), name, label1, label2: string }
Todo      :: struct { using _: Completed, done1, done2: bool }
Day       :: union  { Completed, Todo }

main :: proc() {
    days := [?]Day{
        Completed{ 1, day01, "Secret Entrance", "zeros ended on", "zeros passed"},
        Completed{ 2, day02, "Gift Shop", "simple invalid ids", "repeated invalid ids"},
        Completed{ 3, day03, "Lobby", "total 2 joltage", "total 12 joltage"},
        Completed{ 4, day04, "Printing Department", "accessable paper rolls", "removable paper rolls"},
        Completed{ 5, day05, "Cafeteria", "fresh ingredients", "possibly fresh ingridients"},
        Completed{ 6, day06, "Trash Compactor", "regular grand total", "cephalopod grand total"},
        Todo{ Completed{ 7, day07, "", "", ""}, false, false},
        Todo{ Completed{ 8, day08, "", "", ""}, false, false},
        Todo{ Completed{ 9, day09, "", "", ""}, false, false},
        Todo{ Completed{10, day10, "", "", ""}, false, false},
        Todo{ Completed{11, day11, "", "", ""}, false, false},
        Todo{ Completed{12, day12, "", "", ""}, false, false},
    }

    init_qpc()
    
    if len(os.args) >= 2 {
        assert(len(os.args) == 2, "bad argument")
        num, _ := strconv.parse_int(os.args[1])
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
    line := read_file(path when !ODIN_DEBUG else test_path)
    lines := read_lines(path when !ODIN_DEBUG else test_path)
    
    return
}

////////////////////////////////////////////////

day12 :: dayXX
day11 :: dayXX
day10 :: dayXX
day09 :: dayXX
day08 :: dayXX
day07 :: dayXX

day06 :: proc(path, test_path: string) -> (grand_total, cephalopod_total: i64) {
    lines := read_lines(path when !ODIN_DEBUG else test_path)
    
    { // part 1
        rests := make([]string, len(lines))
        defer delete(rests)
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

day05 :: proc(path, test_path: string) -> (fresh_count, possibly_fresh_count: i64) {
    lines := read_lines(path when !ODIN_DEBUG else test_path)
    
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
        ranges = second
        second = make([dynamic] Range)
    }
    
    for r in second {
        possibly_fresh_count += r.end - r.begin + 1
    }
    
    return
}

day04 :: proc(path, test_path: string) -> (part1, part2: i64) {
    lines := read_lines(path when !ODIN_DEBUG else test_path)
    
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

day03 :: proc(path, test_path: string) -> (max_2_joltage, max_12_joltage: i64) {
    lines := read_lines(path when !ODIN_DEBUG else test_path)
    
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

day02 :: proc(path, test_path: string) -> (simple_invalid_id_sum, repeated_invalid_id_sum: i64) {
    line := read_file(path when !ODIN_DEBUG else test_path)
    
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

day01 :: proc(path, test_path:string) -> (zeros_ended_on, zeros_passed: i64){
    lines := read_lines(path when !ODIN_DEBUG else test_path)
    
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
