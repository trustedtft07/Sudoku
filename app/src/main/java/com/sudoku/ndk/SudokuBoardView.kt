package com.sudoku.ndk

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class SudokuBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var selectedRow: Int = -1
        private set
    var selectedCol: Int = -1
        private set

    private var board = IntArray(81)
    private var fixedMask = IntArray(81)

    private val bgPaint = Paint().apply { color = Color.WHITE }
    private val selectedPaint = Paint().apply { color = Color.parseColor("#DBEAFE") }
    private val highlightPaint = Paint().apply { color = Color.parseColor("#F1F5F9") }
    private val thinLinePaint = Paint().apply {
        color = Color.parseColor("#CBD5E1")
        strokeWidth = 2f
    }
    private val thickLinePaint = Paint().apply {
        color = Color.parseColor("#1E293B")
        strokeWidth = 6f
    }
    private val fixedTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0F172A")
        textAlign = Paint.Align.CENTER
    }
    private val userTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2563EB")
        textAlign = Paint.Align.CENTER
    }
    private val conflictTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#DC2626")
        textAlign = Paint.Align.CENTER
    }

    fun refreshFromNative() {
        board = SudokuNative.getBoard()
        fixedMask = SudokuNative.getFixedMask()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && width > 0) {
            val cellSize = width / 9f
            val col = (event.x / cellSize).toInt().coerceIn(0, 8)
            val row = (event.y / cellSize).toInt().coerceIn(0, 8)
            selectedRow = row
            selectedCol = col
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = width.toFloat()
        val cell = size / 9f

        canvas.drawRect(0f, 0f, size, size, bgPaint)

        if (selectedRow in 0..8 && selectedCol in 0..8) {
            for (i in 0 until 9) {
                canvas.drawRect(i * cell, selectedRow * cell, (i + 1) * cell, (selectedRow + 1) * cell, highlightPaint)
                canvas.drawRect(selectedCol * cell, i * cell, (selectedCol + 1) * cell, (i + 1) * cell, highlightPaint)
            }
            canvas.drawRect(
                selectedCol * cell,
                selectedRow * cell,
                (selectedCol + 1) * cell,
                (selectedRow + 1) * cell,
                selectedPaint
            )
        }

        val textSize = cell * 0.58f
        fixedTextPaint.textSize = textSize
        userTextPaint.textSize = textSize
        conflictTextPaint.textSize = textSize
        val textOffset = (fixedTextPaint.descent() + fixedTextPaint.ascent()) / 2f

        for (r in 0 until 9) {
            for (c in 0 until 9) {
                val value = board[r * 9 + c]
                if (value == 0) continue

                val paint = when {
                    fixedMask[r * 9 + c] == 1 -> fixedTextPaint
                    SudokuNative.isConflict(r, c) -> conflictTextPaint
                    else -> userTextPaint
                }

                val cx = c * cell + cell / 2f
                val cy = r * cell + cell / 2f - textOffset
                canvas.drawText(value.toString(), cx, cy, paint)
            }
        }

        for (i in 0..9) {
            val pos = i * cell
            val paint = if (i % 3 == 0) thickLinePaint else thinLinePaint
            canvas.drawLine(pos, 0f, pos, size, paint)
            canvas.drawLine(0f, pos, size, pos, paint)
        }
    }
}
