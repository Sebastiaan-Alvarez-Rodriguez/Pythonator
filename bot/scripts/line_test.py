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

def bezier(sx, sy, cx, cy, ex, ey):
    turtle.up()
    turtle.goto(sx, sy)
    turtle.down()

    x = sx
    y = sy

    for i in range(0, 50):
        t = i / 50
        ax = (1 - t) ** 2 * sx + 2 * (1 - t) * t * cx + t ** 2 * ex
        ay = (1 - t) ** 2 * sy + 2 * (1 - t) * t * cy + t ** 2 * ey

        line(x, y, ax, ay)

        x = ax
        y = ay

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

    def line(self, ex, ey):
        deltax = absdiff(self.x, ex)
        deltay = absdiff(self.y, ey)

        if self.x < ex:
            dx = 1
        else:
            dx = -1

        if self.y < ey:
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

            if swap:
                self.line_rel(ay * dx, dy)
            else:
                self.line_rel(dx, ay * dy)

    def bezier(self, cx, cy, ex, ey):
        sx = self.x
        sy = self.y
        for i in range(0, 10):
            t = i / 10
            x = (1 - t) ** 2 * sx + 2 * (1 - t) * t * cx + t ** 2 * ex
            y = (1 - t) ** 2 * sy + 2 * (1 - t) * t * cy + t ** 2 * ey

            self.line(x, y)
        self.line(ex, ey)

def testline(cs, sx, sy, ex, ey):
    turtle.pencolor('red')
    line(sx, sy, ex, ey)

    turtle.pencolor('blue')
    t = Turtle(sx // cs, sy // cs, cs)
    t.line(ex // cs, ey // cs)

def testbezier(cs, sx, sy, cx, cy, ex, ey):
    turtle.pencolor('green')
    line(sx, sy, cx, cy)
    line(cx, cy, ex, ey)

    turtle.pencolor('red')
    bezier(sx, sy, cx, cy, ex, ey)

    turtle.pencolor('blue')
    t = Turtle(sx // cs, sy // cs, cs)
    t.bezier(cx // cs, cy // cs, ex // cs, ey // cs)

def a():
    cs = 10
    grid(0, 0, 400, 400, 2)

    # testbezier(cs, 20, 20, 200, 300, 380, 20)

    # testline(cs, 200, 200, 100, 400)
    # testline(cs, 20, 40, 400, 320)
    # testline(cs, 400, 40, 20, 320)
    # testline(cs, 400, 400, 0, 20)

a()

turtle.done()