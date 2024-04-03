#pragma once

#include <string>
#include <ctime>

void validateArguments(int argc, char *argv[]);
bool isValidTimestamp(const std::string &timestamp);
std::time_t convertToTimeT(const std::string &timestamp);
