#ifndef _PYTHONATOR_PEN_H
#define _PYTHONATOR_PEN_H

// The time to wait until the pen is moved from up to down or vice versa, in ms.
#define PEN_ACTUATE_DELAY 400

// Enum to indicate the pen's state: either down (drawing) or up (not drawing).
enum pen_state {
    PEN_UP,
    PEN_DOWN
};

// Initialize the pen's pin.
void pen_init();

// Set the pen's state.
void pen_set(enum pen_state state);

#endif
