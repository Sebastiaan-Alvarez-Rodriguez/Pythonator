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
    std::vector<Renderer::Line> move_queue;

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
        this->process_line_vec(f, this->line_queue);
    }

    template <typename F>
    void process_moves(F f) {
        this->process_line_vec(f, this->move_queue);
    }
private:
    template <typename F>
    void process_line_vec(F f, std::vector<Renderer::Line>& vec) {
        if (vec.size() == 0) {
            return;
        }

        auto lock = std::lock_guard(this->line_queue_mutex);

        f(static_cast<const Renderer::Line*>(vec.data()), vec.size());
        vec.clear();
    }

    int read_byte();
    int read_u16le();
    pythonator::Status line_to(const Vec2Sz& dst);
};

#endif
