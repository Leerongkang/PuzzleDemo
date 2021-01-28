package com.puzzle

data class Template(
    val imageCount: Int,
    val totalHeight: Int,
    val totalWidth: Int,
    val templateThumbnail: String,
    val templates: List<TemplateInfo>
)

data class TemplateInfo(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)