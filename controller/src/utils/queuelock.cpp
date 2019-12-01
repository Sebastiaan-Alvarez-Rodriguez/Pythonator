#include "utils/queuelock.hpp"

#include <mutex>

QueueLock::QueueLock() {}

void QueueLock::lock() {
    std::unique_lock<std::mutex> lck(this->mutex);

    auto id = std::this_thread::get_id();

    this->thread_queue.push(id);

    this->cv.wait(lck, [&]() {
        return this->thread_queue.front() == id;
    });
}

void QueueLock::unlock() {
    std::unique_lock<std::mutex> lck(this->mutex);
    this->thread_queue.pop();
    lck.unlock();
    this->cv.notify_all();
}
