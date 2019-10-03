import sys

class Limits:
    RANGE_X = 3000
    RANGE_Y = 2950

class Command:
    START = 0x00
    END = 0x01
    PEN_DOWN = 0x02
    PEN_UP = 0x03
    ORIG = 0x04
    LINE = 0x05

def write_binary(word, size = 1):
    sys.stdout.buffer.write(word.to_bytes(size, byteorder='little'))

def start():
    write_binary(Command.START)

def end():
    write_binary(Command.END)

def pen_down():
    write_binary(Command.PEN_DOWN)

def pen_up():
    write_binary(Command.PEN_UP)

def line(x, y):
    if x < 0 or x >= Limits.RANGE_X or y < 0 or y >= Limits.RANGE_Y:
        raise ValueError()

    write_binary(Command.LINE)

    write_binary(x, 2)
    write_binary(y, 2)

def output_simple(lines):
    x = 0
    y = 0
    start()
    pen_down()

    for (sx, sy), (ex, ey) in lines:
        if sx != x or sy != y:
            pen_up()
            line(sx, sy)
            pen_down()

        line(ex, ey)

        x = ex
        y = ey

    end()
