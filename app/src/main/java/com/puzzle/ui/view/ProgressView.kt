package com.puzzle.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.puzzle.R
import com.puzzle.dp2px

/**
 * 展示下载进度的 View
 */
class ProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 最大进度
    var maxProgress = 100F

    // 当前进度
    var progress : Float = 0F
        set(value) {
            field = if (maxProgress <= 0F || field < 0) {
                        0F
                    } else {
                        value / maxProgress * 360
                    }
            invalidate()
        }

    // 圆圈宽度
    private val circleWidth = 2

    // 内边距，防止画圆时，显示不全
    private val padding = 5F

    // 圆圈进度位置
    private val rect = RectF()

    // 用于绘制圆圈
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = circleWidth.dp2px().toFloat()
        style = Paint.Style.STROKE
        color = context.getColor(R.color.white)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 进度大于 100% 时，取消绘制
        if (progress >= 360F) {
            return
        }
        // 绘制当前进度
        canvas.apply {
            drawArc(rect, -90F, progress, false, paint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.top = padding
        rect.left = padding
        rect.bottom = h.toFloat() - padding
        rect.right = w.toFloat() - padding
    }
}