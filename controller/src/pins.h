#ifndef _PYTHONATOR_PINS_H
#define _PYTHONATOR_PINS_H

#include <avr/io.h>

#define STEPPER_DISABLE_DDR DDRB
#define STEPPER_DISABLE_PORT PORTB
#define STEPPER_DISABLE_BIT (1 << PB0)

#define BUILTIN_LED_DDR DDRB
#define BUILTIN_LED_PORT PORTB
#define BUILTIN_LED_BIT (1 << PB5)

#define STEPPER_DDR DDRD
#define STEPPER_PORT PORTD
#define STEP_X_BIT (1 << PD2)
#define STEP_Y_BIT (1 << PD3)
#define STEP_Z_BIT (1 << PD4)

#define DIR_X_BIT (1 << PD5)
#define DIR_Y_BIT (1 << PD6)
#define DIR_Z_BIT (1 << PD7)

#endif
