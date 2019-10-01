#ifndef _SIMULATOR_GRAPHICS_SHADER_H
#define _SIMULATOR_GRAPHICS_SHADER_H

#include <string>
#include <string_view>
#include <glad/glad.h>

using Uniform = GLint;
using Attribute = GLuint;

class ShaderProgram {
    GLuint program;

public:
    ShaderProgram(std::string_view vertex_src, std::string_view fragment_src);

    ~ShaderProgram() {
        glDeleteProgram(this->program);
    }

    void use() const {
        glUseProgram(this->program);
    }

    Uniform uniform(const std::string& name) const {
        return glGetUniformLocation(*this, name.c_str());
    }

    Attribute attribute(const std::string& name) const {
        return static_cast<Attribute>(glGetAttribLocation(*this, name.c_str()));
    }

    operator GLuint() const {
        return this->program;
    }
};

#endif
