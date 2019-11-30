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
    public:
        ImageProcessor(uint8_t*, size_t);
        ~ImageProcessor();

        void transform();

        inline const std::vector<std::pair<Coord, Coord>>& getData() const {
            return this->result_data;
        }
};

#endif // _IMAGE_PROCESSOR_HPP_
