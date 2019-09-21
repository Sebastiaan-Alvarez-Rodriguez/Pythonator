# Pythonator Controller

This sub-project is the controller of the robot, ie what runs on the chip connected to the motors. Our setup includes a ripoff arduino uno (with ATmega328p cpu), an arduino CNC shield v3, and two DRV8825 stepper motor drivers, one for each axis. The pen mechanism is controlled by a small solenoid.

## Required software

To compile this part of the project, the following software is required:
- meson
- ninja
- avr-binutils
- avr-gcc
- avr-libc
- avrdude

## Compiling

To compile the controller:
```
$ mkdir build
$ cd build
$ meson .. --cross-file ../avr-atmega328p-cross.ini
$ ninja
```

To use the provided `ninja flash` target to flash the chip, set the port the arduino is connected with by passing `-Dport=/dev/<port>` to meson (usually `/dev/ttyACM0`) and add yourself to to appropriate groups (`dialout` or `uucp` commonly).
