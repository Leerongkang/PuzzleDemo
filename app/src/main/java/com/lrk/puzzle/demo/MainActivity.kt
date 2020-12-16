package com.lrk.puzzle.demo

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.lrk.puzzle.demo.adappter.TemplateAdapter
import com.lrk.puzzle.demo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var showTemplate = true
    private var shouldUpdateTabLayout = false
    private var currentFrameMode = R.drawable.meitu_puzzle__frame_none to "无边框"
    private var images = emptyList<String>()
    private var selectNum = 1
    private val template2CategoryMap = TemplateData.templateInCategory(selectNum)
    private val fistTemplateInCategoryMap = TemplateData.templateCategoryFirst(selectNum)
    private val templateRecyclerViewLayoutManager = LinearLayoutManager(this).apply {
        orientation = LinearLayoutManager.HORIZONTAL
    }
    private val templateAdapter = TemplateAdapter(
        this@MainActivity,
        TemplateData.allTemplateWithPictureNum(selectNum)
    ).apply {
        setOnTemplateSelectListener {
            binding.templateGroup.templateTabLayout.setScrollPosition(
                template2CategoryMap[it.adapterPosition] ?: 0,
                0f,
                false
            )
            select = it.adapterPosition
            notifyDataSetChanged()
        }
    }

    private var imageHeight = 0
    private var imageWidth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        images = intent.getStringArrayListExtra("images") ?: emptyList()
        selectNum = images.size
        val bitmap = BitmapFactory.decodeFile(images[0])
        imageWidth = bitmap.width
        imageHeight = bitmap.height
        binding.puzzleImageView.setImageBitmap(bitmap)
        initViews()
    }

    private fun initViews() {
        initTitleBar()
        initTemplateViewGroup()
        initBottomTabLayout()
    }

    private fun initTemplateViewGroup() {
        binding.templateGroup.apply {
            templateRecyclerView.apply {
                adapter = templateAdapter
                layoutManager = templateRecyclerViewLayoutManager
                setOnScrollChangeListener { _, _, _, _, _ ->
                    if (shouldUpdateTabLayout) {
                        val lastPos =
                            templateRecyclerViewLayoutManager.findLastVisibleItemPosition()
                        val pos = if (lastPos == templateAdapter.list.size - 1) {
                            5
                        } else {
                            val firstPos =
                                templateRecyclerViewLayoutManager.findFirstVisibleItemPosition()
                            template2CategoryMap[firstPos] ?: 0
                        }
                        templateTabLayout.setScrollPosition(
                            pos, 0F, false
                        )
                    }
                    shouldUpdateTabLayout = true
                }
            }
            templateTabLayout.apply {
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_34))
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_11))
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_43))
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_169))
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_full))
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_others))
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        shouldUpdateTabLayout = false
                        val categoryPos = (tab?.position ?: 0)
                        templateRecyclerViewLayoutManager.scrollToPositionWithOffset(
                            fistTemplateInCategoryMap[categoryPos] ?: 0,
                            0
                        )
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }
                })
            }
            frameTextView.apply {
                val drawable = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.meitu_puzzle__frame_none
                )?.apply {
                    setBounds(0, 0, dp2px(40), dp2px(40))
                }
                setCompoundDrawables(null, drawable, null, null)
                setOnClickListener {
                    updateFrameMode()
                }
            }
            binding.closeImageView.apply {
                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        showTemplate = !showTemplate
                        val res = if (showTemplate) {
                            R.drawable.ic_down
                        } else {
                            R.drawable.ic_up
                        }
                        performClick()
                        setImageResource(res)
                    }
                    true
                }
            }
        }

    }

    private fun initTitleBar() {
        binding.titleBar.apply {
            backImageView.setOnClickListener {
                finish()
            }
            finishImageView.setOnClickListener {
                saveBitmap(binding.puzzleImageView, System.currentTimeMillis().toString())
            }
        }
    }

    private fun initBottomTabLayout() {
        binding.bottomTabLayout.apply {
            addTab(newTab().setText("模板"))
            addTab(newTab().setText("海报"))
            addTab(newTab().setText("自由"))
            addTab(newTab().setText("拼接"))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    binding.closeImageView.performClick()
                }
            })
        }
    }

    private fun saveBitmap(view: View, fileName: String) {
        val bitmap = view2bitmap(view)
        GlobalScope.launch {
            val savedUri = saveLocal(fileName, bitmap)
            withContext(Dispatchers.Main) {
                if (savedUri != null) {
                    startActivity(Intent(this@MainActivity, SuccessActivity::class.java).apply {
                        putExtra("savedUri", savedUri)
                    })
                } else {
                    Toast.makeText(this@MainActivity, "保存失败", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun view2bitmap(view: View): Bitmap {
        var height = view.height
        var width = view.width
        Log.e("kkl", "width $width height $height")
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
//        view.layout(0, dp2px(60), width, height)
//        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        Log.e("kkl", "width ${bitmap.width} height ${bitmap.height}")
        return bitmap
    }

    private suspend fun saveLocal(fileName: String, bitmap: Bitmap): Uri? {
        var imagePath: Uri? = null
        withContext(Dispatchers.IO) {
            try {
                val contentValues = ContentValues()
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM")
                } else {
                    contentValues.put(
                        MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        ).path
                    )
                }
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                val uri =
                    contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                if (uri != null) {
                    contentResolver.openOutputStream(uri)?.use {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, it)
                    }
                    imagePath = uri
                }
            } catch (e: Exception) {
            }
        }
        return imagePath
    }

    private fun updateFrameMode() {
        currentFrameMode = when (currentFrameMode.first) {
            R.drawable.meitu_puzzle__frame_none -> R.drawable.meitu_puzzle__frame_small to "小边框"
            R.drawable.meitu_puzzle__frame_small -> R.drawable.meitu_puzzle__frame_medium to "中边框"
            R.drawable.meitu_puzzle__frame_medium -> R.drawable.meitu_puzzle__frame_large to "大边框"
            else -> R.drawable.meitu_puzzle__frame_none to "无边框"
        }
        binding.templateGroup.frameTextView.apply {
            text = currentFrameMode.second
            setCompoundDrawables(
                null,
                ContextCompat.getDrawable(this@MainActivity, currentFrameMode.first)?.apply {
                    setBounds(0, 0, dp2px(40), dp2px(40))
                }, null, null
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.invalidate()
    }

    private fun dp2px(dp: Int) = (dp * resources.displayMetrics.density + 0.5f).toInt()

}