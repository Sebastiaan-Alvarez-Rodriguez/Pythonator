#ifndef _UTILS_QUEUELOCK_HPP_
#define _UTILS_QUEUELOCK_HPP_

#include <condition_variable>
#include <mutex>
#include <thread>
#include <queue>

class QueueLock {
    private:
        std::mutex mutex;
        std::queue<std::thread::id> thread_queue;
        std::condition_variable cv;
    public:
        QueueLock();

        void lock();
        void unlock();
};

#endif
