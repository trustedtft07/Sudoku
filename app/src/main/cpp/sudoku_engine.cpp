#include "sudoku_engine.hpp"

#include <algorithm>
#include <numeric>

SudokuEngine::SudokuEngine() : rng_(std::random_device{}()) {
    newGame(36);
}

void SudokuEngine::newGame(int holes) {
    solution_.fill(0);
    fillGrid(solution_, 0);
    board_ = solution_;
    fixed_.fill(1);

    std::array<int, kSize * kSize> indices{};
    std::iota(indices.begin(), indices.end(), 0);
    std::shuffle(indices.begin(), indices.end(), rng_);

    int clampedHoles = std::clamp(holes, 10, 60);
    for (int i = 0; i < clampedHoles; ++i) {
        int idx = indices[i];
        board_[idx] = 0;
        fixed_[idx] = 0;
    }
}

bool SudokuEngine::setCell(int row, int col, int value) {
    if (row < 0 || row >= kSize || col < 0 || col >= kSize) return false;
    if (value < 0 || value > 9) return false;

    int idx = row * kSize + col;
    if (fixed_[idx] == 1) return false;

    board_[idx] = value;
    return true;
}

bool SudokuEngine::applyHint() {
    std::array<int, kSize * kSize> emptyCells{};
    int count = 0;
    for (int i = 0; i < kSize * kSize; ++i) {
        if (board_[i] == 0) {
            emptyCells[count++] = i;
        }
    }
    if (count == 0) return false;

    std::uniform_int_distribution<int> dist(0, count - 1);
    int chosen = emptyCells[dist(rng_)];
    board_[chosen] = solution_[chosen];
    fixed_[chosen] = 1;
    return true;
}

bool SudokuEngine::isSolved() const {
    for (int r = 0; r < kSize; ++r) {
        for (int c = 0; c < kSize; ++c) {
            int val = board_[r * kSize + c];
            if (val == 0 || isConflict(r, c)) {
                return false;
            }
        }
    }
    return true;
}

bool SudokuEngine::isConflict(int row, int col) const {
    int val = board_[row * kSize + col];
    if (val == 0) return false;

    for (int i = 0; i < kSize; ++i) {
        if (i != col && board_[row * kSize + i] == val) return true;
        if (i != row && board_[i * kSize + col] == val) return true;
    }

    int boxRow = (row / 3) * 3;
    int boxCol = (col / 3) * 3;
    for (int r = boxRow; r < boxRow + 3; ++r) {
        for (int c = boxCol; c < boxCol + 3; ++c) {
            if ((r != row || c != col) && board_[r * kSize + c] == val) {
                return true;
            }
        }
    }
    return false;
}

bool SudokuEngine::fillGrid(Grid& grid, int index) {
    if (index == kSize * kSize) return true;

    int row = index / kSize;
    int col = index % kSize;

    std::array<int, 9> digits{1, 2, 3, 4, 5, 6, 7, 8, 9};
    std::shuffle(digits.begin(), digits.end(), rng_);

    for (int d : digits) {
        if (isValidPlacement(grid, row, col, d)) {
            grid[index] = d;
            if (fillGrid(grid, index + 1)) {
                return true;
            }
            grid[index] = 0;
        }
    }
    return false;
}

bool SudokuEngine::isValidPlacement(const Grid& grid, int row, int col, int value) const {
    for (int i = 0; i < kSize; ++i) {
        if (grid[row * kSize + i] == value || grid[i * kSize + col] == value) {
            return false;
        }
    }

    int boxRow = (row / 3) * 3;
    int boxCol = (col / 3) * 3;
    for (int r = boxRow; r < boxRow + 3; ++r) {
        for (int c = boxCol; c < boxCol + 3; ++c) {
            if (grid[r * kSize + c] == value) {
                return false;
            }
        }
    }
    return true;
}
