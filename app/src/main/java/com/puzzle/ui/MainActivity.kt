package com.puzzle.ui

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.puzzle.R
import com.puzzle.adappter.TemplateAdapter
import com.puzzle.dp2px
import com.puzzle.template.Template
import com.puzzle.template.TemplateData
import com.puzzle.ui.view.PuzzleImageView
import com.puzzle.ui.view.PuzzleLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_image_replace.view.*
import kotlinx.android.synthetic.main.layout_template.view.*
import kotlinx.android.synthetic.main.layout_title.*
import kotlinx.android.synthetic.main.layout_title.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

/**
 * 拼图Activity
 */
const val INTENT_EXTRA_REPLACE = "isReplaceImage"
const val INTENT_EXTRA_DATA_REPLACE = "image_path"
const val INTENT_REQUEST_CODE_REPLACE_IMAGE = 1

class MainActivity : BaseActivity() {

    // 边框图片尺寸
    private val frameIconSize = 40
    // 图片单词旋转的角度
    private val rotateAngle = 90F
    // 单张图片替换时，该View的下标
    private var selectedImageIndex = -1
    // 输入图片数量，使用Intent传入
    private var selectNum = 1

    // 模板选择部分，是否隐藏标志位
    private val showTemplateGroup: Boolean
        get() = templateGroup?.alpha == 1F

    // 图片调整部分，是否隐藏标志位
    private val showUpdateGroup: Boolean
        get() = imageUpdateGroup?.alpha == 1F

    // 防止模板部分的 TabLayout 与 RecyclerView 联动时，滚动冲突
    private var shouldUpdateTabLayout = false
    private var puzzleViewInit = false
    // 当前的边框模式，边框图标Id to 边框描述，用于界面显示
    private var currentFrameMode = 0 to ""
    // 输入图片路径
    private var images = mutableListOf<String>()
    // 输入图片解析后的Bitmap
    private val bitmapList = mutableListOf<Bitmap>()
    // 用于模板联动的数据结构
    private val template2CategoryMap = mutableMapOf<Int, Int>()
    private val fistTemplateInCategoryMap = mutableMapOf<Int, Int>()
    private val allTemplates = mutableListOf<Template>()
    // 替换图片的View
    private lateinit var selectedImageView: PuzzleImageView
    private val templateRecyclerViewLayoutManager = LinearLayoutManager(this).apply {
        orientation = LinearLayoutManager.HORIZONTAL
    }
    // 带修正的当前选中图片下标，防止 bitmapList 下标越界（当导入图片只有一张时，可能出现）
    private val bitmapIndex
        get() = if (selectedImageIndex > bitmapList.lastIndex) {
            0
        } else {
            selectedImageIndex
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        images = intent.getStringArrayListExtra(getString(R.string.intent_extra_selected_images)).orEmpty().toMutableList()
        selectNum = images.size
        initWithCoroutines()
    }

    /**
     * 从[ImageSelectActivity]返回后，获取更换图片路径，并进行加载和替换
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_REQUEST_CODE_REPLACE_IMAGE) {
            data?.getStringExtra(INTENT_EXTRA_DATA_REPLACE)
                ?.let {
                    mainScope.launch {
                        playLoadingAnimation()
                        // 防止输入图片只有一张时，下标越界
                        selectedImageIndex = if (selectedImageIndex > images.lastIndex){
                                                0
                                            } else {
                                                selectedImageIndex
                                            }
                        images[selectedImageIndex] = it
                        val changeBitmap = decodeBitmap(it)
                        bitmapList[selectedImageIndex] = changeBitmap
//                        selectedImageView.setImageBitmap(changeBitmap)
                        puzzleLayout.initViews(bitmapList,puzzleLayout.template.imageCount)
                        pauseLoadingAnimation()
                        selectedImageView.tag = true
//                        imageUpdateGroup.closeImageUpdateImageView.performClick()
//                        selectedImageIndex = -1
                    }
                }
        }
    }

    /**
     * 开启协程，加载数据，并初始化界面
     */
    private fun initWithCoroutines() {
        loadingAnimateView.repeatCount = -1
        mainScope.launch {
            playLoadingAnimation()
            loadTemplateData()
            initViews()
            val bitmaps = decodeBitmaps()
            puzzleLayout.template = allTemplates[0]
            puzzleLayout.initViews(bitmaps, allTemplates[0].imageCount)
            puzzleLayout.onHideUtilsListener = {
                if (showUpdateGroup) {
                    imageUpdateGroup.closeClickImageView.performClick()
                    selectedImageIndex = -1
                }
                if (showTemplateGroup) {
                    showImageView.performClick()
                }
            }
            puzzleContainer.post {
                resizePuzzleLayout()
                puzzleViewInit = true
            }
            pauseLoadingAnimation()
        }
    }

    /**
     * 加载输入图片数量为[selectNum]的模板数据
     */
    private suspend fun loadTemplateData() {
        template2CategoryMap.putAll(TemplateData.templateInCategory(selectNum, this))
        fistTemplateInCategoryMap.putAll(TemplateData.templateCategoryFirst(selectNum, this))
        allTemplates.addAll(TemplateData.allTemplateWithNum(selectNum, this))
    }

    /**
     * 根据屏幕宽高计算拼图的大小
     */
    private fun resizePuzzleLayout() {
        val containerWidth = puzzleContainer.width
        val containerHeight = puzzleContainer.height
        val templateWidth = puzzleLayout.template.totalWidth
        val templateHeight = puzzleLayout.template.totalHeight
        var finalWidth = containerWidth
        var finalHeight =
            (templateHeight * (containerWidth / templateWidth.toDouble())).roundToInt()
        if (finalHeight > containerHeight) {
            finalHeight = containerHeight
            finalWidth =
                (templateWidth * (containerHeight / templateHeight.toDouble())).roundToInt()
        }
        puzzleLayout.proportion = finalHeight / templateHeight.toDouble()
        puzzleLayout.layoutParams =
            FrameLayout.LayoutParams(finalWidth, finalHeight, Gravity.CENTER)
    }

    /**
     * 使用[Glide] 加载 [images] 中的图片路径，并添加到 [bitmapList] 中。
     */
    private suspend fun decodeBitmaps() = withContext(Dispatchers.IO) {
        val bitmaps = mutableListOf<Bitmap>()
        images.forEach {
            val decodeBitmap: Bitmap = Glide.with(this@MainActivity)
                .asBitmap()
                .override(1440)
                .load("file://$it")
                .submit()
                .get()
            bitmaps.add(decodeBitmap)
        }
        bitmapList.clear()
        bitmapList.addAll(bitmaps)
//        PuzzleQualityManager.puzzleQuality.metric.input_suc = PuzzleMetric.
        bitmaps
    }

    /**
     * 使用 [Glide] 加载单张图片
     */
    private suspend fun decodeBitmap(path: String) = withContext(Dispatchers.IO) {
        Glide.with(this@MainActivity)
            .asBitmap()
            .override(1000)
            .load("file://$path")
            .submit()
            .get()
    }

    private suspend fun initViews() {
        initTitleBar()
        initTemplateViewGroup()
        initBottomTabLayout()
        initImageUpdateGroup()
    }

    private suspend fun initTemplateViewGroup() {
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
                frameIconSize.dp2px(),
                frameIconSize.dp2px()
            )
        }
        templateGroup.frameTextView.setCompoundDrawables(null, drawable, null, null)
        templateGroup.frameTextView.setOnClickListener {
            updateFrameMode()
        }
    }

    private fun initTemplateTabLayout() {
        templateGroup.templateTabLayout.apply {
            if (fistTemplateInCategoryMap[TemplateData.template34] != null) {
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_34))
            }
            if (fistTemplateInCategoryMap[TemplateData.template11] != null) {
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_11))
            }
            if (fistTemplateInCategoryMap[TemplateData.template43] != null) {
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_43))
            }
            if (fistTemplateInCategoryMap[TemplateData.template169] != null) {
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_169))
            }
            if (fistTemplateInCategoryMap[TemplateData.templateFull] != null) {
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_full))
            }
            if (fistTemplateInCategoryMap[TemplateData.templateMore] != null) {
                addTab(newTab().setIcon(R.drawable.meitu_puzzle_temp_others))
            }
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    shouldUpdateTabLayout = false
                    val categoryPos = (tab.position)
                    templateGroup.templateRecyclerView.smoothScrollToPosition(
                        fistTemplateInCategoryMap[categoryPos] ?: 0
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    private suspend fun initTemplateRecyclerView() {
        templateGroup.templateRecyclerView.adapter = TemplateAdapter(
            TemplateData.allTemplateThumbnailPathWithNum(selectNum, this)
        ) { adapter, holder ->
            if (!puzzleViewInit) {
                return@TemplateAdapter
            }
            templateGroup.templateTabLayout.setScrollPosition(
                template2CategoryMap[holder.adapterPosition] ?: 0,
                0f,
                false
            )
            adapter.currentSelectPos = holder.adapterPosition
            adapter.notifyItemChanged(adapter.currentSelectPos)
            adapter.notifyItemChanged(adapter.lastSelectedPos)
            puzzleLayout.template = allTemplates[holder.adapterPosition]
            puzzleLayout.initViews(
                bitmapList,
                allTemplates[holder.adapterPosition].imageCount
            )
            resizePuzzleLayout()
            puzzleLayout.requestLayout()
        }
        templateGroup.templateRecyclerView.layoutManager = templateRecyclerViewLayoutManager
        templateGroup.templateRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            if (shouldUpdateTabLayout) {
                val lastPos =
                    templateRecyclerViewLayoutManager.findLastVisibleItemPosition()
                val firstPos =
                    templateRecyclerViewLayoutManager.findFirstVisibleItemPosition()
                if (lastPos == allTemplates.size - 1) {
                    templateGroup.templateTabLayout.setScrollPosition(
                        templateGroup.templateTabLayout.tabCount - 1, 0F, false
                    )
                } else if (firstPos == 0) {
                    templateGroup.templateTabLayout.setScrollPosition(
                        firstPos, 0F, false
                    )
                }
            }
            shouldUpdateTabLayout = true
        }
    }

    private fun initTitleBar() {
        titleBar.backImageView.setOnClickListener {
            finish()
        }
        titleBar.finishImageView.setOnClickListener {
            puzzleLayout.clearAllImageViewSelectBorder()
            saveBitmap(puzzleLayout, System.currentTimeMillis().toString())
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
                    if (!showTemplateGroup) {
                        showImageView.performClick()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {
                    showImageView.performClick()
                }
            })
        }
    }

    private fun initImageUpdateGroup() {
        puzzleLayout.onImageClickListener = { viewIndex, view ->
            if (viewIndex != selectedImageIndex) {
                selectedImageView = view
                selectedImageIndex = viewIndex
                if (!showUpdateGroup) {
                    imageUpdateGroup.closeImageUpdateImageView.performClick()
                }
            } else {
                view.showBorder(false)
                selectedImageIndex = -1
                imageUpdateGroup.closeImageUpdateImageView.performClick()
            }
        }
        imageUpdateGroup.closeClickImageView.setOnClickListener {
            selectedImageView.performClick()
        }
        imageUpdateGroup.replaceImageView.setOnClickListener {
            startActivityForResult(Intent(this, ImageSelectActivity::class.java).apply {
                putExtra(INTENT_EXTRA_REPLACE, true)
            }, INTENT_REQUEST_CODE_REPLACE_IMAGE)
        }
        imageUpdateGroup.rotateImageView.setOnClickListener {
            val sourceBitmap = bitmapList[bitmapIndex]
            val matrix = Matrix()
            matrix.postRotate(rotateAngle)
            val rotateBitmap = Bitmap.createBitmap(
                sourceBitmap,
                0,
                0,
                sourceBitmap.width,
                sourceBitmap.height,
                matrix,
                true
            )
            bitmapList[bitmapIndex] = rotateBitmap
            selectedImageView.setImageBitmap(rotateBitmap)
            selectedImageView.fixTransformation()
        }
        imageUpdateGroup.rotateHorizontalImageView.setOnClickListener {
            val sourceBitmap = bitmapList[bitmapIndex]
            val matrix = Matrix()
            matrix.setScale(-1F, 1F)
            matrix.postTranslate(sourceBitmap.width.toFloat(), 0F)
            val rotateBitmap = Bitmap.createBitmap(
                sourceBitmap,
                0,
                0,
                sourceBitmap.width,
                sourceBitmap.height,
                matrix,
                true
            )
            bitmapList[bitmapIndex] = rotateBitmap
            selectedImageView.setImageBitmap(rotateBitmap)
            selectedImageView.fixTransformation()
        }
        imageUpdateGroup.rotateVerticalImageView.setOnClickListener {
            val sourceBitmap = bitmapList[bitmapIndex]
            val matrix = Matrix()
            matrix.setScale(1F, -1F)
            matrix.postTranslate(0F, sourceBitmap.height.toFloat())
            val rotateBitmap = Bitmap.createBitmap(
                sourceBitmap,
                0,
                0,
                sourceBitmap.width,
                sourceBitmap.height,
                matrix,
                true
            )
            bitmapList[bitmapIndex] = rotateBitmap
            selectedImageView.setImageBitmap(rotateBitmap)
            selectedImageView.fixTransformation()
        }
    }

    private fun saveBitmap(view: View, fileName: String) {
        mainScope.launch {
            playLoadingAnimation()
            val bitmap = view2bitmap(view)
            val savedUri = saveLocal(fileName, bitmap)
            if (!TextUtils.isEmpty(savedUri.toString())) {
                startActivity(Intent(this@MainActivity, SuccessActivity::class.java).apply {
                    putExtra(getString(R.string.intent_extra_saved_uri), savedUri)
                })
            } else {
                showToast(getString(R.string.saved_failed))
            }
            pauseLoadingAnimation()
        }
    }

    /**
     * 播放加载动画
     */
    private fun playLoadingAnimation() {
        loadingAnimateView.alpha = 1F
        loadingAnimateView.playAnimation()
    }

    /**
     * 取消加载动画
     */
    private fun pauseLoadingAnimation() {
        loadingAnimateView.pauseAnimation()
        loadingAnimateView.alpha = 0F
    }

    /**
     * 将 [PuzzleLayout] 转换为 bitmap，用于图片保存
     */
    private fun view2bitmap(view: View): Bitmap {
        val height = view.height
        val width = view.width
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    /**
     * 将拼图保存到本地。
     * Android Q 版本以上插入[MediaStore]后再保存，否则先保存到本地，再发送广播通知图库更新
     *
     */
    private suspend fun saveLocal(fileName: String, bitmap: Bitmap): Uri =
        withContext(Dispatchers.IO) {
            var imagePath: Uri = Uri.parse("")
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues()
                    contentValues.put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        fileName
                    )
                    contentValues.put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        getString(R.string.dcim)
                    )
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
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        imagePath = uri
                    }
                } else {
                    val dir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                    )
                    val imageFile = File(dir, "$fileName.jpg")
                    val outputStream = imageFile.outputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    imagePath = Uri.parse(imageFile.path)
                    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imagePath))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imagePath
        }

    /**
     * 边框模式的更新
     */
    private fun updateFrameMode() {
        currentFrameMode = when (currentFrameMode.first) {
            R.drawable.meitu_puzzle__frame_none -> {
                puzzleLayout.updateFrameSize(PuzzleLayout.FRAME_SMALL)
                R.drawable.meitu_puzzle__frame_small to getString(R.string.small_frame)
            }
            R.drawable.meitu_puzzle__frame_small -> {
                puzzleLayout.updateFrameSize(PuzzleLayout.FRAME_MEDIUM)
                R.drawable.meitu_puzzle__frame_medium to getString(R.string.medium_frame)
            }
            R.drawable.meitu_puzzle__frame_medium -> {
                puzzleLayout.updateFrameSize(PuzzleLayout.FRAME_LARGE)
                R.drawable.meitu_puzzle__frame_large to getString(R.string.large_frame)
            }
            else -> {
                puzzleLayout.updateFrameSize(PuzzleLayout.FRAME_NONE)
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
                frameIconSize.dp2px(),
                frameIconSize.dp2px()
            )
        }
        templateGroup.frameTextView.setCompoundDrawables(null, drawable, null, null)
    }
}