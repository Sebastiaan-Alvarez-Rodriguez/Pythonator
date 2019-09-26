#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <avr/io.h>
#include <util/delay.h>
#include "pins.h"

// X 1475 * 4 steps
// Y 1500 * 4+ steps

#define MICROSTEPS 4
#define CELLS_PER_FULL_STEP 2
#define MICROSTEPS_PER_CELL MICROSTEPS / CELLS_PER_FULL_STEP

#define MAX_X 1475 * CELLS_PER_FULL_STEP
#define MAX_Z 1500 * CELLS_PER_FULL_STEP

void move(uint8_t bits, int steps) {
    for (int i = 0; i < steps; ++i) {
        STEPPER_PORT |= bits;
        _delay_us(10);
        STEPPER_PORT &= ~bits;
        _delay_ms(1);
    }
}

void step(uint8_t bits) {
    move(bits, MICROSTEPS_PER_CELL);
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

    for (int32_t i = 1; i <= delta_x; ++i) {
        int32_t dst_a = abs(delta_y * i - delta_x * j);
        int32_t dst_b = abs(delta_y * i - delta_x * (j + 1));

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

void stepper_bezier_to(struct stepper_state* state, unsigned cx, unsigned cy, unsigned x, unsigned y) {
    const int n = 50;
    const unsigned sx = state->x;
    const unsigned sy = state->y;

    for (int i = 0; i < n; ++i) {
        double t = i / (double) n;
        double ax = (1 - t) * (1 - t) * sx + 2 * (1 - t) * t * cx + t * t * x;
        double ay = (1 - t) * (1 - t) * sy + 2 * (1 - t) * t * cy + t * t * y;

        stepper_line_to(state, (unsigned) ax, (unsigned) ay);
    }

    stepper_line_to(state, x, y);
}

void pen_enable(bool state) {
    if (state) {
        PEN_PORT &= ~PEN_BIT;
    } else {
        PEN_PORT |= PEN_BIT;
    }

    _delay_ms(100);
}

int main() {
    LED_DDR |= LED_BIT;
    LED_PORT &= ~LED_BIT;

    STEPPER_DISABLE_DDR |= STEPPER_DISABLE_BIT;
    STEPPER_DISABLE_PORT &= ~STEPPER_DISABLE_BIT;

    STEPPER_DDR |= STEP_X_BIT | STEP_Z_BIT | DIR_X_BIT | DIR_Z_BIT;
    STEPPER_PORT &= ~(STEP_X_BIT | STEP_Z_BIT | DIR_X_BIT | DIR_Z_BIT);

    PEN_DDR |= PEN_BIT;
    pen_enable(false);

    struct stepper_state state = {0, 0};

    const int n = 200;
    const int r = 200;

    stepper_line_to(&state, 1500 + r, 1500);

    double t = 2.0 * M_PI / n;

    pen_enable(true);

    for (int i = 0; i <= n; ++i) {
        unsigned x = (unsigned)(cos(t * i * 3) * r + 1500);
        unsigned y = (unsigned)(sin(t * i * 2) * r + 1500);

        stepper_line_to(&state, x, y);
    }

    pen_enable(false);
    stepper_line_to(&state, 0, 0);

    STEPPER_DISABLE_PORT |= STEPPER_DISABLE_BIT;

    while (true) {
        LED_PORT |= LED_BIT;
        _delay_ms(1000);
        LED_PORT &= ~LED_BIT;
        _delay_ms(1000);
    }

    return 0;
}