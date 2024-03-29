#ifndef _PYTHONATOR_STEPPER_H
#define _PYTHONATOR_STEPPER_H

#include <stdint.h>
#include "status.h"

// Stepper limits
// The dimensions of the range the stepper pen can achieve, in full steps
#define STEPPER_RANGE_X_STEPS 1450
#define STEPPER_RANGE_Y_STEPS 1500

// The number of micro steps the stepper motor drivers are configured to. This is the
// same for the x and y axis.
#define STEPPER_MICROSTEPS 16

// The number of cells per full step.
#define STEPPER_CELLS_PER_STEP 2

// The number of microsteps needed to traverse a cell.
#define STEPPER_MICROSTEPS_PER_CELL STEPPER_MICROSTEPS / STEPPER_CELLS_PER_STEP

// The range in cells of the x and y axis
#define STEPPER_RANGE_X STEPPER_RANGE_X_STEPS * STEPPER_CELLS_PER_STEP
#define STEPPER_RANGE_Y STEPPER_RANGE_Y_STEPS * STEPPER_CELLS_PER_STEP

// Stepper timings
// The delay of a step pulse to one of the drivers, in us.
#define STEPPER_STEP_PULSE 10

// The delay to wait after a step pulse to make sure the motors finish
// their step, in us.
#define STEPPER_STEP_DELAY 190

// Initialize the stepper driver pins and set the steppers to idle.
void stepper_init();

// Disable the stepper drivers to make the steppers go idle.
void stepper_disable();

// Enable the stepper drivers to wake the steppers up again.
void stepper_enable();

// Move the pen in a straight line to (x, y). The destination coordinate
// should first be validated with `stepper_validate_line`.
// This function blocks until the pen is at the destination.
void stepper_line_to(uint16_t x, uint16_t y);

// Check if the pen can be moved in a straight line to (x, y). Returns:
// - STATUS_OK if no error occured.
// - STATUS_ERR_BOUNDS if (x, y) would be out of reachable range of the pen.
//   The pen location remains unchanged.
enum status stepper_validate_line(uint16_t x, uint16_t y);

#endif
