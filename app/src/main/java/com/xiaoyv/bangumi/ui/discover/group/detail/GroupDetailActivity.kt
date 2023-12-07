package com.xiaoyv.bangumi.ui.discover.group.detail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityGroupDetailBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener

/**
 * Class: [GroupDetailActivity]
 *
 * @author why
 * @since 12/7/23
 */
class GroupDetailActivity :
    BaseViewModelActivity<ActivityGroupDetailBinding, GroupDetailViewModel>() {
    private val recentlyAdapter by lazy { GroupDetailAdapter() }
    private val otherAdapter by lazy { GroupDetailAdapter() }
    private val viewPool by lazy { RecycledViewPool() }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.groupId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)

        binding.rvGrid.setRecycledViewPool(viewPool)
        binding.rvOther.setRecycledViewPool(viewPool)
    }

    override fun initData() {
        binding.toolbar.title = String.format("Group: %s", viewModel.groupId)

        binding.sectionRecently.title = "最近加入"
        binding.sectionRecently.more = null
        binding.sectionOther.title = "相关的小组"
        binding.sectionOther.more = null

        binding.rvOther.adapter = otherAdapter
        binding.rvGrid.adapter = recentlyAdapter
    }

    override fun initListener() {
        recentlyAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.id)
        }
        
        otherAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpGroupDetail(it.id)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(this, viewModel.loadingViewState)

        viewModel.onGroupDetailLiveData.observe(this) {
            val entity = it ?: return@observe
            binding.ivBanner.loadImageBlur(entity.avatar)
            binding.ivAvatar.loadImageAnimate(entity.avatar)
            binding.tvName.text = entity.name
            binding.tvDesc.text = entity.id
            binding.toolbar.title = entity.name
            binding.tvTime.text = entity.time
            binding.tvSummary.text = entity.summaryText

            recentlyAdapter.submitList(entity.recently)
            otherAdapter.submitList(entity.otherGroups)

            binding.clContainer.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}