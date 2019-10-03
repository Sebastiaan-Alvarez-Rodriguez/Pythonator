#ifndef _PYTHONATOR_SERIAL_H
#define _PYTHONATOR_SERIAL_H

#include <stdint.h>

#define SERIAL_RX_BUFFER_SIZE 8

// Initialize and configure the serial port.
void serial_init();

// Send a character over serial.
void serial_put_u8(uint8_t c);

// Receive a character over serial.
uint8_t serial_get_u8();

// Receive a 16-bit integer in little endian.
uint16_t serial_get_u16le();

#endif
