package com.puzzle.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import com.puzzle.R
import com.puzzle.dp2px
import com.puzzle.material.Template
import com.puzzle.material.TemplateInfo
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 自定义的拼图ViewGroup，支持动态调整
 * 通过onLayout对内部的ImageView进行布局从而实现拼图
 */
class PuzzleLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    companion object {
        // 拼图边框大小
        const val FRAME_NONE = 0
        const val FRAME_SMALL = 5
        const val FRAME_MEDIUM = 10
        const val FRAME_LARGE = 15

        // 调整大小时，选择的边框
        private const val BORDER_DEFAULT = -1
        private const val BORDER_TOP = 0
        private const val BORDER_BOTTOM = 1
        private const val BORDER_LEFT = 2
        private const val BORDER_RIGHT = 3
    }

    // 开始一次新的触摸事件流
    private var newSelect = false

    // 是否绘制辅助线
    private var showGuildLine = false

    // 缩放操作开始（第二个手指按下）
    private var isScaling = false

    // 缩放操作结束（第二个手指抬起）
    private var isScaled = false

    // 正在调整大小
    private var isAdjusterBorder = false

    // 大小是否到达阈值，需要禁用调整
    private var isLimitAdjusterBorder = false

    // 当前手势是否向上（左）滑动
    private var isMoveToStart = true

    //默认的拼图模板
    //     ____
    //    |    |
    //    |————|
    //    |____|
    var template = Template(
        4, 1024, 1024, "1001", listOf(
            TemplateInfo(0, 0, 1024, 512),
            TemplateInfo(0, 512, 1024, 1024)
        )
    )

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
    private val guideLineWidth = 3F

    // 手指按下时的坐标
    private var pressedX = 0F
    private var pressedY = 0F

    // 手指上一次移动时的坐标
    private var lastMoveX = 0F
    private var lastMoveY = 0F

    // View拖动的阈值，拖动偏移量大于阈值才会拦截触摸事件
    private val moveThreshold = 0.dp2px()

    // View大小调整拖动的阈值，拖动偏移量大于阈值才会拦截触摸事件
    private val moveBorderThreshold = 20.dp2px()

    // 动态调整时，图片宽高最小限制
    private val minPuzzleImageViewSize = 60.dp2px()

    // 拼图模板的缩放比例
    var proportion = 0.0

    // 当前选中边框
    private var adjustBorder = BORDER_DEFAULT

    //动态调整线上（左）方的View
    private val abovePuzzleImageViews = mutableListOf<PuzzleImageView>()
    private val abovePuzzleImageViewsRect = mutableListOf<Rect>()

    //动态调整线下（右）方的View
    private val underPuzzleImageViews = mutableListOf<PuzzleImageView>()
    private val underPuzzleImageViewsRect = mutableListOf<Rect>()

    // 拼图输入图片
    private val bitmapList = mutableListOf<Bitmap>()

    // 默认的子 View，不显示，用于逻辑判断
    private val defaultPuzzleImageView = PuzzleImageView(context)

    // 拖动的子 View
    private var sourceImageView: PuzzleImageView = defaultPuzzleImageView

    // 被替换的子 View
    private var destinationImageView: PuzzleImageView = defaultPuzzleImageView

    // 监听，用于拖动时，隐藏工具栏
    var onHideUtilsListener: OnHideUtilsListener = {}

    // 对外部提供，监听点击子 View 的点击，用于显示工具栏
    var onImageClickListener: OnImageClickListener = { _, _ -> }

    // 监听图片位置交换
    var onImageExchangeListener: OnImageExchangeListener = { _, _ -> }

    // 用于监听点击子 View 的点击，用于响应绘制选中框，
    private val puzzleImageViewOnClickListener: OnImageClickListener = { index, view ->
        clearAllImageViewSelectBorder()
        view.showBorder(true)
        onImageClickListener(index, view)
    }

    // 初始化展示图片的子 View，并绑定点击事件
    private val puzzleImageViews = List(9) { index ->
        PuzzleImageView(context).apply {
            scaleType = ScaleType.MATRIX
            setOnClickListener {
                puzzleImageViewOnClickListener(index, it as PuzzleImageView)
            }
        }
    }

    // 辅助线画笔
    private val guideLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.disabled)
        style = Paint.Style.STROKE
        strokeWidth = guideLineWidth
    }

    init {
        layoutParams = LayoutParams(template.totalWidth, template.totalHeight)
    }

    /**
     * 绘制辅助线
     */
    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        // 拖动时， 绘制辅助线
        if (showGuildLine) {
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

    /**
     * 通过模板[template]，摆放PuzzleImageView到对应的位置
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val templateInfoList = template.templates
        for (i in 0 until  childCount) {
            val imageView = getChildAt(i) as PuzzleImageView
            val info = templateInfoList[i]
            val viewLeft = (info.left * proportion).roundToInt() + frameSize
            val viewTop = (info.top * proportion).roundToInt() + frameSize
            val vr = (info.right * proportion).roundToInt()
            val viewRight = if (right < vr || info.right == template.totalWidth) {
                                vr - frameSize
                            } else {
                                vr
                            }
            val vb = (info.bottom * proportion).roundToInt()
            val viewBottom = if (bottom < vb || info.bottom == template.totalHeight ) {
                                vb - frameSize
                            } else {
                                vb
                            }
            imageView.layout(viewLeft, viewTop, viewRight, viewBottom)
            val isUpdate = imageView.tag as Boolean
            // 更换图片后，自适应缩放并居中
            if (isUpdate || bitmapList.size == 1) {
                imageView.center(PuzzleImageView.CENTER_CROP)
                imageView.tag = false
            }
        }
    }

    /**
     * 对触摸事件进行拦截
     * [MotionEvent.ACTION_DOWN]  不拦截，记录按下时的坐标，并查找点击的子 View
     * [MotionEvent.ACTION_MOVE]  满足以下条件之一，仅进行拦截，返回 [true]
     *                              1. 大小调整： 触摸点在边框附近[moveBorderThreshold]，且移动偏移量大于 [moveThreshold]
     *                              2. 图片交换： 当前坐标移动到其他子 View 内
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.actionMasked) {
            // 按下时 记录坐标，不拦截事件
            MotionEvent.ACTION_DOWN -> {
                pressedX = event.x
                pressedY = event.y
                lastMoveX = event.x
                lastMoveY = event.y
                newSelect = true
                isLimitAdjusterBorder = false
                adjustBorder = BORDER_DEFAULT
                // 查找当前选中的 View
                for (i in 0 until childCount) {
                    val view = getChildAt(i)
                    if (y in view.top..view.bottom && x in view.left..view.right) {
                        sourceIndex = i
                        sourceImageView = view as PuzzleImageView
                        // 使当前选中 view，置于顶层
                        sourceImageView.z = (zIndex++).toFloat()
                        exchangeSourceLeft = sourceImageView.left
                        exchangeSourceRight = sourceImageView.right
                        exchangeSourceTop = sourceImageView.top
                        exchangeSourceBottom = sourceImageView.bottom
                        // 清空上一次调整所保存的View
                        abovePuzzleImageViews.clear()
                        underPuzzleImageViews.clear()
                        abovePuzzleImageViewsRect.clear()
                        underPuzzleImageViewsRect.clear()
                        break
                    }
                }
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                if (isScaling) {
                    return false
                }
                val offsetX = abs(pressedX - event.x)
                val offsetY = abs(pressedY - event.y)
                lastMoveX = event.x
                lastMoveY = event.y
                // X轴或Y轴偏移量大于 moveThreshold 时才进行拦截
                val exchange = offsetX > moveThreshold || offsetY > moveThreshold
                // 动态调整左边界，拦截
                val leftRange = (exchangeSourceLeft - moveBorderThreshold)..(exchangeSourceLeft + moveBorderThreshold)
                if (exchange && exchangeSourceLeft != frameSize && pressedX.roundToInt() in leftRange) {
                    for (i in 0 until childCount) {
                        val view = getChildAt(i) as PuzzleImageView
                        if (exchangeSourceLeft == view.left) {
                            underPuzzleImageViews.add(view)
                            underPuzzleImageViewsRect.add(Rect(view.left, view.top, view.right, view.bottom))
                        }
                        if (exchangeSourceLeft == view.right + frameSize) {
                            abovePuzzleImageViews.add(view)
                            abovePuzzleImageViewsRect.add(Rect(view.left, view.top, view.right, view.bottom))
                        }
                    }
                    isAdjusterBorder = true
                    showGuildLine = true
                    adjustBorder = BORDER_LEFT
                    onHideUtilsListener()
                    return true
                }
                // 动态调整右边界，拦截
                val rightRange =
                    (exchangeSourceRight - moveBorderThreshold)..(exchangeSourceRight + moveBorderThreshold)
                if (exchange && exchangeSourceRight != (width - frameSize) && pressedX.roundToInt() in rightRange) {
                    for (i in 0 until childCount) {
                        val view = getChildAt(i) as PuzzleImageView
                        if (exchangeSourceRight == view.right) {
                            abovePuzzleImageViews.add(view)
                            abovePuzzleImageViewsRect.add(Rect(view.left, view.top, view.right, view.bottom))
                        }
                        if (exchangeSourceRight == view.left - frameSize) {
                            underPuzzleImageViews.add(view)
                            underPuzzleImageViewsRect.add(Rect(view.left, view.top, view.right, view.bottom))
                        }
                    }
                    isAdjusterBorder = true
                    showGuildLine = true
                    adjustBorder = BORDER_RIGHT
                    onHideUtilsListener()
                    return true
                }
                // 动态调整上边界，拦截
                val topRange = (exchangeSourceTop - moveBorderThreshold)..(exchangeSourceTop + moveBorderThreshold)
                if (exchange && exchangeSourceTop != frameSize && pressedY.roundToInt() in topRange) {
                    for (i in 0 until childCount) {
                        val view = getChildAt(i) as PuzzleImageView
                        // 边界下面的View
                        if (exchangeSourceTop == view.top) {
                            underPuzzleImageViews.add(view)
                            underPuzzleImageViewsRect.add(Rect(view.left, view.top, view.right, view.bottom))
                        }
                        // 边界上面的View
                        if (exchangeSourceTop == view.bottom + frameSize) {
                            abovePuzzleImageViews.add(view)
                            abovePuzzleImageViewsRect.add(Rect(view.left, view.top, view.right, view.bottom))
                        }
                    }
                    isAdjusterBorder = true
                    showGuildLine = true
                    adjustBorder = BORDER_TOP
                    onHideUtilsListener()
                    return true
                }
                // 动态调整下边界，拦截
                val bottomRange = (exchangeSourceBottom - moveBorderThreshold)..(exchangeSourceBottom + moveBorderThreshold)
                if (exchange && exchangeSourceBottom != (height - frameSize) && pressedY.roundToInt() in bottomRange) {
                    for (i in 0 until childCount) {
                        val view = getChildAt(i) as PuzzleImageView
                        // 边界上面的View
                        if (exchangeSourceBottom == view.bottom) {
                            abovePuzzleImageViews.add(view)
                            abovePuzzleImageViewsRect.add(Rect(view.left, view.top, view.right, view.bottom))
                        }
                        // 边界下面的View
                        if (exchangeSourceBottom == view.top - frameSize) {
                            underPuzzleImageViews.add(view)
                            underPuzzleImageViewsRect.add(Rect(view.left, view.top, view.right, view.bottom))
                        }
                    }
                    isAdjusterBorder = true
                    showGuildLine = true
                    adjustBorder = BORDER_BOTTOM
                    onHideUtilsListener()
                    return true
                }
                // 拦截拖动更换图片
                val inOther = x !in (exchangeSourceLeft..exchangeSourceRight) ||
                        y !in (exchangeSourceTop..exchangeSourceBottom)
                if (inOther && newSelect) {
                    newSelect = false
                    showGuildLine = true
                    adjustBorder = BORDER_DEFAULT
                    // 防止闪烁
                    sourceImageView.visibility = View.INVISIBLE
                }
                return inOther
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                isScaling = true
                isScaled = true
                return false
            }
            MotionEvent.ACTION_POINTER_UP -> {
                isScaling = false
            }
        }
        adjustBorder = BORDER_DEFAULT
        return false
    }

    /**
     * [MotionEvent.ACTION_MOVE]:
     * 1. 双指按下时，不处理。
     * 2. 调整大小时：调整子 View 大小
     * 3. 拖动图片时：移动选中图片，并通过触摸坐标查找选中的子 View，并绘制选中子 View 边框
     *
     * [MotionEvent.ACTION_UP],[MotionEvent.ACTION_POINTER_UP]:
     * 1. 还原透明度和 scaleType，并将拖动子 View 恢复到原位
     * 2. 如果是调整大小：需要额外进行对当前拼图的位置生成新的模板，并保存到[template]
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val offsetX = (pressedX - x).roundToInt()
        val offsetY = (pressedY - y).roundToInt()
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                // 双指按下，不处理
                if (isScaled) {
                    return true
                }
                // 调整图片大小
                if (isAdjusterBorder) {
                    when (adjustBorder) {
                        BORDER_LEFT, BORDER_RIGHT -> {
                            if (limitSizeViewChange(adjustBorder, lastMoveX - x, event)) {
                                lastMoveX = event.x
                                lastMoveY = event.y
                                isLimitAdjusterBorder = true
                                return true
                            }
                            for (i in abovePuzzleImageViews.indices) {
                                val view = abovePuzzleImageViews[i]
                                val rect = abovePuzzleImageViewsRect[i]
                                view.layout(rect.left, rect.top, rect.right - offsetX, rect.bottom)
                            }
                            for (i in underPuzzleImageViews.indices) {
                                val view = underPuzzleImageViews[i]
                                val rect = underPuzzleImageViewsRect[i]
                                view.layout(rect.left - offsetX, rect.top, rect.right, rect.bottom)
                            }
                        }
                        BORDER_TOP, BORDER_BOTTOM -> {
                            if (limitSizeViewChange(adjustBorder, (lastMoveY - y), event)) {
                                lastMoveX = event.x
                                lastMoveY = event.y
                                isLimitAdjusterBorder = true
                                return true
                            }
                            for (i in abovePuzzleImageViews.indices) {
                                val view = abovePuzzleImageViews[i]
                                val rect = abovePuzzleImageViewsRect[i]
                                view.layout(rect.left, rect.top, rect.right, rect.bottom - offsetY)
                            }
                            for (i in underPuzzleImageViews.indices) {
                                val view = underPuzzleImageViews[i]
                                val rect = underPuzzleImageViewsRect[i]
                                view.layout(rect.left, rect.top - offsetY, rect.right, rect.bottom)
                            }
                        }
                    }
                    // 拖动交换图片位置
                } else {
                    // 修改透明度
                    if (sourceImageView.alpha != 0.7F) {
                        sourceImageView.alpha = 0.7F
                        sourceImageView.center(PuzzleImageView.CENTER_INSIDE)
                        onHideUtilsListener()
                    }
                    // 防止闪烁
                    if (sourceImageView.visibility == View.INVISIBLE) {
                        sourceImageView.visibility = View.VISIBLE
                    }
                    // 移动选中的View
                    sourceImageView.layout(
                        exchangeSourceLeft - offsetX, exchangeSourceTop - offsetY,
                        exchangeSourceRight - offsetX, exchangeSourceBottom - offsetY
                    )
                    // 拖动回原来位置时，清空选中
                    if (y.roundToInt() in exchangeSourceTop..exchangeSourceBottom &&
                        x.roundToInt() in exchangeSourceLeft..exchangeSourceRight
                    ) {
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
                lastMoveX = event.x
                lastMoveY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                // 抬起后，还原透明度，交换图片
                sourceImageView.alpha = 1.0F
                if (sourceImageView.visibility == View.INVISIBLE) {
                    sourceImageView.visibility = View.VISIBLE
                }
                clearAllImageViewSelectBorder()
                showGuildLine = false
                invalidate()
                if (isScaled) {
                    isScaled = false
                    return true
                }
                isLimitAdjusterBorder = false
                // 抬起后调整大小
                if (isAdjusterBorder) {
                    isAdjusterBorder = false
                    clearAllImageViewSelectBorder()

                    // 重新生成新的模板，并替换当前模板
                    val infoList = mutableListOf<TemplateInfo>()
                    proportion = 1.0
                    for (i in 0 until childCount) {
                        val view = getChildAt(i)
                        val templateRight = if (view.right != width - frameSize) {
                                               view.right
                                           } else {
                                               width
                                           }
                        val templateBottom = if (view.bottom != height - frameSize) {
                                               view.bottom
                                           } else {
                                               height
                                           }
                        infoList.add(
                            TemplateInfo(
                                view.left - frameSize,
                                view.top - frameSize,
                                templateRight,
                                templateBottom
                            )
                        )
                    }
                    template = Template(
                        template.imageCount,
                        height,
                        width,
                        template.templateThumbnail,
                        infoList
                    )
                    return true
                }
                // 复原被拖动的位置
                sourceImageView.layout(
                    exchangeSourceLeft, exchangeSourceTop,
                    exchangeSourceRight, exchangeSourceBottom
                )
                isAdjusterBorder = false
                // 交换图片
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
     * 判断滑动调整是否已经达到最小宽高的阈值
     *
     * @param offset  > 0 :向左，或向上
     * @param adjustBorder View移动的边框 [BORDER_TOP], [BORDER_BOTTOM], [BORDER_LEFT], [BORDER_RIGHT]
     * @return 是否需要停止大小调整
     */
    private fun limitSizeViewChange(adjustBorder: Int, offset: Float, event: MotionEvent): Boolean {
//        val x = event.x.roundToInt()
//        val y = event.y.roundToInt()
//        // 当前滑动的方向，大于 0 时, 表示向左或向上滑动
//        val move = if (abs(offset) > 2) {
//            offset >= 0
//        } else {
//            isMoveToStart
//        }
//        // 限制滑动
//        val limitedByPosition = when(adjustBorder) {
//            BORDER_TOP -> y > exchangeSourceBottom - minPuzzleImageViewSize
//            BORDER_BOTTOM -> y < exchangeSourceTop + minPuzzleImageViewSize
//            BORDER_LEFT -> x < exchangeSourceRight - minPuzzleImageViewSize
//            BORDER_RIGHT -> x < exchangeSourceLeft + minPuzzleImageViewSize
//            else -> false
//        }
//        Log.e("kkl", "$isMoveToStart | $move | $limitedByPosition | $y : $exchangeSourceTop..$exchangeSourceBottom | $x : $exchangeSourceLeft..$exchangeSourceRight | $minPuzzleImageViewSize")
//        if (isMoveToStart != move && !limitedByPosition) {
//            isLimitAdjusterBorder = false
//            isMoveToStart = move
//        }
        if (isLimitAdjusterBorder ) {
            return true
        }
        when (adjustBorder) {
            BORDER_LEFT, BORDER_RIGHT -> {
                if (checkLimitSize(false, offset)) {
                    return true
                }
            }
            BORDER_TOP, BORDER_BOTTOM -> {
                if (checkLimitSize(true, offset)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 校验调整后是否有子 View 的宽高会小于最小值[minPuzzleImageViewSize]
     * @param isVerticalAdjust true：调整边框为[BORDER_BOTTOM]或[BORDER_TOP]; false: 调整边框为[BORDER_LEFT]或[BORDER_RIGHT]
     */
    private fun checkLimitSize(isVerticalAdjust: Boolean, offset: Float): Boolean {
        if (offset > 0) {
            for (view in abovePuzzleImageViews) {
                val rectSize = if (isVerticalAdjust) {
                                view.height - offset
                            } else {
                                view.width - offset
                            }
                if (rectSize <= minPuzzleImageViewSize) {
                    return true
                }
            }
        } else {
            for (view in underPuzzleImageViews) {
                val rectSize = if (isVerticalAdjust) {
                                view.height + offset
                            } else {
                                view.width + offset
                            }
                if (rectSize <= minPuzzleImageViewSize) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 交换两个View中的图片
     */
    private fun exchangeBitmaps() {
        sourceImageView.scaleType = ScaleType.MATRIX
        // 输入图片只有一张时，取消交换
        if (bitmapList.size <= 1) {
            sourceImageView.center(PuzzleImageView.CENTER_CROP)
            destinationImageView.center(PuzzleImageView.CENTER_CROP)
            return
        }
        // 拖动到原来位置时，取消交换
        if (sourceIndex == -1 || destinationIndex == -1) {
            sourceImageView.center(PuzzleImageView.CENTER_CROP)
            return
        }
        // 进行交换
        val temp = bitmapList[sourceIndex]
        bitmapList[sourceIndex] = bitmapList[destinationIndex]
        bitmapList[destinationIndex] = temp
        (getChildAt(sourceIndex) as PuzzleImageView).setImageBitmap(bitmapList[sourceIndex])
        (getChildAt(destinationIndex) as PuzzleImageView).setImageBitmap(bitmapList[destinationIndex])
        onImageExchangeListener(sourceIndex, destinationIndex)
        // 交换完成后，自适应缩放，并居中
        sourceImageView.center(PuzzleImageView.CENTER_CROP)
        destinationImageView.center(PuzzleImageView.CENTER_CROP)
        // 恢复默认值
        destinationImageView = defaultPuzzleImageView
        sourceImageView = defaultPuzzleImageView
        sourceIndex = -1
        destinationIndex = -1
    }

    /**
     * 根据模板中的图片数量，将相应的 View ，加入 Layout 中并设置对应图片
     * @param bitmaps    输入图片解析后的 bitmap
     * @param imageCount 当前拼图模板需要的图片数量，若 [imageCount] > [bitmaps.size] 则选择第一张图片
     */
    fun initViews(bitmaps: List<Bitmap>, imageCount: Int) {
        removeAllViews()
        bitmapList.clear()
        bitmapList.addAll(bitmaps)
        for (i in 0 until imageCount) {
            val view = puzzleImageViews[i]
            val index = if (i >= bitmapList.size) {
                0
            } else {
                i
            }
            view.setImageBitmap(bitmapList[index])
            view.scaleType = ScaleType.MATRIX
            // tag 是否是更换图片的 View【更换图片后需要重置缩放状态】
            view.tag = false
            addView(view)
        }
    }

    /**
     * 添加边框效果
     * @param frame [FRAME_NONE] : 无边框; [FRAME_SMALL] : 小边框;
     *              [FRAME_MEDIUM] : 中边框; [FRAME_LARGE] : 大边框
     */
    fun updateFrameSize(frame: Int) {
        frameSize = frame.dp2px()
        requestLayout()
        invalidate()
    }

    /**
     * 清空所有子View的选中标记
     */
    fun clearAllImageViewSelectBorder() {
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view is PuzzleImageView) {
                view.showBorder(false)
            }
        }
    }

}
/**
 * 图片点击回调
 */
typealias OnImageClickListener = (index: Int, view: PuzzleImageView) -> Unit

/**
 * 隐藏工具栏回调
 */
typealias OnHideUtilsListener = () -> Unit

/**
 * 拖动交换图片回调
 */
typealias OnImageExchangeListener = (from: Int, to: Int) -> Unit