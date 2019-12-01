#include "config/writer.hpp"
#include "error/exceptions.hpp"
#include "config/config.hpp"

ConfigWriter::ConfigWriter(const std::string& output) : output(std::ofstream(output))
{
    if(!this->output)
        throw NoConfigFoundException("Failed to open output configuration file");
}

ConfigWriter::~ConfigWriter() {}

void ConfigWriter::write(const Config& config)
{
    for(auto& it : config)
        this->output << it.first << ": " << it.second->fileFormat() << std::endl;
}
