package com.puzzle

import android.app.Application
import android.content.Context

/**
 * 拼图demo全局application
 */
class PuzzleApplication:Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}