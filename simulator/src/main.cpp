#include <iostream>
#include <memory>
#include <string_view>
#include <stdexcept>
#include <GLFW/glfw3.h>
#include <glad/glad.h>
#include "Utility.h"
#include "Simulator.h"
#include "Pythonator.h"

using GlfwWindowPtr = std::unique_ptr<GLFWwindow, Deleter<&glfwDestroyWindow>>;

void run(GLFWwindow* window) {
    glClearColor(.5f, .5f, .5f, 1.0f);
    glLineWidth(2.0f);

    auto sim = Simulator();
    sim.line_to({0, pythonator::limits::range.y - 1});
    sim.line_to({pythonator::limits::range.x - 1, pythonator::limits::range.y - 1});
    sim.line_to({pythonator::limits::range.x - 1, 0});
    sim.line_to({0, 0});

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
        glClear(GL_COLOR_BUFFER_BIT);

        sim.draw();

        glfwSwapBuffers(window);
        glfwPollEvents();
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
