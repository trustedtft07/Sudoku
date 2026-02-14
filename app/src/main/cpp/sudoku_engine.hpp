#pragma once

#include <array>
#include <random>

class SudokuEngine {
public:
    static constexpr int kSize = 9;
    using Grid = std::array<int, kSize * kSize>;

    SudokuEngine();

    void newGame(int holes);
    bool setCell(int row, int col, int value);
    bool applyHint();
    bool isSolved() const;
    bool isConflict(int row, int col) const;

    const Grid& board() const { return board_; }
    const Grid& fixedMask() const { return fixed_; }

private:
    Grid board_{};
    Grid solution_{};
    Grid fixed_{};
    std::mt19937 rng_;

    bool fillGrid(Grid& grid, int index);
    bool isValidPlacement(const Grid& grid, int row, int col, int value) const;
};
