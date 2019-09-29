#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <avr/io.h>
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

    while (true) {
        enum command cmd = (enum command) serial_getchar();
        enum status status = STATUS_OK;

        switch (cmd) {
        case CMD_START:
            stepper_enable();
            pen_set(PEN_UP);
            break;
        case CMD_END:
            pen_set(PEN_UP);
            stepper_line_to(0, 0);
            stepper_disable();
            break;
        case CMD_PEN_DOWN:
            pen_set(PEN_DOWN);
            break;
        case CMD_PEN_UP:
            pen_set(PEN_UP);
            break;
        case CMD_ORIG:
            pen_set(PEN_UP);
            stepper_line_to(0, 0);
            break;
        case CMD_LINE:
            {
                uint16_t x = serial_get_u16le();
                uint16_t y = serial_get_u16le();
                status = stepper_line_to(x, y);
            }
            break;
        default:
            status = STATUS_ERR_UNKNOWN_CMD;
        }

        serial_putchar((uint8_t) status);

        LED_PORT |= LED_MASK;
        _delay_ms(100);
        LED_PORT &= ~LED_MASK;
    }

    return 0;
}