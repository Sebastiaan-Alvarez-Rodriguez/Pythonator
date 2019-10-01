#include "graphics/Shader.h"
#include <stdexcept>
#include <cstddef>
#include <iostream>

namespace {
    struct Shader {
        GLuint shader;

        Shader(GLenum type, std::string_view src);
        ~Shader();
    };

    Shader::Shader(GLenum type, std::string_view src):
        shader(glCreateShader(type)) {

        auto data = src.data();
        auto size = static_cast<GLint>(src.size());

        glShaderSource(this->shader, 1, &data, &size);
        glCompileShader(this->shader);

        GLint compile_status;
        glGetShaderiv(this->shader, GL_COMPILE_STATUS, &compile_status);
        if (compile_status != GL_TRUE) {
            GLint length;
            glGetShaderiv(this->shader, GL_INFO_LOG_LENGTH, &length);
            auto info = std::string("Failed to compile shader: ");
            auto offset = info.size();
            info.append(static_cast<size_t>(length), 0);
            glGetShaderInfoLog(this->shader, length, nullptr, info.data() + offset);
            throw std::runtime_error(info);
        }
    }

    Shader::~Shader() {
        glDeleteShader(this->shader);
    }
}

ShaderProgram::ShaderProgram(std::string_view vertex_src, std::string_view fragment_src) {
    auto vertex = Shader(GL_VERTEX_SHADER, vertex_src);
    auto fragment = Shader(GL_FRAGMENT_SHADER, fragment_src);

    this->program = glCreateProgram();

    glAttachShader(this->program, vertex.shader);
    glAttachShader(this->program, fragment.shader);
    glLinkProgram(this->program);

    GLint link_status;
    glGetProgramiv(this->program, GL_LINK_STATUS, &link_status);
    if (link_status != GL_TRUE) {
        GLint length;
        glGetProgramiv(this->program, GL_INFO_LOG_LENGTH, &length);
        auto info = std::string("Failed to link shader program: ");
        auto offset = info.size();
        std::cout << length << std::endl;
        info.append(static_cast<size_t>(length), 0);
        glGetProgramInfoLog(this->program, length, nullptr, info.data() + offset);
        throw std::runtime_error(info);
    }
}
