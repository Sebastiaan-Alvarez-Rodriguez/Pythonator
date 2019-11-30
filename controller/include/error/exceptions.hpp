#ifndef _ERROR_EXCEPTIONS_HPP_
#define _ERROR_EXCEPTIONS_HPP_

#include <stdexcept>

#include "utils/strings.hpp"

class ProgramException : public std::runtime_error
{
    public:
        template <typename... T>
        ProgramException(const T&... args) : std::runtime_error(make_str(args...)) {}
        virtual ~ProgramException() = default;
};

class BluetoothException : public ProgramException {
    public:
        template <typename... T>
        BluetoothException(const T&... args) : ProgramException(args...) {}
        virtual ~BluetoothException() = default;
};

class USBException : public ProgramException {
    public:
        template <typename... T>
        USBException(const T&... args) : ProgramException(args...) {}
        virtual ~USBException() = default;
};

#endif // _ERROR_EXCEPTIONS_HPP_
