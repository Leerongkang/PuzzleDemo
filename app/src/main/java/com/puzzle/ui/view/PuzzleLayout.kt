package com.puzzle.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import com.puzzle.R
import com.puzzle.dp2px
import com.puzzle.template.Template
import com.puzzle.template.TemplateInfo
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
    private val moveBorderThreshold = 15.dp2px()

    // 开始一次新的触摸事件流
    private var newSelect = false
    private var isReplacing = false

    // 当前
    private var isAdjusterBorder = false
    private var isLimitAdjusterBorder = false
    private var isMoveToStart = true
    // 当前选中边框
    private var adjustBorder = BORDER_DEFAULT

    //动态调整线上（左）方的View
    private val abovePuzzleImageViews = mutableListOf<PuzzleImageView>()
    private val abovePuzzleImageViewsRect = mutableListOf<Rect>()

    //动态调整线下（右）方的View
    private val underPuzzleImageViews = mutableListOf<PuzzleImageView>()
    private val underPuzzleImageViewsRect = mutableListOf<Rect>()

    // 动态调整时，图片宽高最小限制
    private val minPuzzleImageViewSize = 60.dp2px()

    // 拼图输入图片
    private val bitmapList = mutableListOf<Bitmap>()

    // 对展示图片的View进行复用
    private val puzzleImageViews = List(9) { index ->
        PuzzleImageView(context).apply {
            scaleType = ScaleType.CENTER_CROP
            setOnClickListener {
                puzzleImageViewOnClickListener(index, it as PuzzleImageView)
            }
        }
    }

    // 默认的子View，不显示，用于逻辑判断
    private val defaultPuzzleImageView = PuzzleImageView(context)
    private var sourceImageView: PuzzleImageView = defaultPuzzleImageView
    private var destinationImageView: PuzzleImageView = defaultPuzzleImageView

    //监听
    var onHideUtilsListener: OnHideUtilsListener = {}
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

    /**
     * 绘制辅助线
     */
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

    /**
     * 通过模板[template]，摆放PuzzleImageView到对应的位置
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val templateInfoList = template.templates
        for (i in templateInfoList.indices) {
            val imageView = getChildAt(i)
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
            imageView?.layout(
                viewLeft,
                viewTop,
                viewRight,
                viewBottom
            )
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
        when (event.action) {
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
                val offsetX = abs(pressedX - event.x)
                val offsetY = abs(pressedY - event.y)
                lastMoveX = event.x
                lastMoveY = event.y
                // X轴或Y轴偏移量大于 moveThreshold 时才进行拦截
                val exchange = offsetX > moveThreshold || offsetY > moveThreshold
                // 动态调整左边界，拦截
                val leftRange = (exchangeSourceLeft - moveBorderThreshold)..(exchangeSourceLeft + moveBorderThreshold)
                if (exchange && exchangeSourceLeft != frameSize && pressedX.roundToInt() in leftRange) {
                    for (i in 0 until childCount){
                        val view = getChildAt(i) as PuzzleImageView
                        if (exchangeSourceLeft == view.left){
                            underPuzzleImageViews.add(view)
                            underPuzzleImageViewsRect.add(Rect(view.left,view.top,view.right,view.bottom))
                        }
                        if (exchangeSourceLeft == view.right + frameSize){
                            abovePuzzleImageViews.add(view)
                            abovePuzzleImageViewsRect.add(Rect(view.left,view.top,view.right,view.bottom))
                        }
                    }
                    isAdjusterBorder = true
                    adjustBorder = BORDER_LEFT
                    onHideUtilsListener()
                    return true
                }
                // 动态调整右边界，拦截
                val rightRange = (exchangeSourceRight - moveBorderThreshold)..(exchangeSourceRight + moveBorderThreshold)
                if (exchange && exchangeSourceRight != (width - frameSize) && pressedX.roundToInt() in rightRange) {
                    for (i in 0 until childCount){
                        val view = getChildAt(i) as PuzzleImageView
                        if (exchangeSourceRight == view.right){
                            abovePuzzleImageViews.add(view)
                            abovePuzzleImageViewsRect.add(Rect(view.left,view.top,view.right,view.bottom))
                        }
                        if (exchangeSourceRight == view.left - frameSize){
                            underPuzzleImageViews.add(view)
                            underPuzzleImageViewsRect.add(Rect(view.left,view.top,view.right,view.bottom))
                        }
                    }
                    isAdjusterBorder = true
                    adjustBorder = BORDER_RIGHT
                    onHideUtilsListener()
                    return true
                }
                // 动态调整上边界，拦截
                val topRange = (exchangeSourceTop - moveBorderThreshold)..(exchangeSourceTop + moveBorderThreshold)
                if (exchange && exchangeSourceTop != frameSize && pressedY.roundToInt() in topRange) {
                    for (i in 0 until childCount){
                        val view = getChildAt(i) as PuzzleImageView
                        // 边界下面的View
                        if (exchangeSourceTop == view.top){
                            underPuzzleImageViews.add(view)
                            underPuzzleImageViewsRect.add(Rect(view.left,view.top,view.right,view.bottom))
                        }
                        // 边界上面的View
                        if (exchangeSourceTop == view.bottom + frameSize){
                            abovePuzzleImageViews.add(view)
                            abovePuzzleImageViewsRect.add(Rect(view.left,view.top,view.right,view.bottom))
                        }
                    }
                    isAdjusterBorder = true
                    adjustBorder = BORDER_TOP
                    onHideUtilsListener()
                    return true
                }
                // 动态调整下边界，拦截
                val bottomRange = (exchangeSourceBottom - moveBorderThreshold)..(exchangeSourceBottom + moveBorderThreshold)
                if (exchange && exchangeSourceBottom != (height - frameSize) && pressedY.roundToInt() in bottomRange) {
                    for (i in 0 until childCount){
                        val view = getChildAt(i) as PuzzleImageView
                        // 边界上面的View
                        if (exchangeSourceBottom == view.bottom){
                            abovePuzzleImageViews.add(view)
                            abovePuzzleImageViewsRect.add(Rect(view.left,view.top,view.right,view.bottom))
                        }
                        // 边界下面的View
                        if (exchangeSourceBottom == view.top - frameSize){
                            underPuzzleImageViews.add(view)
                            underPuzzleImageViewsRect.add(Rect(view.left,view.top,view.right,view.bottom))
                        }
                    }
                    isAdjusterBorder = true
                    adjustBorder = BORDER_BOTTOM
                    onHideUtilsListener()
                    return true
                }
                // 拦截拖动更换图片
                val inOther = x !in (exchangeSourceLeft..exchangeSourceRight) ||
                        y !in (exchangeSourceTop..exchangeSourceBottom)
                if (inOther && newSelect) {
                    sourceImageView.alpha = 0.7F
                    sourceImageView.scaleType = ScaleType.CENTER_INSIDE
                    newSelect = false
                    isReplacing = true
                    adjustBorder = BORDER_DEFAULT
                    onHideUtilsListener()
                }
                return inOther
            }
        }
        adjustBorder = BORDER_DEFAULT
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val offsetX = (pressedX - x).roundToInt()
        val offsetY = (pressedY - y).roundToInt()
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (isAdjusterBorder) {
//                    for (v in abovePuzzleImageViews){
//                        v.showBorder(true)
//                    }
//                    for (v in underPuzzleImageViews){
//                        v.showBorder(true)
//                    }
                    when (adjustBorder) {
                        BORDER_LEFT, BORDER_RIGHT -> {
                            if (limitSizeViewChange(adjustBorder,(lastMoveX-x).roundToInt())) {
                                lastMoveX = event.x
                                lastMoveY = event.y
                                isLimitAdjusterBorder = true
                                return true
                            }
                            for (i in abovePuzzleImageViews.indices){
                                val view = abovePuzzleImageViews[i]
                                val rect = abovePuzzleImageViewsRect[i]
                                view.layout(rect.left,rect.top,rect.right-offsetX,rect.bottom)
                            }
                            for (i in underPuzzleImageViews.indices){
                                val view = underPuzzleImageViews[i]
                                val rect = underPuzzleImageViewsRect[i]
                                view.layout(rect.left-offsetX,rect.top,rect.right,rect.bottom)
                            }
                        }
                        BORDER_TOP, BORDER_BOTTOM -> {
                            if (limitSizeViewChange(adjustBorder,(lastMoveY-y).roundToInt())) {
                                lastMoveX = event.x
                                lastMoveY = event.y
                                isLimitAdjusterBorder = true
                                return true
                            }
                            for (i in abovePuzzleImageViews.indices){
                                val view = abovePuzzleImageViews[i]
                                val rect = abovePuzzleImageViewsRect[i]
                                view.layout(rect.left,rect.top,rect.right,rect.bottom - offsetY)
                            }
                            for (i in underPuzzleImageViews.indices){
                                val view = underPuzzleImageViews[i]
                                val rect = underPuzzleImageViewsRect[i]
                                view.layout(rect.left,rect.top-offsetY,rect.right,rect.bottom)
                            }
                        }
                    }
                } else {
                    if (sourceImageView.alpha != 0.7F) {
                        sourceImageView.alpha = 0.7F
                        sourceImageView.scaleType = ScaleType.CENTER_INSIDE
                        onHideUtilsListener()
                    }
                    // 移动选中的View
                    sourceImageView.apply {
                        layout(
                            exchangeSourceLeft - offsetX, exchangeSourceTop - offsetY,
                            exchangeSourceRight - offsetX, exchangeSourceBottom - offsetY
                        )
                    }
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
            MotionEvent.ACTION_UP -> {
                isLimitAdjusterBorder = false
                // 抬起后调整大小
                if (isAdjusterBorder){
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
                // 抬起后，还原透明度，交换图片
                sourceImageView.alpha = 1.0F
                clearAllImageViewSelectBorder()
                sourceImageView.scaleType = ScaleType.CENTER_CROP
                sourceImageView.layout(
                    exchangeSourceLeft, exchangeSourceTop,
                    exchangeSourceRight, exchangeSourceBottom
                )
                isReplacing = false
                isAdjusterBorder = false
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
     * @param offset  0 :向左，或向上
     * @param adjustBorder View移动的边框 [BORDER_TOP], [BORDER_BOTTOM], [BORDER_LEFT], [BORDER_RIGHT]
     * @return 是否需要停止大小调整
     */
    private fun limitSizeViewChange(adjustBorder: Int, offset: Int): Boolean {
        val move = offset >= 0
        if (isLimitAdjusterBorder /*&& isMoveToStart == move*/) {
            return true
        }
        isMoveToStart = move
        when (adjustBorder) {
            BORDER_LEFT, BORDER_RIGHT -> {
                if (offset > 0){
                    for (view in abovePuzzleImageViews) {
                        val rectWith = view.width - offset
                        if(rectWith <= minPuzzleImageViewSize) {
                            view.showBorder(true)
                            return true
                        }
                    }
                } else{
                    for (view in underPuzzleImageViews) {
                        val rectWith = view.width + offset
                        if(rectWith <= minPuzzleImageViewSize) {
                            view.showBorder(true)
                            return true
                        }
                    }
                }
            }
            BORDER_TOP, BORDER_BOTTOM -> {
                if (offset > 0) {
                    for (view in abovePuzzleImageViews) {
                        val rectHeight = view.height - offset
                        if(rectHeight <= minPuzzleImageViewSize) {
                            view.showBorder(true)
                            return true
                        }
                    }
                } else {
                    for (view in underPuzzleImageViews) {
                        val rectHeight = view.height + offset
                        if(rectHeight <= minPuzzleImageViewSize) {
                            view.showBorder(true)
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    /**
     * 交换两个View中的图片
     */
    private fun exchangeBitmaps() {
        if (bitmapList.size <= 1) {
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
        for (i in 0..childCount) {
            val view = getChildAt(i)
            if (view is PuzzleImageView) {
                view.showBorder(false)
            }
        }
    }

}

typealias OnImageClickListener = (index: Int, view: PuzzleImageView) -> Unit
typealias OnHideUtilsListener = () -> Unit