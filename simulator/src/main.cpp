#include <iostream>
#include <memory>
#include <string_view>
#include <stdexcept>
#include <cmath>
#include <GLFW/glfw3.h>
#include <glad/glad.h>
#include "Utility.h"
#include "Simulator.h"
#include "Pythonator.h"

using GlfwWindowPtr = std::unique_ptr<GLFWwindow, Deleter<&glfwDestroyWindow>>;

void run(GLFWwindow* window) {
    glClearColor(.8f, .8f, .8f, 1.0f);
    glLineWidth(1.0f);

    auto sim = Simulator();

    const auto n = 1000;
    const auto a = 3.14159265f * 2 / n;
    const auto r = 1000;
    const auto mid = pythonator::limits::range / 2.f;

    sim.move_to(mid + Vec2F{r, 0});

    for (auto i = 0; i <= n; ++i) {
        float t = i * a;
        auto pos = Vec2F(std::cos(t * 7), std::sin(t * 6)) * r + mid;
        sim.line_to(static_cast<Vec2Sz>(pos));
    }

    {
        int width, height;
        glfwGetWindowSize(window, &width, &height);

        sim.resize({
            static_cast<size_t>(width),
            static_cast<size_t>(height)
        });
    }

    glfwSetWindowUserPointer(window, static_cast<void*>(&sim));

    glfwSetFramebufferSizeCallback(window, [](GLFWwindow* window, int width, int height) {
        auto* sim = static_cast<Simulator*>(glfwGetWindowUserPointer(window));

        glViewport(0, 0, width, height);

        sim->resize({
            static_cast<size_t>(width),
            static_cast<size_t>(height)
        });
    });

    while (!glfwWindowShouldClose(window)) {
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT);

        sim.draw();

        glfwSwapBuffers(window);
    }

    glfwSetWindowUserPointer(window, nullptr);
    glfwSetFramebufferSizeCallback(window, nullptr);
}

int main(int argc, char* argv[]) {
    if (glfwInit() != GLFW_TRUE) {
        std::cerr << "Failed to initialize GLFW" << std::endl;
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

    run(window.get());

    return 0;
}
