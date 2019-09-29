#ifndef _PYTHONATOR_PINS_H
#define _PYTHONATOR_PINS_H

#include <avr/io.h>

// The DDR, port and mask controlling both the arduino's built-in led and the
// Pythonator's led, which is connected to the SpnDir pin on the CNC shield.
#define LED_DDR DDRB
#define LED_PORT PORTB
#define LED_MASK (1 << PB5)

// The DDR, port and masks controlling the Pythonator's stepper motors.
// Note that X and Y refer to the Pythonator's perspective of X and Y axes,
// while the Y stepper motor is connected to the Z-stepper on the CNC shield.
#define STEPPER_DDR DDRD
#define STEPPER_PORT PORTD
#define STEPPER_STEP_X_MASK (1 << PD2)
#define STEPPER_STEP_Y_MASK (1 << PD4)
#define STEPPER_DIR_X_MASK (1 << PD5)
#define STEPPER_DIR_Y_MASK (1 << PD7)

// The DDR, port and mask controlling whether (all) stepper driver chips are
// enabled. If the pin is high, the drivers are idle. In general the chips
// should be disabled if they are not in use, however any microsteps the stepper
// motors are at should be considered lost.
#define STEPPER_DISABLE_DDR DDRB
#define STEPPER_DISABLE_PORT PORTB
#define STEPPER_DISABLE_MASK (1 << PB0)

// The DDR, port and mask controlling the Pythonator's pen solenoid, which is
// connected to the SpnEn pin on the CNC shield.
#define PEN_DDR DDRB
#define PEN_PORT PORTB
#define PEN_MASK (1 << PB4)

#endif
