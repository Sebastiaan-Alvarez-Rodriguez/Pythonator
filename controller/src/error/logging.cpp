#include "error/logging.hpp"

#include <cstdio>
#include <cstdarg>

const char* terminal_formats[] = {
    "", //Default
    "\033[33m", //Warning
    "\033[31m"
};

const char* error_msgs[] = {
    "Info",
    "Warning",
    "Error"
};

void log_data(LogStatus status, const char* fmt, ...) {
    fprintf(stderr, "%s%s: ", terminal_formats[(size_t)status], error_msgs[(size_t)status]);

    va_list args;
    va_start(args, fmt);
    std::vfprintf(stderr, fmt, args);
    va_end(args);

    fprintf(stderr, "\033[0m\n");
}
