#ifndef _SIMULATOR_SIMULATOR_H
#define _SIMULATOR_SIMULATOR_H

#include "Pythonator.h"
#include "math/Vec.h"
#include "graphics/VertexArray.h"
#include "graphics/Shader.h"
#include "graphics/DynamicBuffer.h"

class Simulator {
    ShaderProgram program;
    VertexArray vao;
    DynamicBuffer pen_buffer;
    Vec2Sz pen_position;

public:
    Simulator();
    void resize(Vec2Sz dim);
    void draw();
    pythonator::Status line_to(Vec2Sz destination);
    pythonator::Status move_pen(Vec2Sz destination);
};

#endif
