package com.puzzle.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.puzzle.R
import com.puzzle.dp2px

/**
 * 
 */
class PuzzleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

     var shouldShowBorder = false
    private val outBorderWidth = 0.dp2px()
    private val borderWidth = 5.dp2px()
    private val borderRect = Rect()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.main)
        strokeWidth = borderWidth.toFloat()
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (shouldShowBorder) {
            canvas.drawRect(borderRect, borderPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderRect.set(outBorderWidth, outBorderWidth, w - outBorderWidth, h - outBorderWidth)
    }

    public fun showBorder(show: Boolean) {
        shouldShowBorder = show
        invalidate()
    }
}