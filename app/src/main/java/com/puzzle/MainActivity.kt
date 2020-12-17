package com.puzzle

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.lrk.puzzle.demo.R
import com.puzzle.adappter.TemplateAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_template.view.*
import kotlinx.android.synthetic.main.layout_title.*
import kotlinx.android.synthetic.main.layout_title.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private val frameIconHeight = 40
    private var showTemplate = true
    private var shouldUpdateTabLayout = false
    private val templateCategoryNum = 6
    private var selectNum = 1
    private var imageHeight = 0
    private var imageWidth = 0
    private var currentFrameMode = 0 to ""
    private var images = emptyList<String>()
    private val template2CategoryMap = TemplateData.templateInCategory(selectNum)
    private val fistTemplateInCategoryMap = TemplateData.templateCategoryFirst(selectNum)
    private val templateRecyclerViewLayoutManager = LinearLayoutManager(this).apply {
        orientation = LinearLayoutManager.HORIZONTAL
    }
    private val templateAdapter by lazy {
        TemplateAdapter(
            TemplateData.allTemplateWithPictureNum(selectNum)
        ) { adapter, holder ->
            templateGroup.templateTabLayout.setScrollPosition(
                template2CategoryMap[holder.adapterPosition] ?: 0,
                0f,
                false
            )
            adapter.selectPosition = holder.adapterPosition
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        images = intent.getStringArrayListExtra(getString(R.string.intent_extra_selected_images))
            ?: emptyList()
        selectNum = images.size
        val bitmap = BitmapFactory.decodeFile(images[0])
        imageWidth = bitmap.width
        imageHeight = bitmap.height
        puzzleImageView.setImageBitmap(bitmap)
        initViews()
    }

    private fun initViews() {
        initTitleBar()
        initTemplateViewGroup()
        initBottomTabLayout()
    }

    private fun initTemplateViewGroup() {
        initTemplateRecyclerView()
        initTemplateTabLayout()
        initCloseImageView()
        initFrameModeView()
    }

    private fun initFrameModeView() {
        currentFrameMode = R.drawable.meitu_puzzle__frame_none to getString(R.string.none_frame)
        val drawable = ContextCompat.getDrawable(this, currentFrameMode.first)?.apply {
            setBounds(0, 0, frameIconHeight.dp2px(), frameIconHeight.dp2px())
        }
        templateGroup.frameTextView.setCompoundDrawables(null, drawable, null, null)
        templateGroup.frameTextView.setOnClickListener {
            updateFrameMode()
        }
    }

    private fun initCloseImageView() {
        closeImageView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showTemplate = !showTemplate
                val res = if (showTemplate) {
                    R.drawable.ic_down
                } else {
                    R.drawable.ic_up
                }
                view.performClick()
                closeImageView.setImageResource(res)
            }
            true
        }
    }

    private fun initTemplateTabLayout() {
        templateGroup.templateTabLayout.apply {

            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_34))
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_11))
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_43))
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_169))
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_full))
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_others))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab) {
                    shouldUpdateTabLayout = false
                    val categoryPos = (tab.position)
                    templateRecyclerViewLayoutManager.scrollToPositionWithOffset(
                        fistTemplateInCategoryMap[categoryPos] ?: 0,
                        0
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    private fun initTemplateRecyclerView() {

        templateGroup.templateRecyclerView.adapter = templateAdapter
        templateGroup.templateRecyclerView.layoutManager = templateRecyclerViewLayoutManager

        templateGroup.templateRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            if (shouldUpdateTabLayout) {
                val lastPos =
                    templateRecyclerViewLayoutManager.findLastVisibleItemPosition()
                val pos = if (lastPos == templateAdapter.list.size - 1) {
                    templateCategoryNum - 1
                } else {
                    val firstPos =
                        templateRecyclerViewLayoutManager.findFirstVisibleItemPosition()
                    template2CategoryMap[firstPos] ?: 0
                }
                templateGroup.templateTabLayout.setScrollPosition(
                    pos, 0F, false
                )
            }
            shouldUpdateTabLayout = true
        }
    }

    private fun initTitleBar() {
        titleBar.backImageView.setOnClickListener {
            finish()
        }
        titleBar.finishImageView.setOnClickListener {
            saveBitmap(puzzleImageView, System.currentTimeMillis().toString())
        }
    }


    private fun initBottomTabLayout() {
        bottomTabLayout.apply {

            addTab(newTab().setText(context.getString(R.string.template)))
            addTab(newTab().setText(context.getString(R.string.poster)))
            addTab(newTab().setText(context.getString(R.string.free)))
            addTab(newTab().setText(context.getString(R.string.splice)))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {
                    closeImageView.performClick()
                }
            })
        }
    }

    private fun saveBitmap(view: View, fileName: String) {
        val bitmap = view2bitmap(view)
        GlobalScope.launch {
            val savedUri = saveLocal(fileName, bitmap)
            withContext(Dispatchers.Main) {
                if (!TextUtils.isEmpty(savedUri.toString())) {
                    startActivity(Intent(this@MainActivity, SuccessActivity::class.java).apply {
                        putExtra(getString(R.string.intent_extra_saved_uri), savedUri)
                    })
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.saved_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun view2bitmap(view: View): Bitmap {
        val height = view.height
        val width = view.width
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
//        view.layout(0, dp2px(60), width, height)
//        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private suspend fun saveLocal(fileName: String, bitmap: Bitmap): Uri {
        var imagePath: Uri = Uri.parse("")
        withContext(Dispatchers.IO) {
            try {
                val contentValues = ContentValues()
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        getString(R.string.dcim)
                    )
                } else {
                    contentValues.put(
                        MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        ).path
                    )
                }
                contentValues.put(
                    MediaStore.Images.Media.MIME_TYPE,
                    getString(R.string.mime_type_jpeg)
                )
                val uri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                if (uri != null) {
                    val outputStream = contentResolver.openOutputStream(uri)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    imagePath = uri
                }
            } catch (e: Exception) {
            }
        }
        return imagePath
    }

    private fun updateFrameMode() {
        currentFrameMode = when (currentFrameMode.first) {
            R.drawable.meitu_puzzle__frame_none ->
                R.drawable.meitu_puzzle__frame_small to getString(R.string.small_frame)
            R.drawable.meitu_puzzle__frame_small ->
                R.drawable.meitu_puzzle__frame_medium to getString(R.string.medium_frame)
            R.drawable.meitu_puzzle__frame_medium ->
                R.drawable.meitu_puzzle__frame_large to getString(R.string.large_frame)
            else -> R.drawable.meitu_puzzle__frame_none to getString(R.string.none_frame)
        }

        templateGroup.frameTextView.text = currentFrameMode.second
        val drawable = ContextCompat.getDrawable(
            this@MainActivity,
            currentFrameMode.first
        )?.apply {
            setBounds(0, 0, frameIconHeight.dp2px(), frameIconHeight.dp2px())
        }
        templateGroup.frameTextView.setCompoundDrawables(null, drawable, null, null)
    }

    private fun Int.dp2px() = (this * resources.displayMetrics.density + 0.5f).toInt()
}