#include "error/exceptions.hpp"

ConfigException::ConfigException(size_t line, const char* str) : ProgramException("At line ", line, ": ", str) {}
ConfigException::ConfigException(size_t line, const std::string& str) : ProgramException("At line ", line, ": ", str) {}
