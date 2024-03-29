#ifndef _PYTHONATOR_COMMAND_H
#define _PYTHONATOR_COMMAND_H

// Types of commands that the Pythonator accepts.
// All parameters are in little endian.
// Commands are received over serial as bytes, and a status (see status.h)
// code is returned as soon as the command is finished.
// If no parameters codes are specified near a command, it takes none.
// If no error status is specified, it can only return STATUS_OK.
// See doc/serial_protocol.md
enum command {
    // Indicate the start of a command stream.
    // Enabled the stepper drivers and retracts the pen.
    CMD_START = 0x00,

    // Indicate the end of a command stream.
    // Retracts and returns the pen to the origin and disables the stepper drivers.
    CMD_END = 0x01,

    // Release the pen and start drawing.
    CMD_PEN_DOWN = 0x02,

    // Retract the pen and stop drawing.
    CMD_PEN_UP = 0x03,

    // Retract the pen and return to the origin. The bot may take any path.
    // No parameters.
    CMD_ORIG = 0x04,

    // Move in a straight line to the target coordinates. Parameters:
    // - x-coordinate in cells (u16).
    // - y-coordinate in cells (u16).
    // Returns:
    // - STATUS_OK if no error occured and the pen is at (x, y).
    // - STATUS_ERR_BOUNDS if (x, y) would be out of reachable range of the pen.
    //   The pen location remains unchanged.
    CMD_LINE = 0x05
};

#endif
