#ifndef _PYTHONATOR_SERIAL_H
#define _PYTHONATOR_SERIAL_H

#include <stdint.h>

// Initialize and configure the serial port.
void serial_init();

// Send a character over serial.
void serial_putchar(char c);

// Receive a character over serial.
char serial_getchar();

// Receive a 16-bit integer in little endian.
uint16_t serial_get_u16le();

#endif
