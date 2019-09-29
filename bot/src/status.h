#ifndef _PYTHONATOR_STATUS_H
#define _PYTHONATOR_STATUS_H

// Status codes that can be returned by various functions.
enum status {
    // No error
    STATUS_OK,

    // Out of bounds
    STATUS_ERR_BOUNDS,

    // Unrecognized command
    STATUS_ERR_UNKNOWN_CMD
};

#endif
