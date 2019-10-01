#ifndef _SIMULATOR_UTILITY_H
#define _SIMULATOR_UTILITY_H

template <auto F>
struct Deleter {
    template <typename P>
    void operator()(P* p) {
        F(p);
    }
};

#endif
