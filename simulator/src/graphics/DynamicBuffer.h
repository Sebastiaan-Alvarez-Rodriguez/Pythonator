#ifndef _SIMULATOR_GRAPHICS_GPUVECTOR_H
#define _SIMULATOR_GRAPHICS_GPUVECTOR_H

#include <glad/glad.h>
#include <algorithm>
#include <cstddef>

class DynamicBuffer {
    size_t length;
    size_t capacity;
    GLuint buffer;

public:
    DynamicBuffer():
        length(0),
        capacity(0) {
        glGenBuffers(1, &this->buffer);
    }

    ~DynamicBuffer() {
        glDeleteBuffers(1, &this->buffer);
    }

    operator GLuint() const {
        return this->buffer;
    }

    template <typename T>
    void append(GLenum target, const T* data, size_t length) {
        this->append_bytes(target, reinterpret_cast<const std::byte*>(data), length * sizeof(T));
    }

    void append_bytes(GLenum target, const std::byte* data, size_t length) {
        this->ensure_capacity(this->length + length);

        glBindBuffer(target, this->buffer);
        glBufferSubData(target, static_cast<GLsizeiptr>(this->length), static_cast<GLsizeiptr>(length), data);

        this->length += length;
    }

    void ensure_capacity(size_t capacity) {
        if (capacity <= this->capacity) {
            return;
        }

        while (capacity > this->capacity) {
            this->capacity = std::max(this->capacity * 2, size_t{128});
        }

        if (this->size() == 0) {
            glBindBuffer(GL_ARRAY_BUFFER, this->buffer);
            glBufferData(GL_ARRAY_BUFFER, static_cast<GLsizeiptr>(this->capacity), nullptr, GL_DYNAMIC_DRAW);
            return;
        }

        GLuint new_buffer;
        glGenBuffers(1, &new_buffer);
        glBindBuffer(GL_COPY_WRITE_BUFFER, new_buffer);
        glBufferData(GL_COPY_WRITE_BUFFER, static_cast<GLsizeiptr>(this->capacity), nullptr, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_COPY_READ_BUFFER, this->buffer);

        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, static_cast<GLsizeiptr>(this->size()));

        glDeleteBuffers(1, &this->buffer);

        this->buffer = new_buffer;
    }

    size_t size() const {
        return this->length;
    }
};

#endif
