#ifndef _SIMULATOR_SIMULATOR_H
#define _SIMULATOR_SIMULATOR_H

#include <vector>
#include <mutex>
#include <atomic>
#include "math/Vec.h"
#include "Pythonator.h"
#include "Renderer.h"

class Simulator {
    std::vector<Renderer::Line> line_queue;
    std::mutex line_queue_mutex;
    Vec2Sz position;
    std::atomic_bool running;
    bool steppers_activated;
    bool pen_down;

public:
    Simulator();

    void interpret_loop();

    void quit() {
        this->running = false;
    }

    template <typename F>
    void process_lines(F f) {
        if (this->line_queue.size() == 0) {
            return;
        }

        auto lock = std::lock_guard(this->line_queue_mutex);

        f(static_cast<const Renderer::Line*>(this->line_queue.data()), this->line_queue.size());
        this->line_queue.clear();
    }

private:
    int read_byte();
    int read_u16le();
    pythonator::Status line_to(const Vec2Sz& dst);
};

#endif
