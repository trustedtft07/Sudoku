package com.sudoku.ndk

object SudokuNative {
    init {
        System.loadLibrary("sudoku_ndk")
    }

    external fun newGame(holes: Int)
    external fun setCell(row: Int, col: Int, value: Int): Boolean
    external fun applyHint(): Boolean
    external fun isSolved(): Boolean
    external fun isConflict(row: Int, col: Int): Boolean
    external fun getBoard(): IntArray
    external fun getFixedMask(): IntArray
}
