#include "pen.h"
#include <stdbool.h>
#include "pinout.h"

void pen_init() {
    PEN_DDR |= PEN_MASK;
    PEN_PORT |= PEN_MASK;
}

void pen_set(enum pen_state state) {
    if (state == PEN_DOWN) {
        PEN_PORT &= ~PEN_MASK;
    } else {
        PEN_PORT |= PEN_MASK;
    }
}