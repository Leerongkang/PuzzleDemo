package com.lrk.puzzle.demo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.meitu.puzzle.R
import com.meitu.puzzle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var shouldUpdateTabLayout = false
    var currentFrameMode = R.drawable.meitu_puzzle__frame_none to "无边框"

    val templateRecyclerViweLayoutManager = LinearLayoutManager(this).apply {
        orientation = LinearLayoutManager.HORIZONTAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initViews() {
        binding.apply {
            templateView.apply {
                templateRecyclerView.apply {
                    adapter = TemplateAdapter(this@MainActivity,
                        TemplateData.allTemplateWithPictureNum(1)
                    ).apply {
                        setOnTemplateSelectListener {
                            binding.templateView.templateTabLayout.setScrollPosition(
                                it.adapterPosition / 2,
                                0f,
                                false
                            )
                            select = it.adapterPosition
                            notifyDataSetChanged()
                        }
                    }
                    layoutManager = templateRecyclerViweLayoutManager
                    setOnScrollChangeListener(object : View.OnScrollChangeListener {
                        override fun onScrollChange(
                            v: View?,
                            scrollX: Int,
                            scrollY: Int,
                            oldScrollX: Int,
                            oldScrollY: Int
                        ) {
                            if (shouldUpdateTabLayout) {
                                val lastPos =
                                    templateRecyclerViweLayoutManager.findLastVisibleItemPosition()
                                val pos = if (lastPos == 12) {
                                    5
                                } else {
                                    templateRecyclerViweLayoutManager.findFirstVisibleItemPosition() / 2
                                }
                                templateTabLayout.setScrollPosition(
                                    pos, 0F, false
                                )
                            }
                            shouldUpdateTabLayout = true
                        }
                    })
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
                            templateRecyclerViweLayoutManager.scrollToPositionWithOffset(
                                (tab?.position ?: 0) * 2,
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