package com.puzzle.ui

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.puzzle.app
import com.puzzle.coroutine.XXMainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    private val mainScope = XXMainScope()
    private var toast: Toast = Toast.makeText(app, "", Toast.LENGTH_SHORT)

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    /**
     * 使用同一个 Toast 进行消息提醒，避免连续显示 Toast 时不能取消上一次 Toast 消息
     */
    protected fun showToast(message: String) {
        toast.setText(message)
        toast.show()
    }

    /**
     * 简化协程的启动方式，并将协程与生命周期关联
     */
    protected fun launch(block: suspend () -> Unit) {
        mainScope.launch {
            block()
        }
    }
}