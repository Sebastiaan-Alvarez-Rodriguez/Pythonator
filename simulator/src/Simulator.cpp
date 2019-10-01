#include "Simulator.h"
#include <sys/types.h>
#include <sys/time.h>
#include <unistd.h>

Simulator::Simulator():
    position({0, 0}), running(false),
    steppers_activated(false),
    pen_down(false) {
}

void Simulator::interpret_loop() {
    this->running = true;

    while (this->running) {
        int command_byte = this->read_byte();
        if (command_byte == -1) {
            goto end_loop;
        }

        auto status = pythonator::Status::OK;

        switch (static_cast<pythonator::Command>(command_byte)) {
        case pythonator::Command::START:
            this->steppers_activated = true;
            this->pen_down = false;
            this->position = Vec2Sz(0);
            break;
        case pythonator::Command::END:
            this->steppers_activated = false;
            this->position = Vec2Sz(0);
            this->pen_down = false;
            break;
        case pythonator::Command::PEN_DOWN:
            this->pen_down = true;
            break;
        case pythonator::Command::PEN_UP:
            this->pen_down = false;
            break;
        case pythonator::Command::ORIG:
            this->pen_down = false;
            this->position = Vec2Sz(0);
            break;
        case pythonator::Command::LINE:
            {
                auto x = read_u16le();
                if (x == -1) {
                    goto end_loop;
                }
                auto y = read_u16le();
                if (y == -1) {
                    goto end_loop;
                }

                status = this->line_to({
                    static_cast<uint16_t>(x),
                    static_cast<uint16_t>(y)
                });
            }
            break;
        default:
            status = pythonator::Status::ERR_UNKNOWN_CMD;
        }

        putchar(static_cast<char>(status));
    }

end_loop:
    this->running = false;
}

int Simulator::read_byte() {
    fd_set rfds;

    FD_ZERO(&rfds);
    FD_SET(0, &rfds);

    struct timeval tv = {0, 1000};

    while (true) {
        int rv = select(1, &rfds, nullptr, nullptr, &tv);
        if (rv == -1) {
            return -1;
        } else if (rv) {
            return getchar();
        } else if (!this->running) {
            return -1;
        }
    }
}

int Simulator::read_u16le() {
    int low = this->read_byte();
    if (low == -1) {
        return -1;
    }

    int high = this->read_byte();
    if (high == -1) {
        return -1;
    }

    return (high << 8) | low;
}

pythonator::Status Simulator::line_to(const Vec2Sz& dst) {
    if (dst.x >= pythonator::limits::range.x || dst.y >= pythonator::limits::range.y) {
        return pythonator::Status::ERR_BOUNDS;
    }

    auto lock = std::lock_guard(this->line_queue_mutex);

    this->line_queue.push_back({this->position, dst});
    this->position = dst;

    return pythonator::Status::OK;
}
