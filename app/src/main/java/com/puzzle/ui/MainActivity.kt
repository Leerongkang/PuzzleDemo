package com.puzzle.ui

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.lrk.puzzle.demo.R
import com.puzzle.Template
import com.puzzle.TemplateData
import com.puzzle.adappter.TemplateAdapter
import com.puzzle.coroutine.XXMainScope
import com.puzzle.dp2px
import com.puzzle.ui.view.PuzzleLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_template.view.*
import kotlinx.android.synthetic.main.layout_title.*
import kotlinx.android.synthetic.main.layout_title.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private val frameIconHeight = 40
    private var showTemplate = true
    private var shouldUpdateTabLayout = false
    private val templateCategoryNum = 6
    private var selectNum = 1
    private var currentFrameMode = 0 to ""
    private var images = emptyList<String>()
    private val template2CategoryMap = mutableMapOf<Int, Int>()
    private val fistTemplateInCategoryMap = mutableMapOf<Int, Int>()
    private val allTemplates = mutableListOf<Template>()
    private val bitmapList = mutableListOf<Bitmap>()
    private var puzzleViewInit = false
    private val templateRecyclerViewLayoutManager = LinearLayoutManager(this).apply {
        orientation = LinearLayoutManager.HORIZONTAL
    }
    private val templateAdapter by lazy {
        TemplateAdapter(
            TemplateData.allTemplateWithPictureNum(selectNum)
        ) { adapter, holder ->
            if (puzzleViewInit) {
                templateGroup.templateTabLayout.setScrollPosition(
                    template2CategoryMap[holder.adapterPosition] ?: 0,
                    0f,
                    false
                )
                adapter.currentSelectPos = holder.adapterPosition
                adapter.notifyItemChanged(adapter.currentSelectPos)
                adapter.notifyItemChanged(adapter.lastSelectedPos)
                puzzleImageView.template = allTemplates[holder.adapterPosition]
                puzzleImageView.initViews(
                    bitmapList,
                    allTemplates[holder.adapterPosition].imageCount
                )
                resizePuzzleLayout()
                puzzleImageView.requestLayout()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        images = intent.getStringArrayListExtra(getString(R.string.intent_extra_selected_images))
            ?: emptyList()
        selectNum = images.size
        loadTemplateData()
        setBitmap()
        initViews()
    }

    private fun loadTemplateData() {
        template2CategoryMap.putAll(TemplateData.templateInCategory(selectNum))
        fistTemplateInCategoryMap.putAll(TemplateData.templateCategoryFirst(selectNum))
        allTemplates.addAll(TemplateData.allTemplateWithNum(selectNum))
    }

    private fun setBitmap() {
        XXMainScope().launch {
            val bitmaps = decodeBitmap(images)
            puzzleImageView.template = allTemplates[0]
            puzzleImageView.initViews(bitmaps, allTemplates[0].imageCount)
            puzzleContainer.post {
                resizePuzzleLayout()
                puzzleViewInit = true
            }
        }
    }

    private fun resizePuzzleLayout() {
        val containerWidth = puzzleContainer.width
        val containerHeight = puzzleContainer.height
        val templateWidth = puzzleImageView.template.totalWidth
        val templateHeight = puzzleImageView.template.totalHeight
        var finalWidth = containerWidth
        var finalHeight =
            (templateHeight * (containerWidth / templateWidth.toDouble())).roundToInt()
        if (finalHeight > containerHeight) {
            finalHeight = containerHeight
            finalWidth =
                (templateWidth * (containerHeight / templateHeight.toDouble())).roundToInt()
        }
        puzzleImageView.proportion = finalHeight / templateHeight.toDouble()
        puzzleImageView.layoutParams =
            FrameLayout.LayoutParams(finalWidth, finalHeight, Gravity.CENTER)
    }

    private suspend fun decodeBitmap(path: List<String>) = withContext(Dispatchers.IO) {
        val bitmaps = mutableListOf<Bitmap>()
        val op = BitmapFactory.Options()
        path.forEach {
            val exifInterface = ExifInterface(it)
            val imageHeight = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)?.toInt()?:0
            val imageWidth =    exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)?.toInt()?:0
            val scalingRatio = if (imageHeight > imageWidth) {imageHeight / 1000} else {imageWidth / 1000}
            op.inSampleSize = scalingRatio
            val decodeBitmap = BitmapFactory.decodeFile(it, op)
            bitmaps.add(decodeBitmap)
        }
        bitmapList.clear()
        bitmapList.addAll(bitmaps)
        bitmaps
    }

    private fun initViews() {
        initTitleBar()
        initTemplateViewGroup()
        initBottomTabLayout()
    }

    private fun initTemplateViewGroup() {
        initTemplateRecyclerView()
        initTemplateTabLayout()
        initFrameModeView()
    }

    private fun initFrameModeView() {
        currentFrameMode = R.drawable.meitu_puzzle__frame_none to getString(R.string.none_frame)
        val drawable = ContextCompat.getDrawable(this, currentFrameMode.first)?.apply {
            setBounds(
                0,
                0,
                frameIconHeight.dp2px(this@MainActivity),
                frameIconHeight.dp2px(this@MainActivity)
            )
        }
        templateGroup.frameTextView.setCompoundDrawables(null, drawable, null, null)
        templateGroup.frameTextView.setOnClickListener {
            updateFrameMode()
        }
    }

    private fun initTemplateTabLayout() {
        templateGroup.templateTabLayout.apply {
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_34))
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_11))
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_43))
            addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_169))
            if (selectNum < 7) {
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_full))
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_others))
            }
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
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (!showTemplate) {
                        showImageView.performClick()
                        showTemplate = !showTemplate
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {
                    showImageView.performClick()
                    showTemplate = !showTemplate
                }
            })
        }
    }

    private fun saveBitmap(view: View, fileName: String) {
        val bitmap = view2bitmap(view)
        XXMainScope().launch {
            val savedUri = saveLocal(fileName, bitmap)
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

    private fun view2bitmap(view: View): Bitmap {
        val height = view.height
        val width = view.width
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
//        view.layout(0, dp2px(60), width, height)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)

        return bitmap
    }

    private suspend fun saveLocal(fileName: String, bitmap: Bitmap): Uri =
        withContext(Dispatchers.IO) {
            var imagePath: Uri = Uri.parse("")
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
                        MediaStore.Images.Media.DATA,
                        Environment.getExternalStoragePublicDirectory(
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
            imagePath
        }

    private fun updateFrameMode() {
        currentFrameMode = when (currentFrameMode.first) {
            R.drawable.meitu_puzzle__frame_none -> {
                puzzleImageView.updateFrameSize(PuzzleLayout.FRAME_SMALL)
                R.drawable.meitu_puzzle__frame_small to getString(R.string.small_frame)
            }
            R.drawable.meitu_puzzle__frame_small -> {
                puzzleImageView.updateFrameSize(PuzzleLayout.FRAME_MEDIUM)
                R.drawable.meitu_puzzle__frame_medium to getString(R.string.medium_frame)
            }
            R.drawable.meitu_puzzle__frame_medium -> {
                puzzleImageView.updateFrameSize(PuzzleLayout.FRAME_LARGE)
                R.drawable.meitu_puzzle__frame_large to getString(R.string.large_frame)
            }
            else -> {
                puzzleImageView.updateFrameSize(PuzzleLayout.FRAME_NONE)
                R.drawable.meitu_puzzle__frame_none to getString(R.string.none_frame)
            }
        }

        templateGroup.frameTextView.text = currentFrameMode.second
        val drawable = ContextCompat.getDrawable(
            this@MainActivity,
            currentFrameMode.first
        )?.apply {
            setBounds(
                0,
                0,
                frameIconHeight.dp2px(this@MainActivity),
                frameIconHeight.dp2px(this@MainActivity)
            )
        }
        templateGroup.frameTextView.setCompoundDrawables(null, drawable, null, null)
    }
}