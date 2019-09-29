#include "serial.h"
#include <util/setbaud.h>
#include <avr/io.h>

void serial_init() {
    // Set baud rate
    UBRR0H = UBRRH_VALUE;
    UBRR0L = UBRRL_VALUE;

    // Enable baud doubler if needed
    #if USE_2X
        UCSR0A |= 1 << U2X0;
    #else
        UCSR0A &= ~(1 << U2X0);
    #endif

    // Enable receive and transfer
    UCSR0B |= (1 << RXEN0) | (1 << TXEN0);
    // Default configuration is 8-bit data, no parity, 1-bit stop.
}

void serial_putchar(char c) {
    loop_until_bit_is_set(UCSR0A, UDRE0);
    UDR0 = c;
}

char serial_getchar() {
    loop_until_bit_is_set(UCSR0A, RXC0);
    return UDR0;
}

uint16_t serial_get_u16le() {
    uint8_t low = serial_getchar();
    uint8_t high = serial_getchar();
    return (high << 8) | low;
}
