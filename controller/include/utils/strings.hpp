#ifndef _UTILS_STRINGS_HPP_
#define _UTILS_STRINGS_HPP_

#include <string>
#include <sstream>

template <typename... Args>
std::string make_str(const Args&... args) {
    std::stringstream ss;
    (ss << ... << args);
    return ss.str();
}

#endif // _UTILS_STRINGS_HPP_
