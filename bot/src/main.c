#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <avr/io.h>
#include <util/delay.h>
#include "pinout.h"
#include "serial.h"
#include "stepper.h"
#include "pen.h"

// void bezier_to(struct stepper_state* state, unsigned cx, unsigned cy, unsigned x, unsigned y) {
//     const int n = 50;
//     const unsigned sx = state->x;
//     const unsigned sy = state->y;

//     for (int i = 0; i < n; ++i) {
//         double t = i / (double) n;
//         double ax = (1 - t) * (1 - t) * sx + 2 * (1 - t) * t * cx + t * t * x;
//         double ay = (1 - t) * (1 - t) * sy + 2 * (1 - t) * t * cy + t * t * y;

//         line_to(state, (unsigned) ax, (unsigned) ay);
//     }

//     line_to(state, x, y);
//}

int main() {
    LED_DDR |= LED_MASK;
    LED_PORT &= ~LED_MASK;

    stepper_init();
    pen_init();
    serial_init();

    while (true) {
        char c = serial_getchar();
        serial_putchar(c);
        LED_PORT |= LED_MASK;
        _delay_ms(100);
        LED_PORT &= ~LED_MASK;
        _delay_ms(100);
    }

    // struct stepper_state state = {0, 0};

    // const int n = 200;
    // const int r = 200;

    // stepper_line_to(&state, 1500 + r, 1500);

    // double t = 2.0 * M_PI / n;

    // pen_enable(true);

    // for (int i = 0; i <= n; ++i) {
    //     unsigned x = (unsigned)(cos(t * i * 3) * r + 1500);
    //     unsigned y = (unsigned)(sin(t * i * 2) * r + 1500);

    //     stepper_line_to(&state, x, y);
    // }

    // pen_enable(false);
    // stepper_line_to(&state, 0, 0);

    // STEPPER_DISABLE_PORT |= STEPPER_DISABLE_MASK;

    // while (true) {
    //     LED_PORT |= LED_MASK;
    //     _delay_ms(1000);
    //     LED_PORT &= ~LED_MASK;
    //     _delay_ms(1000);
    // }

    return 0;
}