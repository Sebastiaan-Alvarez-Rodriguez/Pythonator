#!/usr/bin/env python
import turtle
import math

turtle.setup(width=420, height=420)
turtle.setworldcoordinates(-10, -10, 410, 410)
turtle.speed(0)

turtle.pencolor('lightgray')
turtle.bgcolor('#F0F0F0')

def line(sx, sy, ex, ey):
    turtle.up()
    turtle.goto(sx, sy)
    turtle.down()
    turtle.goto(ex, ey)

def grid(sx, sy, ex, ey, st):
    turtle.up()

    stx = (ex - sx) / st
    sty = (ey - sy) / st

    for i in range(0, st + 1):
        x = stx * i + sx
        y = sty * i + sy
        line(x, sy, x, ey)
        line(sx, y, ex, y)

def absdiff(a, b):
    return a - b if a > b else b - a

class Turtle:
    def __init__(self, x, y, cs):
        self.x = x
        self.y = y
        self.cs = cs

    def line_rel(self, dx, dy):
        line(self.x * self.cs, self.y * self.cs, (self.x + dx) * self.cs, (self.y + dy) * self.cs)
        self.x += dx
        self.y += dy

def octline(t, sx, sy, ex, ey):
    deltax = absdiff(sx, ex)
    deltay = absdiff(sy, ey)

    if sx < ex:
        dx = 1
    else:
        dx = -1

    if sy < ey:
        dy = 1
    else:
        dy = -1

    swap = False

    if deltax < deltay:
        deltax, deltay = deltay, deltax
        swap = True

    y = 0

    for x in range(int(deltax)):
        x0 = x + 1
        y0a = y
        y0b = y + 1

        dst_a = abs(deltay * x0 - deltax * y0a)
        dst_b = abs(deltay * x0 - deltax * y0b)

        ay = 0 if dst_a < dst_b else 1
        y += ay

        if ay == 1:
            t.line_rel(dx, dy)
        elif swap:
            t.line_rel(0, dy)
        else:
            t.line_rel(dx, 0)

        # if swap:
        #     t.line_rel(ay * dx, dy)
        # else:
        #     t.line_rel(dx, ay * dy)

def testline(cs, sx, sy, ex, ey):
    turtle.pencolor('red')
    line(sx, sy, ex, ey)

    turtle.pencolor('blue')
    t = Turtle(sx // cs, sy // cs, cs)
    octline(t, sx // cs, sy // cs, ex // cs, ey // cs)

def a():
    cs = 10
    grid(0, 0, 400, 400, 400 // cs)
    # testline(cs, 200, 200, 100, 400)
    # testline(cs, 20, 40, 400, 320)
    # testline(cs, 400, 40, 20, 320)
    # testline(cs, 400, 400, 0, 20)

    # n = 9
    # r = 20 * cs

    # turtle.color('green')
    # turtle.up()
    # turtle.goto(0, 0)
    # turtle.down()
    # turtle.goto(40 * cs, 20 * cs)

    # a = 2 * 3.14159265 / n

    # for i in range(1, n + 1):
    #     x = math.cos(a * i) * 200 + 200
    #     y = math.sin(a * i) * 200 + 200

    #     print(x, y)

    #     turtle.goto(x, y)


    for i in range(0, 3):
        j = 400 / 10 * i
        testline(cs, 200, 200, 0, j)
        testline(cs, 200, 200, j, 400)
        testline(cs, 200, 200, 400, 400 - j)
        testline(cs, 200, 200, 400 - j, 0)

a()

turtle.done()