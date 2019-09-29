#ifndef _SIMULATOR_COMMAND_H
#define _SIMULATOR_COMMAND_H

#include "math/Vec.h"

namespace pythonator {
    // See doc/serial_protocol.md
    enum class Command {
        START = 0x00,
        END = 0x01,
        PEN_DOWN = 0x02,
        PEN_UP = 0x03,
        ORIG = 0x04,
        LINE = 0x05
    };

    // See doc/serial_protocol.md
    enum class Status {
        OK = 0x00,
        ERR_BOUNDS = 0x01,
        ERR_UNKNOWN_CMD = 0x02
    };

    namespace limits {
        constexpr const Vec2Sz range = {1500 * 2, 1000 * 2};
    }
}

#endif
