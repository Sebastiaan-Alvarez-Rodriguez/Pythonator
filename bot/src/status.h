#ifndef _PYTHONATOR_STATUS_H
#define _PYTHONATOR_STATUS_H

// Status codes that can be returned by various functions.
// See doc/serial_protocol.md
enum status {
    // No error
    STATUS_OK = 0x00,

    // Out of bounds
    STATUS_ERR_BOUNDS = 0x01,

    // Unrecognized command
    STATUS_ERR_UNKNOWN_CMD = 0x02
};

#endif
