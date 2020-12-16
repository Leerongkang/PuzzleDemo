package com.lrk.puzzle.demo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lrk.puzzle.demo.adappter.ImageAdapter
import com.lrk.puzzle.demo.adappter.ImageSelectedAdapter
import com.lrk.puzzle.demo.databinding.ActivityImageSelectBinding
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageSelectBinding

    private val selectImages = ArrayList<String>()
    private val selectedAdapter = ImageSelectedAdapter(selectImages).apply {
        setOnSelectedListener { _, pos ->
            selectImages.removeAt(pos)
            notifyDataSetChanged()
            updateSelectNum()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        requestPermission()
        GlobalScope.launch(Dispatchers.Main) {
            val localImages = getLocalImages()
            binding.allImageRecyclerView.apply {
                adapter = ImageAdapter(localImages).apply {
                    setOnSelectedListener { path, _ ->
                        if (selectImages.size < 9) {
                            selectImages.add(path)
                            selectedAdapter.notifyDataSetChanged()
                            updateSelectNum()
                        } else {
                            Toast.makeText(this@ImageSelectActivity, "最多选择9张", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                layoutManager = GridLayoutManager(this@ImageSelectActivity, 4)
                setItemViewCacheSize(100)
            }
        }
    }

    private fun initViews() {
        binding.apply {
            imageSelectedRecyclerView.apply {
                adapter = selectedAdapter
                layoutManager = LinearLayoutManager(
                    this@ImageSelectActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }
            doneTextView.setOnClickListener {
                if (selectImages.size > 0) {
                    startActivity(Intent(this@ImageSelectActivity, MainActivity::class.java).apply {
                        putStringArrayListExtra("images", selectImages)
                    })
                }
            }
        }
    }


    private fun updateSelectNum() {
        when (selectImages.size) {
            0 -> binding.apply {
                doneTextView.setBackgroundColor(getColor(R.color.disabled))
                selectNumTextView.visibility = View.INVISIBLE
            }

            else -> binding.apply {
                doneTextView.setBackgroundColor(getColor(R.color.main))
                selectNumTextView.visibility = View.VISIBLE
                selectNumTextView.text = selectImages.size.toString()
            }
        }
    }

    private suspend fun getLocalImages(): List<String> {
        val images = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            val imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val cursor = contentResolver.query(
                imagesUri,
                null,
                "${MediaStore.Images.Media.MIME_TYPE}=? or ${MediaStore.Images.Media.MIME_TYPE}=?",
                arrayOf("image/jpeg", "image/png"),
                MediaStore.Images.Media.DATE_MODIFIED
            )
            cursor?.let {
                while (cursor.moveToNext()) {
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    images.add(path)
                }
                cursor.close()
            }
        }
        return images
    }

    private fun requestPermission() {
        PermissionX.init(this).permissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).onExplainRequestReason { scope, deniedList ->
            val message = "TryMusic 需要您同意以下权限才能正常使用"
            scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
        }.request { allGranted, _, deniedList ->
            if (!allGranted) {
                Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
            }
        }
    }
}