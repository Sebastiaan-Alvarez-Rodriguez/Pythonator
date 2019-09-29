#include "stepper.h"
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <util/delay.h>
#include "pinout.h"

struct stepper_state {
    // Since there are 1500 * 2 = 3000 for the largest axis, we can
    // store the coordinates in an unsigned 16-bit int and we will
    // still be able to use signed ints to calculate with without problems
    uint16_t x, y;
};

static struct stepper_state stepper_state;

void stepper_init() {
    STEPPER_DDR |= STEPPER_STEP_X_MASK | STEPPER_STEP_Y_MASK | STEPPER_DIR_X_MASK | STEPPER_DIR_Y_MASK;
    STEPPER_PORT &= ~(STEPPER_STEP_X_MASK | STEPPER_STEP_Y_MASK | STEPPER_DIR_X_MASK | STEPPER_DIR_Y_MASK);

    STEPPER_DISABLE_DDR |= STEPPER_DISABLE_MASK;
    STEPPER_DISABLE_PORT |= STEPPER_DISABLE_MASK;
}

void stepper_disable() {
    STEPPER_DISABLE_PORT |= STEPPER_DISABLE_MASK;
}

void stepper_enable() {
    STEPPER_DISABLE_PORT &= ~STEPPER_DISABLE_MASK;
}

static void move(uint8_t bits, int steps) {
    for (int i = 0; i < steps; ++i) {
        STEPPER_PORT |= bits;
        _delay_us(STEPPER_STEP_PULSE);
        STEPPER_PORT &= ~bits;
        _delay_us(STEPPER_STEP_DELAY);
    }
}

// Line drawing algorithm where the pen can move in eight directions: horizontal,
// vertical and diagonal.
// The axis system is first reflected such that the line always has an angle of
// 45 degrees or less by taking the axis with the largest difference between the
// start and end points. The line is then drawn iteratively by taking the move
// which ends the pen closest to the target line.
// Because the line is 45 degrees or less, this can be done by comparing a horizontal
// move to a diagonal move. The closest one is selected by calculating the distance to the
// line, where the common terms between the distance of the horizontal move and diagonal
// move are eliminated.
enum status stepper_line_to(uint16_t x, uint16_t y) {
    if (x >= STEPPER_RANGE_X || y >= STEPPER_RANGE_Y) {
        return STATUS_OK;
    }

    int32_t delta_x, delta_y;

    if (stepper_state.x < x) {
        STEPPER_PORT &= ~STEPPER_DIR_X_MASK;
        delta_x = x - stepper_state.x;
    } else {
        STEPPER_PORT |= STEPPER_DIR_X_MASK;
        delta_x = stepper_state.x - x;
    }

    if (stepper_state.y < y) {
        STEPPER_PORT &= ~STEPPER_DIR_Y_MASK;
        delta_y = y - stepper_state.y;
    } else {
        STEPPER_PORT |= STEPPER_DIR_Y_MASK;
        delta_y = stepper_state.y - y;
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
            move(STEPPER_STEP_Y_MASK | (STEPPER_STEP_X_MASK * k), STEPPER_MICROSTEPS_PER_CELL);
        } else {
            move(STEPPER_STEP_X_MASK | (STEPPER_STEP_Y_MASK * k), STEPPER_MICROSTEPS_PER_CELL);
        }
    }

    stepper_state.x = x;
    stepper_state.y = y;

    return STATUS_ERR_BOUNDS;
}
