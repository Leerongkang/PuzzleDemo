package com.puzzle.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.puzzle.R
import com.puzzle.dp2px
import kotlin.math.abs

/**
 * 用于显示单张图片的View，选中时有红色边框
 */
class PuzzleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    companion object {
        /** [currentDrawableInfo] 函数的入参，获取当前图片进行变换后的位置和大小 */
        private const val DRAWABLE_WIDTH = 0
        private const val DRAWABLE_HEIGHT = 1
        private const val DRAWABLE_LEFT = 2
        private const val DRAWABLE_TOP = 3
        private const val DRAWABLE_RIGHT = 4
        private const val DRAWABLE_BOTTOM = 5
        /** [center] 函数的入参，自适应缩放模式 */
        const val CENTER_CROP = 0     // 匹配最短边
        const val CENTER_INSIDE = 1   // 匹配最长边
    }

    // 是否绘制边框
    private var shouldShowBorder = false

    // 是否在执行缩放
    private var isScaled = false

    // 是否在执行平移
    private var isTranslated = false

    // 边框内边距
    private val borderPadding = 0.dp2px()

    // 边框宽度
    private val borderWidth = 5.dp2px()

    // 最大放大倍数
    private val maxScale = 3

    // 图片移动阈值，手指滑动距离小于阈值时将视为点击事件
    private var moveThreshold = 1

    // 手指按下时的坐标
    private var lastX = 0F

    private var lastY = 0F

    // 图片放大调整因子
    private val amplificationFactor = 1.01F

    // 图片缩小调整因子
    private val reductionFactor = 0.99F

    // 缩放中心点
    private var centerPoint = 0F to 0F

    // 选中边框位置
    private val borderRect = Rect()

    // 选中边框画笔
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.main)
        strokeWidth = borderWidth.toFloat()
        style = Paint.Style.STROKE
    }

    // 用于进行图片变换的矩阵
    private val adjustMatrix = imageMatrix

    // 图片变换矩阵中的值
    private val matrixValue: FloatArray = FloatArray(9)
        get() {
            imageMatrix.getValues(field)
            return field
        }

    // 缩放手势监听
    private val scaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // 对缩放比例进行调整，增加 1% 的缩放倍数，视觉效果更好
                val scaleFactor = detector.scaleFactor
                val scale = if (scaleFactor > 1) {
                                scaleFactor * amplificationFactor
                            } else {
                                scaleFactor * reductionFactor
                            }
                // 最大缩放到原图的 maxScale 倍
                val lastScale = matrixValue[Matrix.MSCALE_X]
                if (lastScale > maxScale && scale > 1) {
                    return true
                }
                adjustMatrix.postScale(scale, scale, centerPoint.first, centerPoint.second)
                imageMatrix = adjustMatrix
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                isScaled = true
                scaleType = ScaleType.MATRIX
                imageMatrix.reset()
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) { }
        })

    init {
        scaleType = ScaleType.MATRIX
        post {
            center(CENTER_CROP)
        }
    }

    /**
     * 绘制选中边框
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (shouldShowBorder) {
            canvas.drawRect(borderRect, borderPaint)
        }
    }

    /**
     * 尺寸变化时：
     * 1. 修改边框大小
     * 2. 修改图片缩放以及移动
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderRect.set(borderPadding, borderPadding, w - borderPadding, h - borderPadding)
        if (scaleType == ScaleType.CENTER_CROP) {
            return
        }
        center(CENTER_CROP)
    }

    /**
     * [MotionEvent.ACTION_DOWN]: 记录按下时坐标的位置
     * [MotionEvent.ACTION_POINTER_DOWN]: 双指按下时，交给计算缩放中心点[centerPoint]
     * [MotionEvent.ACTION_MOVE]:
     * 1. 双指时，交给[scaleGestureDetector] 处理
     * 2. 单指时，处理图片平移
     * [MotionEvent.ACTION_UP]: 如果不是缩放或者平移操作，执行[performClick]
     * [MotionEvent.ACTION_POINTER_UP]: 多指抬起时，结束缩放，执行图片调整[fixTransformation]
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    isScaled = true
                    centerPoint = scaleCenterPoint(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    return scaleGestureDetector.onTouchEvent(event)
                } else {
                    if (isScaled) {
                        return true
                    }
                    scaleType = ScaleType.MATRIX
                    val dx = event.x - lastX
                    val dy = event.y - lastY
                    lastX = event.x
                    lastY = event.y
                    // 滑动大于阈值，不会触发点击事件
                    if (abs(dy) > moveThreshold || abs(dx) > moveThreshold) {
                        isTranslated = true
                    }
                    adjustMatrix.postTranslate(dx, dy)
                    imageMatrix = adjustMatrix
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isScaled && !isTranslated) {
                    return performClick()
                }
                isScaled = false
                isTranslated = false
                fixTransformation()
                return true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                fixTransformation()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (visibility == View.INVISIBLE) {
                    visibility = View.VISIBLE
                }
            }
        }
        return true
    }


    override fun performClick(): Boolean {
        return super.performClick()
    }

    /**
     * 计算缩放中心点
     */
    private fun scaleCenterPoint(event: MotionEvent): Pair<Float, Float> {
        if (event.pointerCount != 2) {
            return 0F to 0F
        }
        val x0 = event.getX(0)
        val y0 = event.getY(0)
        val x1 = event.getX(1)
        val y1 = event.getY(1)
        return (x0 + x1) / 2 to (y0 + y1) / 2
    }

    /**
     * @param show true: 绘制边框 false: 取消边框
     */
    fun showBorder(show: Boolean) {
        shouldShowBorder = show
        invalidate()
    }

    /**
     * 将图片自适应缩放，并居中
     * @param type [CENTER_CROP]: 匹配最短边； [CENTER_INSIDE]: 匹配最长边
     */
    fun center(type: Int) {
        val drawableWidth= drawable.bounds.width().toFloat()
        val drawableHeight = drawable.bounds.height().toFloat()
        val viewWidth = width - paddingLeft - paddingRight
        val viewHeight = height - paddingTop - paddingBottom
        var dx = 0f
        var dy = 0f
        val scaleX = viewWidth / drawableWidth
        val scaleY = viewHeight / drawableHeight
        val scale = when(type) {
                    CENTER_CROP -> if (scaleX > scaleY) {
                        dy = (viewHeight - (drawableHeight * scaleX)) * 0.5f
                        scaleX
                    } else {
                        dx = (viewWidth - drawableWidth * scaleY) * 0.5f
                        scaleY
                    }

                    CENTER_INSIDE -> if (scaleX < scaleY) {
                        dy = (viewHeight - (drawableHeight * scaleX)) * 0.5f
                        scaleX
                    } else {
                        dx = (viewWidth - drawableWidth * scaleY) * 0.5f
                        scaleY
                    }
                    else -> 0F
        }

        adjustMatrix.setScale(scale, scale)
        adjustMatrix.postTranslate(dx, dy)
        imageMatrix = adjustMatrix
    }

    /**
     * 调整缩放过小，或者移动超出边界
     */
    fun fixTransformation() {
        // 缩放过小时，自适应缩放并居中，结束调整
        val drawableWidth = currentDrawableInfo(DRAWABLE_WIDTH)
        val drawableHeight = currentDrawableInfo(DRAWABLE_HEIGHT)
        if (drawableHeight < height || drawableWidth < width) {
            center(CENTER_CROP)
            return
        }
        // 上边界移动到 View 的内部，自动到顶部对齐
        val drawableTop = currentDrawableInfo(DRAWABLE_TOP)
        if (drawableTop > 0) {
            adjustMatrix.postTranslate(0F, -drawableTop)
            imageMatrix = adjustMatrix
        }
        // 底部边界移动到 View 的内部，自动到底部对齐
        val drawableBottom = currentDrawableInfo(DRAWABLE_BOTTOM)
        if (drawableBottom < height) {
            adjustMatrix.postTranslate(0F, height - drawableBottom)
            imageMatrix = adjustMatrix
        }
        // 左边界移动到 View 的内部，自动左对齐
        val drawableLeft = currentDrawableInfo(DRAWABLE_LEFT)
        if (drawableLeft > 0) {
            adjustMatrix.postTranslate(-drawableLeft, 0F)
            imageMatrix = adjustMatrix
        }
        // 右边界移动到 View 的内部，自动右对齐
        val drawableRight = currentDrawableInfo(DRAWABLE_RIGHT)
        if (drawableRight < width) {
            adjustMatrix.postTranslate(width - drawableRight, 0F)
            imageMatrix = adjustMatrix
        }
    }

    /**
     * 计算经过调整后的图片大小以及位置信息
     * @param info [DRAWABLE_WIDTH], [DRAWABLE_HEIGHT], [DRAWABLE_LEFT], [DRAWABLE_TOP], [DRAWABLE_RIGHT], [DRAWABLE_BOTTOM]
     */
    private fun currentDrawableInfo(
        info: Int
    ) = when (info) {
            DRAWABLE_WIDTH -> {
                matrixValue[Matrix.MSCALE_X] * drawable.bounds.width()
            }
            DRAWABLE_HEIGHT -> {
                matrixValue[Matrix.MSCALE_Y] * drawable.bounds.height()
            }
            DRAWABLE_LEFT -> {
                matrixValue[Matrix.MTRANS_X]
            }
            DRAWABLE_TOP -> {
                matrixValue[Matrix.MTRANS_Y]
            }
            DRAWABLE_RIGHT -> {
                matrixValue[Matrix.MTRANS_X] + matrixValue[Matrix.MSCALE_X] * drawable.bounds.width()
            }
            DRAWABLE_BOTTOM -> {
                matrixValue[Matrix.MTRANS_Y] + matrixValue[Matrix.MSCALE_Y] * drawable.bounds.height()
            }
            else -> {
                0F
            }
    }
}