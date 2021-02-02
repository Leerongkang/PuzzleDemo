package com.puzzle.material

/**
 * 用于保存整个拼图模板的数据
 */
data class Template(
    val imageCount: Int = 0,                        // 该模板的图片数量
    val totalHeight: Int = 0,                       // 该模板的总高度
    val totalWidth: Int = 0,                        // 模板的总宽度
    val templateThumbnail: String = "",             // 模板的缩略图路径
    val templates: List<TemplateInfo> = emptyList() // 模板中每全部图片的位置信息
)

/**
 * 用于保存单张拼图的位置信息
 */
data class TemplateInfo(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0
)