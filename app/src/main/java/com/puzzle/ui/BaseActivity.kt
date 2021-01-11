package com.puzzle.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.puzzle.coroutine.XXMainScope
import kotlinx.coroutines.cancel

open class BaseActivity: AppCompatActivity() {
    protected val mainScope = XXMainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}