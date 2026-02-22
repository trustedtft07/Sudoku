package com.sudoku.ndk

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var boardView: SudokuBoardView
    private lateinit var tvStatus: TextView
    private var currentHoles = 36

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView = findViewById(R.id.sudoku_board)
        tvStatus = findViewById(R.id.tv_status)

        val numberIds = listOf(
            R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6,
            R.id.btn_7, R.id.btn_8, R.id.btn_9
        )

        numberIds.forEachIndexed { index, id ->
            findViewById<Button>(id).setOnClickListener {
                placeValue(index + 1)
            }
        }

        findViewById<Button>(R.id.btn_erase).setOnClickListener { placeValue(0) }
        findViewById<Button>(R.id.btn_hint).setOnClickListener {
            if (SudokuNative.applyHint()) {
                boardView.refreshFromNative()
                checkVictory()
            }
        }

        findViewById<Button>(R.id.btn_easy).setOnClickListener { startNewGame(28, "Mudah") }
        findViewById<Button>(R.id.btn_medium).setOnClickListener { startNewGame(38, "Sedang") }
        findViewById<Button>(R.id.btn_hard).setOnClickListener { startNewGame(48, "Sulit") }

        startNewGame(currentHoles, "Sedang")
    }

    private fun startNewGame(holes: Int, label: String) {
        currentHoles = holes
        SudokuNative.newGame(holes)
        boardView.refreshFromNative()
        tvStatus.text = "Level: $label"
    }

    private fun placeValue(value: Int) {
        val row = boardView.selectedRow
        val col = boardView.selectedCol
        if (row !in 0..8 || col !in 0..8) {
            Toast.makeText(this, "Pilih kotak terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (SudokuNative.setCell(row, col, value)) {
            boardView.refreshFromNative()
            checkVictory()
        }
    }

    private fun checkVictory() {
        if (SudokuNative.isSolved()) {
            tvStatus.text = "Selesai!"
            Toast.makeText(this, "Selamat, Sudoku selesai!", Toast.LENGTH_LONG).show()
        }
    }
}
