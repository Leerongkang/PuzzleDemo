package com.puzzle.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.permissionx.guolindev.PermissionX
import com.puzzle.R
import com.puzzle.adappter.ImageAdapter
import kotlinx.android.synthetic.main.activity_image_select.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 图片选择Activity
 * 通过接受Intent参数判断是单选还是多选，
 * intent.getBooleanExtra(INTENT_EXTRA_REPLACE,false)
 * true：  多选，可以选择1~9张
 * false： 单选，只能选择一张
 */
class ImageSelectActivity : BaseActivity() {

    private var isReplaceImage = false
    private val selectImages = ArrayList<String>()
    private val selectedAdapter = ImageAdapter(selectImages, true) { adapter, pos ->
        selectImages.removeAt(pos)
        adapter.notifyItemRemoved(pos)
        updateSelectNum()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_select)
        isReplaceImage = intent.getBooleanExtra(INTENT_EXTRA_REPLACE,false)
        requestPermission()
        initViews()
    }

    private fun initAllImageRecyclerView(){
        mainScope.launch {
            selectLoadingAnimateView.playAnimation()
            selectLoadingAnimateView.alpha = 1F
            val localImages = getLocalImages()
            if (isReplaceImage) {
                imageSelectedRecyclerView.visibility = View.GONE
                tipsTextView.visibility = View.GONE
                selectNumTextView.visibility = View.GONE
                doneTextView.visibility = View.GONE
                allImageRecyclerView.adapter = ImageAdapter(localImages) { adapter, pos ->
                    intent.putExtra(INTENT_EXTRA_REPLACE_DATA, adapter.imageList[pos])
                    setResult(INTENT_REQUEST_CODE_REPLACE_IMAGE, intent)
                    finish()
                }
            } else {
                allImageRecyclerView.adapter = ImageAdapter(localImages) { adapter, pos ->
                    if (selectImages.size < 9) {
                        selectImages.add(adapter.imageList[pos])
                        selectedAdapter.notifyItemInserted(selectImages.size - 1)
                        updateSelectNum()
                        imageSelectedRecyclerView.scrollToPosition(selectImages.size - 1)
                    } else {
                        Toast.makeText(
                            this@ImageSelectActivity,
                            getString(R.string.select_limit_tips),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            allImageRecyclerView.layoutManager = GridLayoutManager(this@ImageSelectActivity, 4)
            selectLoadingAnimateView.alpha = 0F
        }
    }

    private fun initViews() {
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
                    putStringArrayListExtra(
                        getString(R.string.intent_extra_selected_images),
                        selectImages
                    )
                })
            }
        }
        exitImageView.setOnClickListener {
            finish()
        }
    }

    private fun updateSelectNum() {
        when (selectImages.size) {
            0 -> {
                doneTextView.setBackgroundColor(getColor(R.color.disabled))
                selectNumTextView.visibility = View.INVISIBLE
            }
            else -> {
                doneTextView.setBackgroundColor(getColor(R.color.main))
                selectNumTextView.visibility = View.VISIBLE
                selectNumTextView.text = selectImages.size.toString()
            }
        }
    }

    private suspend fun getLocalImages(): List<String> = withContext(Dispatchers.IO) {
        val images = mutableListOf<String>()
        val imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(
            imagesUri,
            null,
            "${MediaStore.Images.Media.MIME_TYPE}=? or ${MediaStore.Images.Media.MIME_TYPE}=?",
            arrayOf(getString(R.string.mime_type_jpeg), getString(R.string.mime_type_png)),
            "${MediaStore.Images.Media.DATE_MODIFIED} desc"
        )
        cursor?.let {
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                images.add(path)
            }
            cursor.close()
        }
        images
    }

    private fun requestPermission() {
        PermissionX.init(this).permissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).onExplainRequestReason { scope, deniedList ->
            val message = getString(R.string.permission_tips)
            scope.showRequestReasonDialog(
                deniedList,
                message,
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.request { allGranted, _, deniedList ->
            if (!allGranted) {
                Toast.makeText(
                    this,
                    getString(R.string.denied_permissions, deniedList),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                initAllImageRecyclerView()
            }
        }
    }
}