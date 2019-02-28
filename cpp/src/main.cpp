#include <iostream>
#include <unordered_map>
#include <unordered_set>
#include <sstream>

typedef std::unordered_set<std::string> Photo;

typedef std::pair<Photo, Photo> Slide;

template <typename T>
std::unordered_set<T> union(const std::unordered_set<T>& l, const std::unordered_set<T>& r) 
{
    std::unordered_set<T> ret;
    for(const auto& elem : l)
        ret.insert(elem);
    for(const auto& elem : r)
        ret.insert(elem);
    return ret;
}

std::unordered_set<std::string> getTags(const Slide& slide) {
    return union(slide.first, slide.second);
}

int main(int argc, char *argv[])
{
    unsigned long numLines;
    std::string inputLine;

    std::unordered_map<unsigned long, Photo> horizontals;
    std::unordered_map<unsigned long, Photo> verticals;

    std::getline(std::cin, inputLine);
    numLines = std::stol(inputLine);
    for(unsigned long id = 0; id < numLines; ++id) {
        std::getline(std::cin, inputLine);
        std::istringstream ss(inputLine);
        char type;
        unsigned long dummy;
        Photo tags;
        ss >> type >> dummy;
        std::string tag;
        while(ss >> tag) {
            tags.insert(tag);
        }
        switch(type) {
            case 'H':
                horizontals.insert({id, tags});
                break;
            case 'V':
                verticals.insert({id, tags});
                break;
            default:
                throw std::runtime_error(std::string{"Type is "} + type + " for photo " + std::to_string(id));
        }
    }

    // Build the solution greedily 

    return 0;
}
