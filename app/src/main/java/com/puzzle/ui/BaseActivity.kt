package com.puzzle.ui

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.puzzle.app
import com.puzzle.coroutine.XXMainScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity(),
    CoroutineScope by XXMainScope() {

    private var toast: Toast = Toast.makeText(app, "", Toast.LENGTH_SHORT)

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    /**
     * 使用同一个 Toast 进行消息提醒，避免连续显示 Toast 时不能取消上一次 Toast 消息
     * 自动切换到主线程中
     */
    protected fun showToast(message: String) {
        launch {
            toast.setText(message)
            toast.show()
        }
    }
}