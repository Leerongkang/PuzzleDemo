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
 * 用于显示单张图片的View，选中时有红色边框
 */
class PuzzleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // 是否绘制边框
    var shouldShowBorder = false
    // 边框内边距
    private val borderPadding = 0.dp2px()
    // 边框宽度
    private val borderWidth = 5.dp2px()
    // 边框位置
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
        borderRect.set(borderPadding, borderPadding, w - borderPadding, h - borderPadding)
    }

    /**
     * @param show true: 绘制边框 false: 取消边框
     */
    public fun showBorder(show: Boolean) {
        shouldShowBorder = show
        invalidate()
    }
}