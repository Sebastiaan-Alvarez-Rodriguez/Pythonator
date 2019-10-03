#!/usr/bin/env python3
import ezdxf
import sys
import argparse
import pythonator
import math

SEGMENT_SIZE = 50

def convert_coordinates(c):
    return (int(round(c[0])), int(round(c[1])))

class DxfProcessor:
    def __init__(self, doc, layers):
        self.doc = doc
        self.msp = doc.modelspace()
        self.layers = layers
        self.lines = []

    def add_line(self, start, end):
        sx, sy = convert_coordinates(start)
        ex, ey = convert_coordinates(end)

        if sx != ex or sy != ey:
            self.lines.append([(sx, sy), (ex, ey)])

    def add_arc(self, center, radius, start_angle, arc):
        segments = int(abs(arc) * radius / SEGMENT_SIZE)
        a = arc / segments

        x = center[0] + math.cos(start_angle) * radius
        y = center[1] + math.sin(start_angle) * radius

        for i in range(1, segments + 1):
            t = a * i + start_angle
            nx = center[0] + math.cos(t) * radius
            ny = center[1] + math.sin(t) * radius
            self.add_line((x, y), (nx, ny))
            x, y = nx, ny

    def line(self, layer, entity):
        #TODO: Line styles
        self.add_line(entity.dxf.start, entity.dxf.end)

    def circle(self, layer, entity):
        self.add_arc(entity.dxf.center[:2], entity.dxf.radius, 0, math.pi * 2)

    def arc(self, layer, entity):
        center = entity.dxf.center[:2]
        radius = entity.dxf.radius
        start = math.radians(entity.dxf.start_angle)
        end = math.radians(entity.dxf.end_angle)

        if start_angle < end_angle:
            arc = end_angle - start_angle
        else:
            arc = end_angle - start_angle + math.pi * 2

        self.add_arc(center, radius, start, arc)

    def lwpolyline(self, layer, entity):
        pts = entity.get_points(format = 'xyb')
        n = len(pts)
        if not entity.closed:
            n -= 1

        def angle(a, b):
            return math.atan2(b[1] - a[1], b[0] - a[0])

        for i in range(n):
            j = (i + 1) % len(pts)
            sx, sy, bulge = pts[i]
            ex, ey, _ = pts[j]
            if bulge == 0:
                self.add_line((sx, sy), (ex, ey))
                continue

            theta = 4 * math.atan(abs(bulge))

            chord = math.sqrt((ex - sx) ** 2 + (ey - sy) ** 2)
            sagitta = chord * 0.5 * abs(bulge)
            radius = ((chord * 0.5) ** 2 + sagitta ** 2) / (2 * sagitta)

            # Calculate where the sagitta crosses the chord
            mx = (sx + ex) / 2
            my = (sy + ey) / 2

            # Calculate unit vector from start to end
            nx = (ex - sx) / chord
            ny = (ey - sy) / chord

            # Calculate the center of the arc circle
            if bulge < 0:
                nx *= -1
                ny *= -1

            cx = mx - ny * (radius - sagitta)
            cy = my + nx * (radius - sagitta)

            sa = math.atan2(sy - cy, sx - cx)

            if bulge < 0:
                theta = -theta

            self.add_arc((cx, cy), radius, sa, theta)

    def entity(self, layer, entity):
        ty = entity.dxftype()
        if ty == 'LINE':
            self.line(layer, entity)
        elif ty == 'CIRCLE':
            self.circle(layer, entity)
        elif ty == 'ARC':
            self.arc(layer, entity)
        elif ty == 'LWPOLYLINE':
            self.lwpolyline(layer, entity)
        else:
            raise NotImplementedError(f'Encountered unsupported entity of type {ty}. Use the explode tool to explode them into other primitives if possible.')

    def process(self):
        for e in self.msp:
            l = next((l for l in self.layers if l.dxf.name == e.dxf.layer), None)
            if l is not None:
                self.entity(l, e)

parser = argparse.ArgumentParser(description = 'Convert DXF files to Pythonator commands')
parser.add_argument('input', metavar = '<input>', help = 'DXF source')

grp = parser.add_mutually_exclusive_group()
grp.add_argument('--layers', metavar = '<layers>', help = 'Only include entities from these layers (comma seperated)')
grp.add_argument('--exclude-layers', metavar = '<layers>', help = 'Only include entities not from these layers (comma seperated')

args = parser.parse_args()

doc = ezdxf.readfile(args.input)

if args.layers is not None:
    arg_layers = args.layers.split(',')
    layers = [l for l in doc.layers if l.dxf.name in arg_layers]
elif args.exclude_layers is not None:
    arg_layers = args.exclude_layers.split(',')
    layers = [l for l in doc.layers if l.dxf.name not in arg_layers]

proc = DxfProcessor(doc, layers)
proc.process()

pythonator.output_simple(proc.lines)
