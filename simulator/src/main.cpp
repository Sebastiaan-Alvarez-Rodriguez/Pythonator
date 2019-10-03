#include <iostream>
#include <memory>
#include <string_view>
#include <stdexcept>
#include <thread>
#include <cmath>
#include <GLFW/glfw3.h>
#include <glad/glad.h>
#include "Utility.h"
#include "Simulator.h"
#include "Renderer.h"
#include "Pythonator.h"

using GlfwWindowPtr = std::unique_ptr<GLFWwindow, Deleter<&glfwDestroyWindow>>;

void run(GLFWwindow* window, bool draw_moves) {
    glClearColor(.8f, .8f, .8f, 1.0f);
    glLineWidth(1.0f);

    auto ren = Renderer();
    auto sim = Simulator();
    std::thread sim_thread(&Simulator::interpret_loop, &sim);

    {
        int width, height;
        glfwGetWindowSize(window, &width, &height);

        ren.resize({
            static_cast<size_t>(width),
            static_cast<size_t>(height)
        });
    }

    glfwSetWindowUserPointer(window, static_cast<void*>(&ren));

    glfwSetFramebufferSizeCallback(window, [](GLFWwindow* window, int width, int height) {
        auto* ren = static_cast<Renderer*>(glfwGetWindowUserPointer(window));

        glViewport(0, 0, width, height);

        ren->resize({
            static_cast<size_t>(width),
            static_cast<size_t>(height)
        });
    });

    while (!glfwWindowShouldClose(window)) {
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT);

        ren.draw();

        sim.process_lines([&ren](const auto* lines, auto n) {
            ren.add_lines(lines, n);
        });

        if (draw_moves) {
            sim.process_moves([&ren](const auto* lines, auto n) {
                ren.add_moves(lines, n);
            });
        }

        glfwSwapBuffers(window);
    }

    glfwSetWindowUserPointer(window, nullptr);
    glfwSetFramebufferSizeCallback(window, nullptr);

    sim.quit();
    sim_thread.join();
}

int main(int argc, char* argv[]) {
    if (glfwInit() != GLFW_TRUE) {
        std::cerr << "Failed to initialize GLFW" << std::endl;
    }

    bool draw_moves = false;
    for (int i = 1; i < argc; ++i) {
        auto arg = std::string_view(argv[i]);
        if (arg == "-m" || arg == "--draw-moves") {
            draw_moves = true;
        }
    }

    struct GlfwTerminator {
        ~GlfwTerminator() {
            glfwTerminate();
        }
    } _glfw_terminator;

    glfwWindowHint(GLFW_SAMPLES, 8);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    auto window = GlfwWindowPtr(glfwCreateWindow(800, 600, "Pythonator Simulator", nullptr, nullptr));
    glfwMakeContextCurrent(window.get());

    gladLoadGLLoader(reinterpret_cast<GLADloadproc>(glfwGetProcAddress));
    glfwSwapInterval(1);

    run(window.get(), draw_moves);

    return 0;
}
