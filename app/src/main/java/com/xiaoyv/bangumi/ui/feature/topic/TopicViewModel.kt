package com.xiaoyv.bangumi.ui.feature.topic

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.TopicDetailEntity
import com.xiaoyv.common.api.parser.impl.parserTopic
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.kts.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [TopicViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class TopicViewModel : BaseViewModel() {
    internal var topicId: String = ""

    @TopicType
    internal var topicType: String = TopicType.TYPE_EP

    internal val onTopicDetailLiveData = MutableLiveData<TopicDetailEntity?>()

    fun queryTopicDetail() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onTopicDetailLiveData.value = null
            },
            block = {
                onTopicDetailLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryTopicDetail(topicId, topicType)
                }.parserTopic(topicId)
            }
        )
    }
}