#include <iostream>
#include <unordered_map>
#include <unordered_set>
#include <algorithm>
#include <numeric>
#include <sstream>
#include <deque>
#include <vector>

typedef std::pair<long, std::unordered_set<std::string>> Photo;

typedef std::pair<Photo, Photo> Slide;

template <typename T>
std::unordered_set<T> do_union(const std::unordered_set<T>& l, const std::unordered_set<T>& r) 
{
    std::unordered_set<T> ret;
    for(const auto& elem : l)
        ret.insert(elem);
    for(const auto& elem : r)
        ret.insert(elem);
    return ret;
}

std::unordered_set<std::string> getTags(const Slide& slide) {
    return do_union(slide.first.second, slide.second.second);
}

void output(const Slide& slide)
{
    // Print the left photo first
    std::cout << slide.first.first;
    // If there's a right one (i.e. vertical things), print the right one
    if(slide.second.second.size() != 0)
        std::cout << " " << slide.second.first;
}

void output(const std::deque<Slide>& solution)
{
    // Number of entries
    std::cout << solution.size() << std::endl;
    for(const auto& slide : solution) {
        output(slide);
        std::cout << std::endl;
    }
}

struct candidateSlide {
    bool addAtFront;
    Slide photos;
    unsigned long score;
};

template <typename T>
unsigned long intersectionSize(const std::unordered_set<T>& A, const std::unordered_set<T>& B)
{
    unsigned long size = 0;
    for(const auto& a : A)
        if(B.find(a) != B.end())
            ++size;
    return size;
}

template <typename T>
unsigned long setminusSize(const std::unordered_set<T>& A, const std::unordered_set<T>& B)
{
    return A.size() - intersectionSize(A, B);
}

unsigned long getScore(const Slide& slide, const Photo& photo)
{
    std::unordered_set<std::string> slideTags(do_union(slide.first.second, slide.second.second));
    return std::min(
            std::min(
                setminusSize(slideTags, photo.second),
                setminusSize(photo.second, slideTags)),
            intersectionSize(slideTags, photo.second)
            );
}

candidateSlide getBestCandidate(const std::vector<candidateSlide>& candidates)
{
    std::size_t bestIndex = 0;
    unsigned long bestScore = 0;
    for(std::size_t i = 0; i < candidates.size(); ++i)
        if(candidates[i].score > bestScore)
            bestIndex = i;
    return candidates[bestIndex];
}

void insertBestSlide(std::deque<Slide>& solution, std::unordered_map<unsigned long, Photo>& horizontals, std::unordered_map<unsigned long, Photo>& verticals)
{
    std::vector<candidateSlide> candidates;
    const auto createFrontHorizontalCandidate = [&](const Photo& photo) -> candidateSlide {
        candidateSlide candidate;
        candidate.addAtFront = true;
        candidate.photos.first = photo;
        candidate.score = getScore(solution.front(), photo);
        return candidate;
    };
    const auto createBackHorizontalCandidate = [&](const Photo& photo) -> candidateSlide {
        candidateSlide candidate;
        candidate.addAtFront = false;
        candidate.photos.first = photo;
        candidate.score = getScore(solution.front(), photo);
        return candidate;
    };


    const auto createFrontVerticalCandidate = [&](const Photo& lphoto, const Photo& rphoto) -> candidateSlide {
        candidateSlide candidate;
        candidate.addAtFront = true;
        candidate.photos.first = lphoto;
        candidate.photos.second = rphoto;
        Photo mergedPhoto = lphoto;
        mergedPhoto.second = do_union(lphoto.second, rphoto.second);
        candidate.score = getScore(solution.front(), mergedPhoto);
        return candidate;
    };
    const auto createBackVerticalCandidate = [&](const Photo& lphoto, const Photo& rphoto) -> candidateSlide {
        candidateSlide candidate;
        candidate.addAtFront = false;
        candidate.photos.first = lphoto;
        candidate.photos.second = rphoto;
        Photo mergedPhoto = lphoto;
        mergedPhoto.second = do_union(lphoto.second, rphoto.second);
        candidate.score = getScore(solution.front(), mergedPhoto);
        return candidate;
    };
    
    candidates.reserve(1 + horizontals.size() + (verticals.size() * (verticals.size() - 1)) / 2);
    for(const auto& horizontal : horizontals) {
        candidates.push_back(createFrontHorizontalCandidate(horizontal.second));
        candidates.push_back(createBackHorizontalCandidate(horizontal.second));
    }
    for(const auto& lvert : verticals) {
        for(const auto& rvert : verticals) {
            if(lvert.second.first > rvert.second.first) {
                candidates.push_back(createFrontVerticalCandidate(lvert.second, rvert.second));
                candidates.push_back(createBackVerticalCandidate(lvert.second, rvert.second));
            }
        }
    }
    candidateSlide bestCandidate = getBestCandidate(candidates);
    if(bestCandidate.addAtFront)
        solution.push_front(bestCandidate.photos);
    else
        solution.push_back(bestCandidate.photos);
    if(bestCandidate.photos.second.second.size() != 0) {
        // Then it was a vertical
        verticals.erase(bestCandidate.photos.first.first);
        verticals.erase(bestCandidate.photos.second.first);
    } else {
        horizontals.erase(bestCandidate.photos.first.first);
    }
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
        std::unordered_set<std::string> tags;
        ss >> type >> dummy;
        std::string tag;
        while(ss >> tag) {
            tags.insert(tag);
        }
        switch(type) {
            case 'H':
                horizontals.insert({id, {id, tags}});
                break;
            case 'V':
                verticals.insert({id, {id, tags}});
                break;
            default:
                throw std::runtime_error(std::string{"Type is "} + type + " for photo " + std::to_string(id));
        }
    }

    std::deque<Slide> solution;
    // First add the first element arbitrarily
    Slide firstSlide;
    firstSlide.first = horizontals.begin()->second;
    solution.push_front(firstSlide);
    horizontals.erase(0);
    // Build the solution greedily 
    unsigned done = 0;
    while(horizontals.size() > 0 || verticals.size() > 1) {
        insertBestSlide(solution, horizontals, verticals);
        std::cerr << ++done << " processed" << std::endl;
    }
    // Output the solution
    output(solution);
    return 0;
}
