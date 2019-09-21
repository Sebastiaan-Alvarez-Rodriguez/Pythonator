#include <stdbool.h>
#include <avr/io.h>
#include <util/delay.h>

int main() {
    DDRB |= 1 << PB5;

    while (true) {
        PORTB = 1 << PB5;
        _delay_ms(1000);
        PORTB = 0;
        _delay_ms(100);
    }

    return 0;
}