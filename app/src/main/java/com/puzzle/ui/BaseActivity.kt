package com.puzzle.ui

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.puzzle.PuzzleApplication
import com.puzzle.coroutine.XXMainScope
import kotlinx.coroutines.cancel

open class BaseActivity : AppCompatActivity() {
    protected val mainScope = XXMainScope()
    private var toast: Toast = Toast.makeText(PuzzleApplication.appContext, "", Toast.LENGTH_SHORT)

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    protected fun showToast(message: String) {
        toast.setText(message)
        toast.show()
    }
}