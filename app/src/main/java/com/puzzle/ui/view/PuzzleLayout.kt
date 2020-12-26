package com.puzzle.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.puzzle.dp2px
import com.puzzle.template.Template
import com.puzzle.template.TemplateInfo

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
    private var frameSize = 0
    var proportion = 0.0

    init {
        layoutParams = LayoutParams(template.totalWidth, template.totalHeight)
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

    fun initViews(bitmapList: List<Bitmap>, imageCount: Int) {
        removeAllViews()
        for (i in 0 until imageCount) {
            val view = ImageView(context).apply {
                scaleType = ScaleType.CENTER_CROP
                val index = if (i >= bitmapList.size) {
                    0
                } else {
                    i
                }
                setImageBitmap(bitmapList[index])
            }
            addView(view)
        }
    }

    fun updateFrameSize(frame: Int) {
        frameSize = frame.dp2px()
        requestLayout()
        invalidate()
    }
}