# Serial protocol

The Pythonator is controlled by a host computer via a simple binary serial command protocol. Each command consists of a command type sent as byte and an optional list of parameters. Parameters must be sent as little endian. When the Pythonator finishes to execute a command, it sends a status code byte.

## Status codes
- `0x00` OK: No error
- `0x01` Out of bounds: The operation would result in the Pythonator's pen going out of the reachable range.
- `0x02` Unknown command: Command was not recognized.

## Commands
- `0x00` START: Indicate the start of a command stream. This enables the stepper drivers and retracts the pen.
- `0x01` END: Indicate the end of a command stream. The pen is retracted and returns to the origin, after which the stepper drivers are disabled.
- `0x02` PEN_DOWN: Release the pen solenoid to start drawing.
- `0x03` PEN_UP: Retract the pen solenoid to stop drawing.
- `0x04` ORIG: Retract the pen and return to the origin ((0, 0)). The pen may take any path to do so.
- `0x05` LINE: Draw a line from the current location to the target coordinates. Parameters:
    - Target x-coordinate in cells (u16).
    - Target y-coordinate in cells (u16).

    Returns:
    - `OK` if no error occured. The pen is at (x, y).
    - `Out of bounds` if (x, y) would be out of the reachable range. The pen's location remains unchanged.
If any other command code was sent, `Unknown command` will be returned.
