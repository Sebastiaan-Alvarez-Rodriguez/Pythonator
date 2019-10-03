#!/usr/bin/env python3
import ezdxf
import sys
import argparse
import pythonator

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

    def line(self, layer, entity):
        self.add_line(entity.dxf.start, entity.dxf.end)

    def circle(self, layer, entity):
        raise NotImplementedError()

    def entity(self, layer, entity):
        ty = entity.dxftype()
        if ty == 'LINE':
            self.line(layer, entity)
        elif ty == 'CIRCLE':
            self.circle(layer, entity)
        elif ty == 'ARC':
            pass
        elif ty == 'LWPOLYLINE':
            pass
        elif ty == 'HATCH':
            raise NotImplementedError('Hatch entities are not supported, please explode them into other primitives')
        else:
            raise NotImplementedError(f'Encountered unsupported entity of type {ty}')

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
