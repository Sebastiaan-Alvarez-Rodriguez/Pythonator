#ifndef _IMAGE_PROCESSOR_HPP_
#define _IMAGE_PROCESSOR_HPP_

#include <cstdint>
#include <vector>
#include <memory>

#include <opencv2/opencv.hpp>

#include "bot/command.hpp"
#include "image/coordinate.hpp"

class ImageProcessor {
    private:
        cv::Mat image;
        std::vector<std::pair<Coord, Coord>> result_data;

        cv::Mat edgeDetect(double, int, int, int) const;

        size_t findNextEdge(Coord, bool*, size_t);
    public:
        ImageProcessor(uint8_t*, size_t);
        ~ImageProcessor();

        void transform(size_t, size_t, size_t);
        void optimize();

        inline const std::vector<std::pair<Coord, Coord>>& getData() const {
            return this->result_data;
        }
};

#endif // _IMAGE_PROCESSOR_HPP_
