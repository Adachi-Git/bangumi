package com.xiaoyv.common.widget.grid

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.common.R
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.widget.scroll.AnimeRecyclerView

/**
 * Class: [EpGridView]
 *
 * @author why
 * @since 12/22/23
 */
class EpGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AnimeRecyclerView(context, attrs) {
    private var autoScrollWatched = true
    private val gridHorAdapter by lazy { EpGridHorItemAdapter() }
    private val gridVerAdapter by lazy { EpGridVerItemAdapter() }

    private val horizontalManager by lazy {
        GridLayoutManager(context, SPAN_COUNT_HORIZONTAL, LinearLayoutManager.HORIZONTAL, false)
    }

    private val verticalManager by lazy {
        GridLayoutManager(context, SPAN_COUNT_VERTICAL, LinearLayoutManager.VERTICAL, false)
    }

    init {
        hasFixedSize()
        itemAnimator = null
        if (isInEditMode) {
            layoutManager = horizontalManager
        }
    }

    /**
     * 设置格子数据
     */
    fun fillMediaChapters(
        list: List<ApiUserEpEntity>,
        listener: BaseQuickAdapter.OnItemChildClickListener<ApiUserEpEntity>,
    ) {
        isVisible = true

        if (isHorizontalGrid(list.size)) {
            layoutManager = horizontalManager
            if (adapter !is EpGridHorItemAdapter) adapter = gridHorAdapter
            gridHorAdapter.addOnItemChildClickListener(R.id.item_ep, listener)
            gridHorAdapter.submitList(list) {
                if (autoScrollWatched) {
                    scrollToWatched()

                    // 有效滚动后才标记
                    if (list.isNotEmpty()) autoScrollWatched = false
                }
            }
        } else {
            layoutManager = verticalManager
            if (adapter !is EpGridVerItemAdapter) adapter = gridVerAdapter
            gridVerAdapter.addOnItemChildClickListener(R.id.item_ep, listener)
            gridVerAdapter.submitList(list)
        }
    }

    /**
     * 滑动到第一个不是看过的格子
     */
    fun scrollToWatched() {
        val targetIndex = gridHorAdapter.items.indexOfLast {
            it.type == EpCollectType.TYPE_COLLECT
        }

        if (targetIndex != -1) {
            horizontalManager.scrollToPositionWithOffset(
                targetIndex, ScreenUtils.getScreenWidth() / 3
            )
        }
    }

    companion object {
        const val SPAN_COUNT_HORIZONTAL = 5
        const val SPAN_COUNT_VERTICAL = 8

        fun isHorizontalGrid(size: Int): Boolean {
            return size > SPAN_COUNT_VERTICAL * SPAN_COUNT_HORIZONTAL
        }
    }
}