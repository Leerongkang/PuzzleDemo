package com.puzzle

import android.content.Context

fun Int.dp2px(context: Context) = (this * context.resources.displayMetrics.density + 0.5f).toInt()
