package main

import "base:intrinsics"
import "core:fmt"
import "core:os"
import "core:strconv"

Completed :: struct { num: int, func: proc(_,_:string)->(i64,i64), name, label1, label2: string }
Todo      :: struct { using _: Completed, done1, done2: bool }
Day       :: union{ Completed, Todo }

main :: proc() {
    days := [?]Day{
        Completed{ 1, day01, "Secret Entrance", "zeros ended on", "zeros passed"},
        Completed{ 2, day02, "Gift Shop", "simple invalid ids", "repeated invalid ids"},
        Todo{{ 3, day03, "", "", ""}, false, false},
        Todo{{ 4, day04, "", "", ""}, false, false},
        Todo{{ 5, day05, "", "", ""}, false, false},
        Todo{{ 6, day06, "", "", ""}, false, false},
        Todo{{ 7, day07, "", "", ""}, false, false},
        Todo{{ 8, day08, "", "", ""}, false, false},
        Todo{{ 9, day09, "", "", ""}, false, false},
        Todo{{10, day10, "", "", ""}, false, false},
        Todo{{11, day11, "", "", ""}, false, false},
        Todo{{12, day12, "", "", ""}, false, false},
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
    line, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)

    return
}

////////////////////////////////////////////////

day12 :: dayXX
day11 :: dayXX
day10 :: dayXX
day09 :: dayXX
day08 :: dayXX
day07 :: dayXX
day06 :: dayXX
day05 :: dayXX
day04 :: dayXX
day03 :: dayXX

day02 :: proc(path, test_path: string) -> (simple_invalid_id_sum, repeated_invalid_id_sum: i64) {
    line, ok := read_file(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)
    
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
    
    for len(line) != 0 {
        start, end: i64
        start, line = chop_number(line)
        line = line[1:]
        end, line = chop_number(line)
        line = line[1:]
        
        for i in start..=end {
            digit_count := count_digits(i)
            
            check_loop: for repeat in cast(i64) 2..<8 {
                for space in cast(i64) 0..=4 {
                    total_digit_count := 1 + (space + 1) * (repeat-1)
                    if total_digit_count > max_digit_count do continue
                    
                    sandwich := make_sandwich(space, repeat)
                    
                    pattern_width := (space+1)
                    
                    if digit_count % pattern_width == 0 && digit_count / pattern_width == repeat {
                        if i % sandwich == 0 {
                            if repeat == 2 {
                                simple_invalid_id_sum += i
                            }
                            repeated_invalid_id_sum += i
                            break check_loop
                        } 
                    }
                }
            }
        }
    }
    
    return
}

day01 :: proc(path, test_path:string) -> (zeros_ended_on, zeros_passed: i64){
    lines, ok := read_lines(path when !ODIN_DEBUG else test_path)
    assert(auto_cast ok)
    
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
