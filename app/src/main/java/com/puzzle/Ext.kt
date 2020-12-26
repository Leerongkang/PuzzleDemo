package com.puzzle

/**
 * Int的扩展方法，转换dp到px
 */
fun Int.dp2px() = (this * PuzzleApplication.appContext.resources.displayMetrics.density + 0.5f).toInt()
