#ifndef _SIMULATOR_GRAPHICS_VERTEXARRAY_H
#define _SIMULATOR_GRAPHICS_VERTEXARRAY_H

#include <glad/glad.h>

class VertexArray {
    GLuint vao;

public:
    VertexArray() {
        glGenVertexArrays(1, &this->vao);
    }

    ~VertexArray() {
        glDeleteVertexArrays(1, &this->vao);
    }

    operator GLuint() const {
        return this->vao;
    }
};

#endif
