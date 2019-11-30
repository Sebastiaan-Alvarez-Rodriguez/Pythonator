#include "image/processor.hpp"


ImageProcessor::ImageProcessor(uint8_t* data, size_t data_size) {
    cv::Mat memory_buffer(1, data_size, CV_8UC1, data);
    this->image = cv::imdecode(memory_buffer, cv::IMREAD_COLOR);
}

ImageProcessor::~ImageProcessor() {
}

cv::Mat ImageProcessor::edgeDetect(double blur_sigma, int low, int high, int kernel_size) const {
    cv::Mat grayscale;
    cv::cvtColor(this->image, grayscale, cv::COLOR_BGR2GRAY);

    cv::Mat blurred;
    cv::GaussianBlur(grayscale, blurred, cv::Size(0, 0), blur_sigma);

    cv::Mat result;
    cv::Canny(blurred, result, low, high, kernel_size);
    return result;
}

void ImageProcessor::transform() {
    //Canny edge detection, TODO: figure out parameters
    cv::Mat edge_mapping = this->edgeDetect(3.5, 2000, 14000, 7);

    //Use OpenCV contour detection
    std::vector<std::vector<cv::Point>> contours;
    std::vector<cv::Vec4i> hierarchy;
    cv::findContours(edge_mapping, contours, hierarchy, cv::RETR_LIST, cv::CHAIN_APPROX_TC89_KCOS);

    //Convert output to points
    Coord prev_coord;
    for(auto& it : contours) {
        bool first = true;
        for(auto& it2 : it) {
            Coord current_coord;
            current_coord.x = it2.x;
            current_coord.y = it2.y;
            if(!first) {
                std::pair<Coord, Coord> line_data;
                line_data.first.x = prev_coord.x;
                line_data.first.y = prev_coord.y;

                line_data.second = current_coord;
                this->result_data.push_back(line_data);
            }
            else
                first = false;
            prev_coord = current_coord;
        }
    }
}
