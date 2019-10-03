#include "Renderer.h"
#include <algorithm>
#include <iostream>
#include "math/Mat.h"

constexpr const std::string_view VERTEX_SRC = R"VERT(
    #version 330
    #extension GL_ARB_explicit_uniform_location: require

    layout(location = 0) in vec2 a_vertex;

    layout(location = 1) uniform mat4 u_mvp;

    void main() {
        gl_Position = u_mvp * vec4(a_vertex, 1, 1);
    }
)VERT";

constexpr const std::string_view FRAGMENT_SRC = R"FRAG(
    #version 330
    #extension GL_ARB_explicit_uniform_location: require

    out vec4 f_color;

    layout(location = 2) uniform vec3 u_color;

    void main() {
        f_color = vec4(u_color, 1);
    }
)FRAG";

constexpr const Attribute A_VERTEX = 0;
constexpr const Uniform U_MVP = 1;
constexpr const Uniform U_COLOR = 2;

Renderer::Renderer():
    program(VERTEX_SRC, FRAGMENT_SRC) {
    this->program.use();
    glBindVertexArray(vao);
    glEnableVertexAttribArray(A_VERTEX);

    this->background_buffer.append(GL_ARRAY_BUFFER, {
        Vec2F{0, 0},
        Vec2F{0, pythonator::limits::range.y},
        Vec2F{pythonator::limits::range},
        Vec2F{pythonator::limits::range.x, 0},
    });
}

void Renderer::resize(Vec2Sz dim) {
    constexpr const auto border = 10.f;
    auto range = pythonator::limits::range;
    auto range_aspect = static_cast<float>(range.x) / static_cast<float>(range.y);
    auto aspect = static_cast<float>(dim.x) / static_cast<float>(dim.y);

    Mat4F perspective;

    if (aspect < range_aspect) {
        auto height = range.y * range_aspect / aspect;
        auto offset = (height - range.y) / 2;
        auto vborder = border * range.x / dim.x;
        perspective = Mat4F::orthographic(-vborder, range.x + vborder, height - offset + vborder, -offset - vborder, -1, 1);
    } else {
        auto width = range.x / range_aspect * aspect;
        auto offset = (width - range.x) / 2;
        auto vborder = border * range.y / dim.y;
        perspective = Mat4F::orthographic(-offset - vborder, width - offset + vborder, range.y + vborder, -vborder, -1, 1);
    }

    glUniformMatrix4fv(U_MVP, 1, false, perspective.data());
}

void Renderer::draw() {
    glUniform3f(U_COLOR, 1, 1, 1);
    glBindBuffer(GL_ARRAY_BUFFER, this->background_buffer);
    glVertexAttribPointer(A_VERTEX, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glDrawArrays(GL_TRIANGLE_FAN, 0, static_cast<GLsizei>(this->background_buffer.size() / sizeof(Vec2F)));

    glUniform3f(U_COLOR, .7f, .7f, .7f);
    glBindBuffer(GL_ARRAY_BUFFER, this->move_buffer);
    glVertexAttribPointer(A_VERTEX, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glDrawArrays(GL_LINES, 0, static_cast<GLsizei>(this->move_buffer.size() / sizeof(Vec2F)));

    glUniform3f(U_COLOR, 0, 0, 1);
    glBindBuffer(GL_ARRAY_BUFFER, this->pen_buffer);
    glVertexAttribPointer(A_VERTEX, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glDrawArrays(GL_LINES, 0, static_cast<GLsizei>(this->pen_buffer.size() / sizeof(Vec2F)));
}

void Renderer::add_lines(const Line* lines, size_t n) {
    this->pen_buffer.append(GL_ARRAY_BUFFER, lines, n);
}

void Renderer::add_moves(const Line* lines, size_t n) {
    this->move_buffer.append(GL_ARRAY_BUFFER, lines, n);
}
