package com.puzzle.ui

import android.net.Uri
import android.os.Bundle
import com.puzzle.R
import kotlinx.android.synthetic.main.activity_success.*

/**
 * 拼图结果Activity
 */
class SuccessActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        val imagePath: Uri? = intent.getParcelableExtra(getString(R.string.intent_extra_saved_uri))
        previewImageView.setImageURI(imagePath)
        backImageView.setOnClickListener {
            finish()
        }
    }
}