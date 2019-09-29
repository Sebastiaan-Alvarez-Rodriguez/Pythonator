#ifndef _PYTHONATOR_SERIAL_H
#define _PYTHONATOR_SERIAL_H

// Initialize and configure the serial port.
void serial_init();

// Send a character over serial. Blocks until the character has been sent.
void serial_putchar(char c);

// Receive a character over serial. Blocks until a character has been received.
char serial_getchar();

#endif
