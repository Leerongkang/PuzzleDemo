package com.lrk.puzzle.demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.lrk.puzzle.demo.adappter.TemplateAdapter
import com.lrk.puzzle.demo.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX

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
            binding.templateView.templateTabLayout.setScrollPosition(
                template2CategoryMap[it.adapterPosition] ?: 0,
                0f,
                false
            )
            select = it.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        images = intent.getStringArrayListExtra("images") ?: emptyList()
        selectNum = images.size
        Toast.makeText(this, "$selectNum", Toast.LENGTH_LONG).show()
        initViews()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initViews() {
        binding.apply {
            templateView.apply {
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
            }
            bottomTabLayout.apply {
                addTab(newTab().setText("模板"))
                addTab(newTab().setText("海报"))
                addTab(newTab().setText("自由"))
                addTab(newTab().setText("拼接"))
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        closeImageView.performClick()
                    }
                })
            }
            titleBar.backImageView.setOnClickListener {
//                startActivity(Intent(this@MainActivity,ImageSelectActivity::class.java))
                finish()
            }
            closeImageView.apply {
                setOnClickListener {
                    Log.e("kkl","ttt")
                    showTemplate = !showTemplate
                    val res =  if (showTemplate) {
                        R.drawable.ic_down
                    } else {
                        R.drawable.ic_up
                    }
                    setImageResource(res)
                }
            }
        }
    }

    private fun updateFrameMode() {
        currentFrameMode = when (currentFrameMode.first) {
            R.drawable.meitu_puzzle__frame_none -> R.drawable.meitu_puzzle__frame_small to "小边框"
            R.drawable.meitu_puzzle__frame_small -> R.drawable.meitu_puzzle__frame_medium to "中边框"
            R.drawable.meitu_puzzle__frame_medium -> R.drawable.meitu_puzzle__frame_large to "大边框"
            else -> R.drawable.meitu_puzzle__frame_none to "无边框"
        }
        binding.templateView.frameTextView.apply {
            text = currentFrameMode.second
            setCompoundDrawables(
                null,
                ContextCompat.getDrawable(this@MainActivity, currentFrameMode.first)?.apply {
                    setBounds(0, 0, dp2px(40), dp2px(40))
                }, null, null
            )
        }
    }

    private fun dp2px(dp: Int) = (dp * resources.displayMetrics.density + 0.5f).toInt()


}