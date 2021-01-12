package com.puzzle.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import com.puzzle.PuzzleApplication
import com.puzzle.R
import com.puzzle.dp2px
import com.puzzle.template.Template
import com.puzzle.template.TemplateInfo
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 自定义的拼图ViewGroup
 * 通过onLayout对内部的ImageView进行布局从而实现拼图
 */
class PuzzleLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    companion object {
        const val FRAME_NONE = 0
        const val FRAME_SMALL = 5
        const val FRAME_MEDIUM = 10
        const val FRAME_LARGE = 15
    }

    //默认的拼图模板
    //    |————|
    //    |    |
    //    |————|
    //    |    |
    //    |————|
    var template = Template(
        4, 1024, 1024, "1001", listOf(
            TemplateInfo(0, 0, 1024, 512),
            TemplateInfo(0, 512, 1024, 1024)
        )
    )
        set(value) {
            field = value
            horizontalBorders.clear()
            verticalBorders.clear()
            for (info in value.templates) {
                if (info.left != 0) {
                    horizontalBorders.add((info.left * proportion).roundToInt())
                }
                if (info.right != value.totalWidth) {
                    horizontalBorders.add((info.right * proportion).roundToInt())
                }
                if (info.top != 0) {
                    verticalBorders.add((info.top * proportion).roundToInt())
                }
                if (info.bottom != value.totalHeight) {
                    verticalBorders.add((info.bottom * proportion).roundToInt())
                }
            }
        }
    // 拼图模板的缩放比例
    var proportion = 0.0
    // 边框大小
    private var frameSize = 0
    // 被拖动View的位置信息
    private var exchangeSourceLeft = 0
    private var exchangeSourceRight = 0
    private var exchangeSourceTop = 0
    private var exchangeSourceBottom = 0
    // 交换图片的两个View的下标
    private var sourceIndex = -1
    private var destinationIndex = -1
    // z 轴坐标，使View被拖动时始终保持在最上层
    private var zIndex = 100
    // 宽高的1/3，用于绘制辅助线
    private var oneThirdWidth = 0F
    private var oneThirdHeight = 0F
    // 辅助线宽度
    private val guideLineWidth = 5F
    // 手指按下时的坐标
    private var pressedX = 0F
    private var pressedY = 0F
    // View拖动的阈值，拖动偏移量大于阈值才会拦截触摸事件
    private val moveThreshold = 50F
    // 开始一次新的触摸事件流
    private var newSelect = false
    private var isReplacing = false
    //
    private val verticalBorders = mutableListOf<Int>()
    private val horizontalBorders = mutableListOf<Int>()
    // 拼图输入图片
    private val bitmapList = mutableListOf<Bitmap>()
    // 默认的子View，不显示，用于逻辑判断
    private val defaultPuzzleImageView = PuzzleImageView(context)
    private var sourceImageView: PuzzleImageView = defaultPuzzleImageView
    private var destinationImageView: PuzzleImageView = defaultPuzzleImageView
    var onImageClickListener: OnImageClickListener = { _, _ -> }
    private val puzzleImageViewOnClickListener: OnImageClickListener = { index, view ->
        clearAllImageViewSelectBorder()
        view.showBorder(true)
        onImageClickListener(index, view)
    }
    private val guideLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.disabled)
        style = Paint.Style.STROKE
        strokeWidth = guideLineWidth
    }

    init {
        layoutParams = LayoutParams(template.totalWidth, template.totalHeight)
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        // 拖动时， 绘制辅助线
        if (isReplacing) {
            canvas.drawLine(
                oneThirdWidth,
                0F,
                oneThirdWidth,
                height.toFloat(),
                guideLinePaint
            )
            canvas.drawLine(
                oneThirdWidth * 2,
                0F,
                oneThirdWidth * 2,
                height.toFloat(),
                guideLinePaint
            )
            canvas.drawLine(
                0F,
                oneThirdHeight,
                width.toFloat(),
                oneThirdHeight,
                guideLinePaint
            )
            canvas.drawLine(
                0F,
                oneThirdHeight * 2,
                width.toFloat(),
                oneThirdHeight * 2,
                guideLinePaint
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val templateInfoList = template.templates
        for (i in templateInfoList.indices) {
            val imageView = getChildAt(i)
            val info = templateInfoList[i]
            imageView?.layout(
                (info.left * proportion).toInt() + frameSize,
                (info.top * proportion).toInt() + frameSize,
                if (info.right != template.totalWidth) {
                    (info.right * proportion).toInt()
                } else {
                    (info.right * proportion).toInt() - frameSize
                },
                if (info.bottom != template.totalHeight) {
                    (info.bottom * proportion).toInt()
                } else {
                    (info.bottom * proportion).toInt() - frameSize
                }
            )
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            // 按下时 记录坐标，不拦截事件
            MotionEvent.ACTION_DOWN -> {
                pressedX = event.x
                pressedY = event.y
                newSelect = true
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = abs(pressedX - event.x)
                val offsetY = abs(pressedY - event.y)
                // X轴或Y轴偏移量大于 moveThreshold 时才进行拦截
                val exchange = offsetX > moveThreshold || offsetY > moveThreshold
                if (exchange && newSelect) {
                    val x = event.x.toInt()
                    val y = event.y.toInt()
                    // 查找当前选中的 View
                    for (i in 0 until childCount) {
                        val view = getChildAt(i)
                        if (y in view.top..view.bottom && x in view.left..view.right) {
                            sourceIndex = i
                            sourceImageView = view as PuzzleImageView
                            sourceImageView.alpha = 0.7F
                            sourceImageView.scaleType = ScaleType.CENTER_INSIDE
                            sourceImageView.z = (zIndex++).toFloat()
                            exchangeSourceLeft = sourceImageView.left
                            exchangeSourceRight = sourceImageView.right
                            exchangeSourceTop = sourceImageView.top
                            exchangeSourceBottom = sourceImageView.bottom
                            break
                        }
                    }
                    newSelect = false
                    isReplacing = true
                }
                return exchange
            }
            MotionEvent.ACTION_UP -> {
                val offsetX = abs(pressedX - event.x)
                val offsetY = abs(pressedY - event.y)
                pressedX = 0F
                pressedY = 0F
                return offsetX > moveThreshold || offsetY > moveThreshold
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.e("kkl", MotionEvent.actionToString(event.action))
        val x = event.x
        val y = event.y
        val offsetX = (pressedX - x).roundToInt()
        val offsetY = (pressedY - y).roundToInt()
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                // 移动选中的View
                sourceImageView.apply {
                    layout(
                        exchangeSourceLeft - offsetX, exchangeSourceTop - offsetY,
                        exchangeSourceRight - offsetX, exchangeSourceBottom - offsetY
                    )
                }
                // 拖动回原来位置时，清空选中
                if (y.roundToInt() in exchangeSourceTop..exchangeSourceBottom &&
                    x.roundToInt() in exchangeSourceLeft..exchangeSourceRight){
                    clearAllImageViewSelectBorder()
                    destinationIndex = -1
                    destinationImageView = defaultPuzzleImageView
                    return true
                }
                // 根据位置查找交换的图片View
                for (i in 0 until childCount) {
                    val view = getChildAt(i)
                    if (y.roundToInt() in view.top..view.bottom &&
                        x.roundToInt() in view.left..view.right &&
                        view != sourceImageView
                    ) {
                        // 移动到不同的View时才更新界面
                        if (view != destinationImageView) {
                            clearAllImageViewSelectBorder()
                            destinationIndex = i
                            destinationImageView = view as PuzzleImageView
                            destinationImageView.showBorder(true)
                            break
                        }
                    }
                }
            }
            // 抬起后，还原透明度，交换图片
            MotionEvent.ACTION_UP -> {
                sourceImageView.alpha = 1.0F
                clearAllImageViewSelectBorder()
                sourceImageView.scaleType = ScaleType.CENTER_CROP
                sourceImageView.layout(
                    exchangeSourceLeft, exchangeSourceTop,
                    exchangeSourceRight, exchangeSourceBottom
                )
                isReplacing = false
                exchangeBitmaps()
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        oneThirdWidth = w / 3F
        oneThirdHeight = h / 3F
    }

    /**
     * 交换两个View中的图片
     */
    private fun exchangeBitmaps() {
        if (bitmapList.size <= 1){
            return
        }
        if (sourceIndex == -1 || destinationIndex == -1) {
            return
        }
        destinationImageView = defaultPuzzleImageView
        sourceImageView = defaultPuzzleImageView
        val temp = bitmapList[sourceIndex]
        bitmapList[sourceIndex] = bitmapList[destinationIndex]
        bitmapList[destinationIndex] = temp
        (getChildAt(sourceIndex) as PuzzleImageView).setImageBitmap(bitmapList[sourceIndex])
        (getChildAt(destinationIndex) as PuzzleImageView).setImageBitmap(bitmapList[destinationIndex])
        sourceIndex = -1
        destinationIndex = -1
    }

    fun initViews(bitmaps: List<Bitmap>, imageCount: Int) {
        removeAllViews()
        bitmapList.clear()
        bitmapList.addAll(bitmaps)
        for (i in 0 until imageCount) {
            val view = PuzzleImageView(context).apply {
                scaleType = ScaleType.CENTER_CROP
                val index = if (i >= bitmapList.size) {
                                        0
                                  } else {
                                        i
                                  }
                setImageBitmap(bitmapList[index])
                setOnClickListener {
                    puzzleImageViewOnClickListener(i, it as PuzzleImageView)
                }
            }
            addView(view)
        }
    }

    fun updateFrameSize(frame: Int) {
        frameSize = frame.dp2px()
        requestLayout()
        invalidate()
    }

    fun clearAllImageViewSelectBorder() {
        for (i in 0..childCount) {
            val view = getChildAt(i)
            if (view is PuzzleImageView) {
                view.showBorder(false)
            }
        }
    }

}

typealias OnImageClickListener = (index: Int, view: PuzzleImageView) -> Unit