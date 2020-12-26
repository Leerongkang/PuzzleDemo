package com.puzzle.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.puzzle.R
import kotlinx.android.synthetic.main.activity_success.*

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        val imagePath = intent.getParcelableExtra<Uri>(getString(R.string.intent_extra_saved_uri))
//        imagePathTextView.text = imagePath.toString()
        previewImageView.setImageURI(imagePath)
        backImageView.setOnClickListener {
            finish()
        }
    }
}