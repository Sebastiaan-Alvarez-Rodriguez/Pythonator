#include "image/processor.hpp"
#include "error/logging.hpp"
#include "error/exceptions.hpp"

#include <limits>
#include <memory>

ImageProcessor::ImageProcessor(uint8_t* data, size_t data_size) {
    log_info("Decoding image of size %llu", (long long unsigned)data_size);
    cv::Mat memory_buffer(1, data_size, CV_8UC1, data);
    this->image = cv::imdecode(memory_buffer, cv::IMREAD_COLOR);
    if(this->image.data == nullptr) {
        log_error("Failed to decode image");
        throw ImageException("Failed to decode the given image");
    }
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

void ImageProcessor::transform(size_t target_width_orig, size_t target_height_orig, size_t scale_factor) {
    size_t original_width = this->image.size().width;
    size_t original_height = this->image.size().height;
    
    size_t target_width = target_width_orig / scale_factor;
    size_t target_height = target_height_orig / scale_factor;

    float scale_factor_width = (float)target_width / (float)original_width;
    float scale_factor_height = (float)target_height / (float)original_height;

    if(scale_factor_height * original_width > target_width)
        target_height = scale_factor_width * original_height;
    else
        target_width = scale_factor_height * original_width;

    log_info("Starting resize operation from size %llux%llu to size %llux%llu", (long long unsigned)original_width, (long long unsigned)original_height,
                                                                                (long long unsigned)target_width, (long long unsigned)target_height);
    cv::resize(this->image, this->image, cv::Size(target_width, target_height));

    //Canny edge detection
    log_info("Starting edge detection routine");
    cv::Mat edge_mapping = this->edgeDetect(3.5, 2000, 8000, 7);

    //Use OpenCV contour detection
    log_info("Starting contour detection");
    std::vector<std::vector<cv::Point>> contours;
    std::vector<cv::Vec4i> hierarchy;
    cv::findContours(edge_mapping, contours, hierarchy, cv::RETR_LIST, cv::CHAIN_APPROX_TC89_KCOS);

    //Convert output to points
    log_info("Converting contours to bot coordinates");
    Coord prev_coord;
    for(auto& it : contours) {
        bool first = true;
        for(auto& it2 : it) {
            Coord current_coord;
            current_coord.x = it2.x;
            current_coord.y = it2.y;
            if(!first) {
                std::pair<Coord, Coord> line_data;
                line_data.first.x = scale_factor * prev_coord.x;
                line_data.first.y = scale_factor * (target_height - prev_coord.y - 1);

                line_data.second.x = scale_factor * current_coord.x;
                line_data.second.y = scale_factor * (target_height - current_coord.y - 1);
                this->result_data.push_back(line_data);
            }
            else
                first = false;
            prev_coord = current_coord;
        }
    }

    log_info("Generated %llu edges", (long long unsigned)this->result_data.size());
}

size_t ImageProcessor::findNextEdge(Coord current, bool* access_list, size_t guess) {
    float min_dist = std::numeric_limits<double>::infinity();
    size_t best_offset = std::numeric_limits<size_t>::max();
    for(size_t i = 0; i < this->result_data.size(); ++i) {
        size_t offset = (i + guess) % this->result_data.size();

        if(!access_list[offset]) {
            Coord vertex_start = this->result_data[offset].first;
            Coord vertex_end = this->result_data[offset].second;

            //Perfect match on vertex 1
            if(vertex_start.x == current.x && vertex_start.y == current.y)
                return offset;
            //Perfect match on vertex 2
            if(vertex_end.x == current.x && vertex_end.y == current.y) {
                std::swap(this->result_data[offset].first, this->result_data[offset].second);
                return offset;
            }

            float dx = (float)vertex_start.x - (float)current.x;
            float dy = (float)vertex_start.y - (float)current.y;

            float dist = std::hypot(dx, dy);
            if(dist < min_dist) {
                min_dist = dist;
                best_offset = offset;
            }

            dx = (float)vertex_end.x - (float)current.x;
            dy = (float)vertex_end.y - (float)current.y;

            dist = std::hypot(dx, dy);
            if(dist < min_dist) {
                min_dist = dist;
                best_offset = offset;
                std::swap(this->result_data[offset].first, this->result_data[offset].second);
            }
        }
    }
    return best_offset;
}

void ImageProcessor::optimize() {
    std::vector<std::pair<Coord, Coord>> optimized_route;
    optimized_route.reserve(this->result_data.size());

    std::unique_ptr<bool[]> result_data_accessed(new bool[this->result_data.size()]);
    std::memset(result_data_accessed.get(), false, this->result_data.size());

    size_t last_coord = 0;
    Coord current_coord = {0,0};
    while(optimized_route.size() != result_data.size()) {
        size_t next_edge = this->findNextEdge(current_coord, result_data_accessed.get(), last_coord);
        optimized_route.push_back(this->result_data[next_edge]);
        result_data_accessed[next_edge] = true;
        current_coord = this->result_data[next_edge].second;
        last_coord = next_edge;
    }

    std::swap(this->result_data, optimized_route);
}
