package com.puzzle

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lrk.puzzle.demo.databinding.ActivitySuccessBinding

class SuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imagePath = intent.getParcelableExtra<Uri>("savedUri")
        binding.imagePathTextView.text = imagePath.toString()
        binding.previewImageView.setImageURI(imagePath)
        binding.backImageView.setOnClickListener {
            finish()
        }
    }
}