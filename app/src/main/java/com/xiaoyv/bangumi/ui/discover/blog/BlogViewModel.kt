package com.xiaoyv.bangumi.ui.discover.blog

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.loadState.LoadState
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BlogEntity
import com.xiaoyv.common.api.parser.impl.parserBlogList
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.widget.kts.copyAddAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [BlogViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class BlogViewModel : BaseViewModel() {
    internal val onListLiveData = MutableLiveData<List<BlogEntity>?>()

    internal var userId = ""

    /**
     * 搜索条件
     */
    private var current = 1

    @MediaType
    internal var mediaType: String = MediaType.TYPE_ANIME

    internal var tag = ""

    internal var loadingMoreState: LoadState = LoadState.None

    /**
     * 默认日志查询路径为 媒体的动漫
     */
    private var queryPath = MediaType.TYPE_ANIME

    /**
     * 日志拼接路径
     */
    private var tagPath = ""

    internal val isRefresh: Boolean
        get() = current == 1

    fun refresh() {
        current = 1
        queryBlogList()
    }

    fun loadMore() {
        current++
        queryBlogList()
    }

    private fun queryBlogList() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onListLiveData.value = null
            },
            block = {
                buildQueryTagPath()

                val response = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryBlogList(
                        queryPath = queryPath,
                        tagPath = tagPath,
                        page = current
                    ).parserBlogList(mediaType)
                }

                if (isRefresh) {
                    onListLiveData.value = response
                } else {
                    onListLiveData.value = onListLiveData.value.copyAddAll(response)
                }

                loadingMoreState = if (isRefresh && response.isEmpty()) {
                    LoadState.None
                } else {
                    LoadState.NotLoading(response.isEmpty())
                }
            }
        )
    }

    /**
     * 构建查询和标签路径
     */
    private fun buildQueryTagPath() {
        // 直接拼接用户ID或媒体类型的字符串
        queryPath = if (userId.isNotBlank()) "user/$userId" else mediaType

        // TAG 路径
        tagPath = if (tag.isNotBlank()) "/tag/$tag" else ""
    }
}