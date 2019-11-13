#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include "pinout.h"
#include "serial.h"
#include "stepper.h"
#include "pen.h"
#include "command.h"

int main() {
    LED_DDR |= LED_MASK;
    LED_PORT &= ~LED_MASK;

    stepper_init();
    pen_init();
    serial_init();

    sei();

    while (true) {
        enum command cmd = (enum command) serial_get_u8();
        switch (cmd) {
        case CMD_START:
            serial_put_u8((uint8_t) STATUS_OK);
            pen_set(PEN_UP);
            stepper_enable();
            break;
        case CMD_END:
            serial_put_u8((uint8_t) STATUS_OK);
            pen_set(PEN_UP);
            stepper_line_to(0, 0);
            stepper_disable();
            break;
        case CMD_PEN_DOWN:
            serial_put_u8((uint8_t) STATUS_OK);
            pen_set(PEN_DOWN);
            break;
        case CMD_PEN_UP:
            serial_put_u8((uint8_t) STATUS_OK);
            pen_set(PEN_UP);
            break;
        case CMD_ORIG:
            serial_put_u8((uint8_t) STATUS_OK);
            pen_set(PEN_UP);
            stepper_line_to(0, 0);
            break;
        case CMD_LINE:
            {
                uint16_t x = serial_get_u16le();
                uint16_t y = serial_get_u16le();
                enum status status = stepper_validate_line(x, y);
                serial_put_u8((uint8_t) status);

                if (status == STATUS_OK) {
                    stepper_line_to(x, y);
                }
            }
            break;
        default:
            serial_put_u8((uint8_t) STATUS_ERR_UNKNOWN_CMD);
        }
    }

    return 0;
}