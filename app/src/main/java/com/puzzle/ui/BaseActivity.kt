package com.puzzle.ui

import androidx.appcompat.app.AppCompatActivity
import com.puzzle.coroutine.XXMainScope
import kotlinx.coroutines.cancel

open class BaseActivity: AppCompatActivity() {
    protected val mainScope = XXMainScope()

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}