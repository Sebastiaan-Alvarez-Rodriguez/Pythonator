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

class ConfigException : public ProgramException
{
    public:
        template <typename... T>
        ConfigException(const T&... args) : ProgramException(args...) {}
        ConfigException(size_t, const char*);
        ConfigException(size_t, const std::string&);
        virtual ~ConfigException() = default;
};

class IllegalConfigException : public ConfigException
{
    public:
        template <typename... T>
        IllegalConfigException(const T&... args) : ConfigException(args...) {}
        virtual ~IllegalConfigException() = default;
};

class NoConfigFoundException : public ConfigException
{
    public:
        template <typename... T>
        NoConfigFoundException(const T&... args) : ConfigException(args...) {}
        virtual ~NoConfigFoundException() = default;
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
