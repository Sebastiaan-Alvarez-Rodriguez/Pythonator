#ifndef _ERROR_LOGGING_HPP_
#define _ERROR_LOGGING_HPP_

enum class LogStatus {
    INFO,
    WARNING,
    ERROR
};

void log_data(LogStatus, const char*, ...);

#define log_info(...) log_data(LogStatus::INFO, __VA_ARGS__)
#define log_warning(...) log_data(LogStatus::WARNING, __VA_ARGS__)
#define log_error(...) log_data(LogStatus::ERROR, __VA_ARGS__)

#endif // _ERROR_LOGGING_HPP_
