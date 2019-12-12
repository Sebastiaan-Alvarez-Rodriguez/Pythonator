# Pythonator

## Compiling

### Arduino software

The arduino part of the project requires a few dependencies:
- avr-gcc
- avr-libc
- [meson](https://mesonbuild.com/) (can be installed via pip)
- [ninja](https://ninja-build.org/)
Meson cross compilation definitions for the specific Arduino chip used in the project
(an Arduino Uno compatible device) are included in the project. When `avr-gcc` is in the path,
compilation can be done as follows:
```
$ cd bot
$ mkdir build
$ cd build
$ meson .. --cross avr-atmega328p-cross.ini
$ ninja
```

When an arduino is connected, it can be programmed by using the utility target `ninja flash`. The 
interface of the arduino can be customized in meson_options.txt, and defaults to `/dev/ttyACM0`.

### Dxf2ptn
Dxf2ptn is a simple python 3.7 script. The only non-standard dependency is [ezdxf](https://ezdxf.readthedocs.io/en/master/), which can be installed via pip.

### Simulator

The simulator requires the following dependencies:
- meson
- ninja
- [GLFW 3](https://www.glfw.org/)

Compilation can be done as follows:
```
$ cd simulator
$ mkdir build
$ meson ..
$ ninja
```

Once built, the `ptnsim` executable accepts drawing commands via stdin. The `-m` flag can be used to also show non-drawing moves.

### Ptncom

Ptncom requires no dependencies except a Linux system. The executable can be built as follows:
```
$ cd ptncom
$ make
```

The executable accept commands via stdin, and accepts a serial device file as its first parameter. Together with dxf2ptn
and ptncom, one can make a flexible setup for testing and drawing images.
