#include "serial.h"
#include <util/setbaud.h>
#include <avr/io.h>
#include <avr/interrupt.h>
#include "pinout.h"

struct {
    uint8_t buffer[SERIAL_RX_BUFFER_SIZE + 1];
    uint8_t read_head;
    volatile uint8_t write_head;
} rx;

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

    // Enable receive, tranfer, and receive interrupt
    UCSR0B |= (1 << RXEN0) | (1 << TXEN0) | (1 << RXCIE0);
    // Default configuration is 8-bit data, no parity, 1-bit stop.

    rx.read_head = 0;
    rx.write_head = 0;
}

void serial_put_u8(uint8_t c) {
    loop_until_bit_is_set(UCSR0A, UDRE0);
    UDR0 = c;
    loop_until_bit_is_set(UCSR0A, UDRE0);
}

uint8_t serial_get_u8() {
    // Wait until data is available
    while (rx.read_head == rx.write_head);

    uint8_t data = rx.buffer[rx.read_head];
    ++rx.read_head;
    if (rx.read_head == SERIAL_RX_BUFFER_SIZE + 1) {
        rx.read_head = 0;
    }

    return data;
}

uint16_t serial_get_u16le() {
    uint8_t low = serial_get_u8();
    uint8_t high = serial_get_u8();
    return (high << 8) | low;
}

ISR(USART_RX_vect) {
    uint8_t data = UDR0;

    uint8_t next_write = rx.write_head + 1;
    if (next_write == SERIAL_RX_BUFFER_SIZE + 1) {
        next_write = 0;
    }

    if (next_write != rx.read_head) {
        rx.buffer[rx.write_head] = data;
        rx.write_head = next_write;
    }
}
