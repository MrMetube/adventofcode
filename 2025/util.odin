package main

import "core:fmt"
import "core:strings"
import "core:strconv"
import "core:os"
import "core:math"
import win "core:sys/windows"
import "base:intrinsics"


proper_mod :: proc (value, mod: $T) -> (result: T) {
    result = ((value % mod) + mod) % mod
    return result
}

count_digits :: proc (value: $T) -> (result: T) {
    result = (cast(T) math.log10(cast(f64) value)) + 1
    return result
}

// ---------------------- ---------------------- ----------------------
// ---------------------- array functions
// ---------------------- ---------------------- ----------------------

in_bounds_2D :: proc (x,y: int, cols, rows: int) -> bool {
    return x >= 0 && x < cols && y >= 0 && y < rows
}

insert :: proc(array: ^[dynamic]$T, element: T, index: int) {
    resize(array, len(array)+1)
    copy(array[index+1:], array[index:])
    array[index] = element
}

shift_left :: proc(array: ^[dynamic]$T, src_index, dst_index:int) {
    assert(src_index > dst_index)
    element := array[src_index]
    copy(array[dst_index+1:src_index+1], array[dst_index:src_index])
    array[dst_index] = element
}


// ---------------------- ---------------------- ----------------------
// ---------------------- parsing
// ---------------------- ---------------------- ----------------------


dimensions :: proc(file: string) -> (rows, cols: int) {
    for r, i in file {
        if r == '\n' {
            cols = i
            rows = len(file) / cols
            break
        }
    }
    
    return 
}


line_to_numbers :: proc(line:string, separator := ",") -> ([]int) {
	numbers := strings.split(line, separator)
	result := make([]int, len(numbers))
	for n, i in numbers {
		result[i], _ = strconv.parse_int(n)
	}
	return result
}

chop_line :: proc(view: string) -> (result: string, rest:string) {
    cut := 0
    for r, index in view {
        if r == '\n' {
            cut = index
            break
        }
    }

    return view[:cut], view[cut:]
}

trim_left :: proc(view: string) -> (result: string) {
    result = view
    for result != "" && strings.is_space(cast(rune) result[0]) {
        result = result[1:]
    }
    return result
}

chop_digit :: proc(view: ^string) -> (result: i64) {
    r := cast(rune) view[0]
    if is_numeric(r) {
        view^ = view[1:]
        result = cast(i64) (r - '0')
    }
    return
}

chop_number :: proc { chop_number_mut, chop_number_ret }
chop_number_mut :: proc(view: ^string) -> (result: i64) {
    result, view^ = chop_number(view^)
    return result
}
chop_number_ret :: proc(view: string) -> (result: i64, rest: string) {
    view := trim_left(view)
    cut := 0
    for r in view {
        if is_numeric(r) {
            cut +=1
        } else {
            break
        }
    }
    
    result, _ = strconv.parse_i64(view[:cut])
    rest = view[cut:]
    return result, rest
}

trim_until_number :: proc { trim_until_number_copy, trim_until_number_ref}
trim_until_number_copy :: proc(view: string) -> (ok: bool, chopped: i64, rest: string) {
    for r, r_index in view {
        if is_numeric(r) || r == '-' {
            start := r_index
            end   := start
            for n in view[r_index+1:] {
                if is_numeric(n) {
                    end += 1
                } else {
                    break
                }
            }
            end += 1
            number := view[start:end]
            chopped, _ = strconv.parse_i64(number)
            return true, chopped, view[end:]
        }
    }

    return false, chopped, view
}

trim_until_number_ref :: proc(view: ^string) -> (chopped: i64, ok: bool) #optional_ok {
    for r, r_index in view {
        if is_numeric(r) || r == '-' {
            start := r_index
            end   := start
            for n in view[r_index+1:] {
                if is_numeric(n) {
                    end += 1
                } else {
                    break
                }
            }
            end += 1
            number := view[start:end]
            chopped, _ = strconv.parse_i64(number)
            view^ = view[end:]
            return chopped, true
        }
    }

    return chopped, false
}

eat :: proc { eat_mut, eat_ret } 
eat_mut :: proc (view: ^string, target: string) {
    view ^= eat(view^, target)
}
eat_ret :: proc(view: string, target: string) -> (rest: string) {
    cut := len(target)
    if view[:cut] == target {
        return view[cut:]
    } else {
        return view
    }
}

expect :: proc(view: string, target: string) -> (ok: bool, rest: string) {
    cut := len(target)
    if view[:cut] == target {
        return true, view[cut:]
    } else {
        return false, view
    }
}

find :: proc(view: string, target: string) -> (ok: bool, index:i32) {
    for r, r_index in view {
        if len(view) - r_index < len(target) {
            break
        }

        if r == cast(rune) target[0] {
            if view[r_index:][:len(target)] == target {
                cut := r_index + len(target)
                return true, auto_cast cut
            }
        }
    }

    return false, 0
}

trim :: proc(view: string, target: string) -> (ok: bool, rest: string) {
    cut: i32
    ok, cut = find(view, target)
    if ok {
        return true, view[cut:]
    } else {
        return false, view
    }
}


is_numeric :: proc(r: rune) -> bool {
    switch r {
    case '0'..='9': return true
    case:           return false
    }
}

read_file :: proc(file: string) -> (result: string) {
	data, ok := os.read_entire_file(file)
    assert(ok)
	return string(data)
}

read_lines :: proc(file: string) -> (result: []string) {
	data, ok := os.read_entire_file(file)
    assert(ok)
	return strings.split_lines(string(data))
}


// ---------------------- ---------------------- ----------------------
// ---------------------- timing
// ---------------------- ---------------------- ----------------------


@(private="file")
GLOBAL_perf_counter_frequency : win.LARGE_INTEGER

init_qpc :: #force_inline proc() {
    win.QueryPerformanceFrequency(&GLOBAL_perf_counter_frequency)
}

get_wall_clock :: #force_inline proc() -> i64 {
    assert(GLOBAL_perf_counter_frequency != 0)
    result : win.LARGE_INTEGER
    win.QueryPerformanceCounter(&result)
    return cast(i64) result
}

get_seconds_elapsed :: #force_inline proc(start, end: i64) -> f32 {
    return f32(end - start) / f32(GLOBAL_perf_counter_frequency)
}


// ---------------------- ---------------------- ----------------------


index_in_bounds :: #force_inline proc "contextless" (index: int, array:[]$T) -> bool {
	return index >= 0 && index < len(array)
}

kilobytes :: proc(value: $N) -> N where intrinsics.type_is_numeric(N) && size_of(N) == 8 { return value*1024 }
megabytes :: proc(value: $N) -> N where intrinsics.type_is_numeric(N) && size_of(N) == 8 { return kilobytes(value)*1024 }
gigabytes :: proc(value: $N) -> N where intrinsics.type_is_numeric(N) && size_of(N) == 8 { return megabytes(value)*1024 }
terabytes :: proc(value: $N) -> N where intrinsics.type_is_numeric(N) && size_of(N) == 8 { return gigabytes(value)*1024 }

swap :: proc(a, b: ^$T) {
    b^, a^ = a^, b^
}

print_bits :: proc(a: u8) {
	for i:=256; i>0; i >>= 1 {
		fmt.print('1' if a & u8(i) != 0 else '0')
	}
	fmt.println()
}
