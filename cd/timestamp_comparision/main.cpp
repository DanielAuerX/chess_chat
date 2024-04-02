#include <iostream>
#include <chrono>
#include <iomanip>
#include <regex>
#include "main.h"

int main(int argc, char *argv[])
{
    validateArguments(argc, argv);

    std::time_t timestamp1 = convertToTimeT(argv[1]);
    std::time_t timestamp2 = convertToTimeT(argv[2]);

    if (timestamp1 > timestamp2)
    {
        std::cout << "Argument 1 (" << argv[1] << ") is later than Argument 2 (" << argv[2] << ")" << std::endl;
        return 100;
    }

    return 0;
}
void validateArguments(int argc, char *argv[])
{
    if (argc != 3)
    {
        throw std::runtime_error("Usage: ./timestamp-comparison <timestamp1> <timestamp2>\n");
    }

    for (size_t i = 1; i < 3; i++)
    {
        if (!isValidTimestamp(argv[i]))
        {
            throw std::runtime_error("All arguments must be of format 'yyyy-mm-ddThh:mm:ssZ'");
        }
    }
}

std::time_t convertToTimeT(const std::string &timestamp)
{
    std::tm tm = {};
    std::istringstream ss(timestamp);
    ss >> std::get_time(&tm, "%Y-%m-%dT%H:%M:%SZ");
    return std::mktime(&tm);
}

bool isValidTimestamp(const std::string &timestamp)
{
    std::regex pattern(R"(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z)");
    return std::regex_match(timestamp, pattern);
}
