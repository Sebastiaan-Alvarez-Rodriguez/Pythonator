#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <avr/io.h>
#include <util/delay.h>
#include "pins.h"

// X 1475 * 4 steps
// Y 1500 * 4+ steps

#define MICROSTEPS 4
#define MAX_X 1475
#define MAX_Z 1500

void move(uint8_t bits, int steps) {
    for (int i = 0; i < steps; ++i) {
        STEPPER_PORT |= bits;
        _delay_us(10);
        STEPPER_PORT &= ~bits;
        _delay_ms(1);
    }
}

void step(uint8_t bits) {
    move(bits, MICROSTEPS);
}

struct stepper_state {
    // Since there are 1500 * 4 = 6000 for the largest axis, we can
    // store the coordinates in an unsigned int (16-bit) and we will
    // still be able to use signed ints to calculate with without problems
    unsigned x, y;
};

void stepper_line_to(struct stepper_state* state, unsigned x, unsigned y) {
    int32_t delta_x, delta_y;

    if (state->x < x) {
        STEPPER_PORT &= ~DIR_X_BIT;
        delta_x = x - state->x;
    } else {
        STEPPER_PORT |= DIR_X_BIT;
        delta_x = state->x - x;
    }

    if (state->y < y) {
        STEPPER_PORT &= ~DIR_Z_BIT;
        delta_y = y - state->y;
    } else {
        STEPPER_PORT |= DIR_Z_BIT;
        delta_y = state->y - y;
    }

    bool swap_axes = false;

    if (delta_x < delta_y) {
        swap_axes = true;

        int tmp = delta_x;
        delta_x = delta_y;
        delta_y = tmp;
    }

    int32_t j = 0;

    for (int32_t i = 0; i < delta_x; ++i) {
        int32_t dst_a = abs(delta_y * (i + 1) - delta_x * j);
        int32_t dst_b = abs(delta_y * (i + 1) - delta_x * (j + 1));

        int k = dst_a >= dst_b;
        j += k;

        if (swap_axes) {
            step(STEP_Z_BIT | (STEP_X_BIT * k));
        } else {
            step(STEP_X_BIT | (STEP_Z_BIT * k));
        }
    }

    state->x = x;
    state->y = y;
}

int main() {
    BUILTIN_LED_DDR |= BUILTIN_LED_BIT;
    BUILTIN_LED_PORT &= ~BUILTIN_LED_BIT;

    STEPPER_DISABLE_DDR |= STEPPER_DISABLE_BIT;
    STEPPER_DISABLE_PORT &= ~STEPPER_DISABLE_BIT;

    STEPPER_DDR |= STEP_X_BIT | STEP_Z_BIT | DIR_X_BIT | DIR_Z_BIT;
    STEPPER_PORT &= ~(STEP_X_BIT | STEP_Z_BIT | DIR_X_BIT | DIR_Z_BIT);

    struct stepper_state state = {0, 0};

    const int n = 50;
    const int r = 200;
    stepper_line_to(&state, 750 + r, 750);

    double a = 2.0 * M_PI / (double) n;

    for (int i = 1; i <= n; ++i) {
        unsigned x = (unsigned)(cos(a * i) * r + 750);
        unsigned y = (unsigned)(sin(a * i) * r + 750);

        stepper_line_to(&state, x, y);
    }

    stepper_line_to(&state, 0, 0);

    STEPPER_DISABLE_PORT |= STEPPER_DISABLE_BIT;

    while (true) {
        BUILTIN_LED_PORT |= BUILTIN_LED_BIT;
        _delay_ms(100);
        BUILTIN_LED_PORT &= ~BUILTIN_LED_BIT;
        _delay_ms(100);
    }

    return 0;
}