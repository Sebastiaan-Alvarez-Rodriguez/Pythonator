#ifndef _SIMULATOR_RENDERER_H
#define _SIMULATOR_RENDERER_H

#include "Pythonator.h"
#include "math/Vec.h"
#include "graphics/VertexArray.h"
#include "graphics/Shader.h"
#include "graphics/DynamicBuffer.h"

class Renderer {
    ShaderProgram program;
    VertexArray vao;
    DynamicBuffer background_buffer;
    DynamicBuffer pen_buffer;
    DynamicBuffer move_buffer;

public:
    struct Line {
        Vec2F start, end;
    };

    Renderer();
    void resize(Vec2Sz dim);
    void draw();
    void add_lines(const Line* line, size_t n);
    void add_moves(const Line* line, size_t n);
};

#endif
