package com.puzzle

import android.app.Application
import android.content.Context

/**
 * 拼图demo全局application
 */
lateinit var app: PuzzleApplication
class PuzzleApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        app = this
    }
}