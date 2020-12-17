package com.puzzle

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lrk.puzzle.demo.R
import kotlinx.android.synthetic.main.activity_success.*

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        val imagePath = intent.getParcelableExtra<Uri>("savedUri")
        imagePathTextView.text = imagePath.toString()
        previewImageView.setImageURI(imagePath)
        backImageView.setOnClickListener {
            finish()
        }
    }
}